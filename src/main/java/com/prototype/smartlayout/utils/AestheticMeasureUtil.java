package com.prototype.smartlayout.utils;

import com.prototype.smartlayout.SmartLayout;
import com.prototype.smartlayout.aesthetic.ComponentData;
import com.prototype.smartlayout.aesthetic.QuadrantData;
import com.prototype.smartlayout.aesthetic.QuadrantEnum;
import com.prototype.smartlayout.model.Coordinate;
import com.prototype.smartlayout.model.LayoutComponent;
import com.prototype.smartlayout.model.LayoutContainer;
import com.prototype.smartlayout.model.Layoutable;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.math3.special.Gamma;

/**
 * These measures does not include color shape etc.
 * formulas gained from <site>http://www.mi.sanu.ac.rs/vismath/ngo/index.html</site>
 */
@Log4j2
public class AestheticMeasureUtil
{
	public static AestheticMeasureUtil aestheticMeasure;
	
	private float[] coefficients = {
			1, //c_balance
			1, //c_equilibrium
			1, //c_symmetry
			1, //c_sequence
			0, //c_cohesion
			0, //c_unity
			1, //c_proportion
			0, //c_simplicity
			0, //c_density
			0, //c_regularity
			1, //c_economy
			1, //c_homogeneity
			1, //c_rhythm
	};
	
	private HashMap<LayoutComponent, ComponentData> componentDataMap;
	private ArrayList<ComponentData> datas;
	private float[] maxAreas;
	
	private int componentSize;
	
	private int frameWidth;
	private int frameHeight;
	private int frameCenterX;
	private int frameCenterY;
	
	private int layoutWidth;
	private int layoutHeight;
	
	public static float MeasureAesthetics(int frameWidth, int frameHeight, int layoutWidth, int layoutHeight)
	{
		//return 1;
		
		aestheticMeasure = new AestheticMeasureUtil();
		
		aestheticMeasure.componentSize = SmartLayout.components.size();
		
		aestheticMeasure.frameWidth = frameWidth;
		aestheticMeasure.frameHeight = frameHeight;
		aestheticMeasure.frameCenterX = frameWidth / 2;
		aestheticMeasure.frameCenterY = frameHeight / 2;
		
		aestheticMeasure.layoutWidth = layoutWidth;
		aestheticMeasure.layoutHeight = layoutHeight;
		
		aestheticMeasure.CalculateComponentAreas();
		
		float m_balance = aestheticMeasure.MeasureBalance();
		float m_equilibrium = aestheticMeasure.MeasureEquilibrium();
		float m_symmetry = aestheticMeasure.MeasureSymmetry();
		float m_sequence = aestheticMeasure.MeasureSequence();
		float m_cohesion = aestheticMeasure.MeasureCohesion();
		float m_unity = aestheticMeasure.MeasureUnity();
		float m_proportion = aestheticMeasure.MeasureProportion();
		float m_simplicity = aestheticMeasure.MeasureSimplicity();
		float m_density = aestheticMeasure.MeasureDensity();
		float m_regularity = aestheticMeasure.MeasureRegularity();
		float m_economy = aestheticMeasure.MeasureEconomy();
		float m_homogeneity = aestheticMeasure.MeasureHomogeneity();
		float m_rhythm = aestheticMeasure.MeasureRhythm();
		
		float[] measurements = {m_balance, m_equilibrium, m_symmetry, m_sequence, m_cohesion, m_unity,
				m_proportion, m_simplicity, m_density, m_regularity, m_economy, m_homogeneity, m_rhythm};
		
		float m_orderAndComplexity = aestheticMeasure.MeasureOrderAndComplexity(measurements, aestheticMeasure.coefficients);
		
		return m_orderAndComplexity;
	}
	
	private float MeasureBalance()
	{
		float balance;
		float bm_horizontal, bm_vertical;
		
		float w_left, w_right, w_top, w_bottom;
		w_left = w_right = w_top = w_bottom = 0;
		
		for(ComponentData data: datas)
		{
			w_left += data.getLeftDistance() * (data.getLeftArea() / maxAreas[0]);
			w_right += data.getRightDistance() * (data.getRightArea() / maxAreas[1]);
			w_top += data.getTopDistance() * (data.getTopArea() / maxAreas[2]);
			w_bottom += data.getBottomDistance() * (data.getBottomArea() / maxAreas[3]);
		}
		
		bm_horizontal = (w_top - w_bottom) / Math.max(Math.abs(w_top), Math.abs(w_bottom));
		bm_vertical = (w_left - w_right) / Math.max(Math.abs(w_left), Math.abs(w_right));
		
		balance = 1 - (Math.abs(bm_vertical) + Math.abs(bm_horizontal)) / 2;
		
		//System.out.println("Balance: " + balance);
		
		return balance;
	}
	
	private float MeasureEquilibrium()
	{
		float equilibrium;
		float em_x, em_y;
		float totalComponentArea, centerFactorX, centerFactorY;
		totalComponentArea = centerFactorX = centerFactorY = 0;
		
		for(ComponentData data: datas)
		{
			totalComponentArea += data.getTotalArea();
			centerFactorX += data.getTotalArea() * (data.getCenterX() - frameCenterX);
			centerFactorY += data.getTotalArea() * (data.getCenterY() - frameCenterY);
		}
		
		em_x = (2 * centerFactorX) / (componentSize * frameWidth * totalComponentArea);
		em_y = (2 * centerFactorY) / (componentSize * frameHeight * totalComponentArea);
		
		equilibrium = 1 - (Math.abs(em_x) + Math.abs(em_y)) / 2;
		
		//System.out.println("Equilibrium: " + equilibrium);
		
		return equilibrium;
	}
	
	private float MeasureSymmetry()
	{
		float symmetry;
		float sym_v, sym_h, sym_r;
		float[] x, y, b, h, t, r;
		float[] x_n, y_n, b_n, h_n, t_n, r_n;
		
		x = new float[4]; y = new float[4];
		b = new float[4]; h = new float[4];
		t = new float[4]; r = new float[4];
		
		for(ComponentData data: datas)
		{
			//ComponentData data = componentDataMap.get(component);
			
			for(QuadrantData quadrantData: data.getQuadrants())
			{
				int quadrantIndex;
				
				switch(quadrantData.getQuadrantPosition())
				{
				case UL:
					quadrantIndex = 0;
					
					break;
					
				case UR:
					quadrantIndex = 1;
					
					break;
					
				case LL:
					quadrantIndex = 2;

					break;
					
				case LR:
					quadrantIndex = 3;

					break;
					
				default:
					System.out.println("Error in symmetry measurement.");
					
					return -1;
				}
				
				float centerDifferenceX = quadrantData.getCenterX() - frameCenterX;
				float centerDifferenceY = quadrantData.getCenterY() - frameCenterY;
				
				x[quadrantIndex] += Math.abs(centerDifferenceX);
				y[quadrantIndex] += Math.abs(centerDifferenceY);
				h[quadrantIndex] += quadrantData.getHeight();
				b[quadrantIndex] += quadrantData.getWidth();
				t[quadrantIndex] += Math.abs(centerDifferenceY / centerDifferenceX);
				r[quadrantIndex] += Math.sqrt(Math.pow(centerDifferenceX, 2) + Math.pow(centerDifferenceY, 2));
			}
		}

		DecimalFormat df = new DecimalFormat("###.###");
		df.setRoundingMode(RoundingMode.CEILING);
		
		for(int i = 0; i < 4; i++)
		{
			x[i] = Float.parseFloat(df.format(x[i]).replace(",", "."));
			y[i] = Float.parseFloat(df.format(y[i]).replace(",", "."));
			h[i] = Float.parseFloat(df.format(h[i]).replace(",", "."));
			b[i] = Float.parseFloat(df.format(b[i]).replace(",", "."));
			t[i] = Float.parseFloat(df.format(t[i]).replace(",", "."));
			r[i] = Float.parseFloat(df.format(r[i]).replace(",", "."));
		}
		
		x_n = Normalize(x);
		y_n = Normalize(y);
		h_n = Normalize(h);
		b_n = Normalize(b);
		t_n = Normalize(t);
		r_n = Normalize(r);
		
		sym_v = (Math.abs(x_n[0] - x_n[1]) + Math.abs(x_n[2] - x_n[3]) + Math.abs(y_n[0] - y_n[1]) + Math.abs(y_n[2] - y_n[3])
			+ Math.abs(h_n[0] - h_n[1]) + Math.abs(h_n[2] - h_n[3]) + Math.abs(b_n[0] - b_n[1]) + Math.abs(b_n[2] - b_n[3])
			+ Math.abs(t_n[0] - t_n[1]) + Math.abs(t_n[2] - t_n[3]) + Math.abs(r_n[0] - r_n[1]) + Math.abs(r_n[2] - r_n[3])) / 12;
		
		sym_h = (Math.abs(x_n[0] - x_n[2]) + Math.abs(x_n[1] - x_n[3]) + Math.abs(y_n[0] - y_n[2]) + Math.abs(y_n[1] - y_n[3])
			+ Math.abs(h_n[0] - h_n[2]) + Math.abs(h_n[1] - h_n[3]) + Math.abs(b_n[0] - b_n[2]) + Math.abs(b_n[1] - b_n[3])
			+ Math.abs(t_n[0] - t_n[2]) + Math.abs(t_n[1] - t_n[3]) + Math.abs(r_n[0] - r_n[2]) + Math.abs(r_n[1] - r_n[3])) / 12;
		
		sym_r = (Math.abs(x_n[0] - x_n[3]) + Math.abs(x_n[1] - x_n[2]) + Math.abs(y_n[0] - y_n[3]) + Math.abs(y_n[1] - y_n[2])
			+ Math.abs(h_n[0] - h_n[3]) + Math.abs(h_n[1] - h_n[2]) + Math.abs(b_n[0] - b_n[3]) + Math.abs(b_n[1] - b_n[2])
			+ Math.abs(t_n[0] - t_n[3]) + Math.abs(t_n[1] - t_n[2]) + Math.abs(r_n[0] - r_n[3]) + Math.abs(r_n[1] - r_n[2])) / 12;
		
		symmetry = 1 - (Math.abs(sym_v) + Math.abs(sym_h) + Math.abs(sym_r)) / 3;
		
		//System.out.println("Symmetry: " + symmetry);
		
		return symmetry;
	}
	
	private float MeasureSequence()
	{
		float sequence;
		int[] q = {4, 3, 2, 1};
		int[] v = new int[4];
		float[] w = new float[4];
		
		for(ComponentData data: datas)
		{
			//ComponentData data = componentDataMap.get(component);
			
			for(QuadrantData quadrantData: data.getQuadrants())
			{
				int quadrantIndex;
				
				switch(quadrantData.getQuadrantPosition())
				{
				case UL:
					quadrantIndex = 0;
					
					break;
					
				case UR:
					quadrantIndex = 1;
					
					break;
					
				case LL:
					quadrantIndex = 2;

					break;
					
				case LR:
					quadrantIndex = 3;

					break;
					
				default:
					System.out.println("Error in sequence measurement.");
					
					return -1;
				}
				
				w[quadrantIndex] += quadrantData.getArea();
			}
		}
		
		for(int i = 0; i < w.length; i++)
			w[i] *= q[i];
		
		List<Float> w_sorted = Arrays.asList(w[0], w[1], w[2], w[3]);
		Collections.sort(w_sorted);
		
		for(int i = 0; i < v.length; i++)
			v[i] = w_sorted.indexOf(w[i]) + 1;
			
		float subTotal = 0;
		
		for(int i = 0; i < 4; i++)
			subTotal += Math.abs(q[i] - v[i]);
		
		sequence = 1 - subTotal / 8;
		
		//System.out.println("Sequence: " + sequence);
		
		return sequence;
	}
	
	private float MeasureCohesion()
	{
		float cohesion;
		float cm_fl, cm_fo, cm_lo;
		float f_fo, f_lo;
		float t_fl;
		int n = SmartLayout.components.size();
		
		float frameRate, layoutRate;
		float width, height;
		
		width = frameWidth;
		height = frameHeight;
		frameRate = height / width;
		
		width = layoutWidth;
		height = layoutHeight;
		layoutRate = height / width;
		
		t_fl = layoutRate / frameRate;
		
		float f_fo_SubTotal = 0;
		float f_lo_SubTotal = 0;

		for(LayoutComponent component: SmartLayout.components)
		{
			float t_fo, t_lo;
			width = component.getAssignedWidth();
			height = component.getAssignedHeight();
			float objectRate = height / width;
			
			t_fo = objectRate / frameRate;
			t_lo = objectRate / layoutRate;
			
			if(t_fo <= 1)
				f_fo = t_fo;
			else
				f_fo = 1 / t_fo;
			
			if(t_lo <= 1)
				f_lo = t_lo;
			else
				f_lo = 1 / t_lo;
			
			f_fo_SubTotal += f_fo;
			f_lo_SubTotal += f_lo;
		}
		
		cm_fo = f_fo_SubTotal / n;
		cm_lo = f_lo_SubTotal / n;
		
		if(t_fl <= 1)
			cm_fl = t_fl;
		else
			cm_fl = 1 / t_fl;
		
		cohesion = (Math.abs(cm_fl) + Math.abs(cm_fo) + Math.abs(cm_lo)) / 3;
		
		//System.out.println("Cohesion: " + cohesion);
		
		return cohesion;
	}
	
	private float MeasureUnity()
	{
		float unity;
		float um_form, um_space;
		
		Vector<Coordinate> sizes = new Vector<Coordinate>();
		
		for(LayoutComponent component: SmartLayout.components)
		{
			Coordinate size = new Coordinate(component.getAssignedWidth(), component.getAssignedHeight());
			sizes.add(size);
		}
		
		Collections.sort(sizes);
		
		int sizeCount = 1;
		int n = SmartLayout.components.size();
		
		for(int i = 0; i < sizes.size() - 1; i++)
		{
			if(sizes.get(i).getX() != sizes.get(i + 1).getX())
			{
				sizeCount++;
			}
			else
			{
				if(sizes.get(i).getY() != sizes.get(i + 1).getY())
					sizeCount++;
			}
		}
		
		um_form = 1 - (sizeCount - 1) / n;
		
		float totalAreas = 0;
		
		for(ComponentData data: datas)
			totalAreas += data.getTotalArea();
		
		if(frameWidth * frameHeight == totalAreas)
			um_space = 1;
		else
			um_space = 1 - ((layoutWidth * layoutHeight - totalAreas) / (frameWidth * frameHeight - totalAreas));
		
		unity = (Math.abs(um_form) + Math.abs(um_space)) / 2;
		
		//System.out.println("Unity: " + unity);
		
		return unity;
	}
	
	private float MeasureProportion()
	{
		float proportion;
		float pm_object, pm_layout;
		float p_layout, r_layout;
		
		float[] p_j = {1f, 1/1.414f, 1/1.618f, 1/1.732f, 1/2f};
		
		r_layout = (float) layoutHeight / layoutWidth;
		p_layout = r_layout <= 1 ? r_layout : 1 / r_layout;
		
		float min_p = Float.POSITIVE_INFINITY;
		
		for(int i = 0; i < 5; i++)
		{
			float p = Math.abs(p_j[i] - p_layout);
			
			if(p < min_p)
				min_p = p;
		}
		
		pm_layout = 1 - min_p / 0.5f;
		
		float tempObjectTotal = 0;
		
		for(LayoutComponent component: SmartLayout.components)
		{
			float r_i = (float) component.getAssignedHeight() / component.getAssignedWidth();
			float p_i = r_i <= 1 ? r_i : 1 / r_i;
			
			min_p = Float.POSITIVE_INFINITY;
			
			for(int i = 0; i < 5; i++)
			{
				float p = Math.abs(p_j[i] - p_i);
				
				if(p < min_p)
					min_p = p;
			}
			
			tempObjectTotal += 1 - min_p / 0.5f;
		}
		
		int n = SmartLayout.components.size();
		pm_object = tempObjectTotal / n;
		
		proportion = (Math.abs(pm_layout) + Math.abs(pm_object)) / 2;
		
		//System.out.println("Proportion: " + proportion);
		
		return proportion;
	}

	private float MeasureSimplicity()
	{
		float simplicity;
		float n_vap, n_hap;
		float n = SmartLayout.components.size();
		
		Vector<Integer> allVaps, allHaps;
		allVaps = new Vector<Integer>();
		allHaps = new Vector<Integer>();
		
		for(LayoutComponent component: SmartLayout.components)
		{
			allVaps.add(component.getAssignedY());
			allVaps.add(component.getAssignedY() + component.getAssignedHeight());
			allHaps.add(component.getAssignedX());
			allHaps.add(component.getAssignedX() + component.getAssignedWidth());
		}
		
		Collections.sort(allVaps);
		Collections.sort(allHaps);
		
		n_vap = n_hap = 1;
		
		for(int i = 0; i < allVaps.size() - 1; i ++)
		{
			if(allVaps.get(i) != allVaps.get(i + 1))
				n_vap++;
		}
		
		for(int i = 0; i < allHaps.size() - 1; i ++)
		{
			if(allHaps.get(i) != allHaps.get(i + 1))
				n_hap++;
		}
		
		simplicity = 3 / (n_vap + n_hap + n);
		
		//System.out.println("Simplicity: " + simplicity);
		
		return simplicity;
	}

	private float MeasureDensity()
	{
		float density;
		float totalAreas = 0;
		
		for(ComponentData data: datas)
			totalAreas += data.getTotalArea();
		
		density = 1 - totalAreas / (layoutWidth * layoutHeight);
		
		//System.out.println("Density: " + density);
		
		return density;
	}

	private float MeasureRegularity()
	{
		float regularity;
		float rm_alignment, rm_spacing;
		
		float n = SmartLayout.components.size();
		float n_vap, n_hap, n_spacing;
		
		Vector<Integer> allVaps, allHaps, allSpacings;
		allVaps = new Vector<Integer>();
		allHaps = new Vector<Integer>();
		allSpacings = new Vector<Integer>();
		
		for(LayoutComponent component: SmartLayout.components)
		{
			allVaps.add(component.getAssignedY());
			allVaps.add(component.getAssignedY() + component.getAssignedHeight());
			allHaps.add(component.getAssignedX());
			allHaps.add(component.getAssignedX() + component.getAssignedWidth());
		}
		
		Collections.sort(allVaps);
		Collections.sort(allHaps);
		
		n_vap = n_hap = n_spacing = 1;
		
		for(int i = 0; i < allVaps.size() - 1; i ++)
		{
			if(allVaps.get(i) != allVaps.get(i + 1))
				n_vap++;
			
			allSpacings.add(allVaps.get(i + 1) - allVaps.get(i));
		}
		
		for(int i = 0; i < allHaps.size() - 1; i ++)
		{
			if(allHaps.get(i) != allHaps.get(i + 1))
				n_hap++;
			
			allSpacings.add(allHaps.get(i + 1) - allHaps.get(i));
		}
		
		Collections.sort(allSpacings);
		
		for(int i = 0; i < allSpacings.size() - 1; i ++)
		{
			if(allSpacings.get(i) != allSpacings.get(i + 1))
				n_spacing++;
		}
		
		rm_alignment = 1 - (n_vap + n_hap) / (2 * n);
		rm_spacing = n != 1 ? 1 - (n_spacing - 1) / (2 * (n - 1)) : 1;
		
		regularity = (Math.abs(rm_alignment) + Math.abs(rm_spacing)) / 2;
		
		//System.out.println("Regularity: " + regularity);
		
		return regularity;
	}

	private float MeasureEconomy()
	{
		float economy;
		
		Vector<Coordinate> sizes = new Vector<Coordinate>();
		
		for(LayoutComponent component: SmartLayout.components)
		{
			Coordinate size = new Coordinate(component.getAssignedWidth(), component.getAssignedHeight());
			sizes.add(size);
		}
		
		Collections.sort(sizes);
		
		int sizeCount = 1;
		
		for(int i = 0; i < sizes.size() - 1; i++)
		{
			if(sizes.get(i).getX() != sizes.get(i + 1).getX())
			{
				sizeCount++;
			}
			else
			{
				if(sizes.get(i).getY() != sizes.get(i + 1).getY())
					sizeCount++;
			}
		}
		
		economy = 1 / sizeCount;
		
		//System.out.println("Economy: " + economy);
		
		return economy;
	}

	private float MeasureHomogeneity()
	{
		float homogeneity;
		float w, w_max;
		float n, n_ul, n_ur, n_ll, n_lr;
		
		n_ul = n_ur = n_ll = n_lr = 0;
		
		for(ComponentData data: datas)
		{
			for(QuadrantData qd: data.getQuadrants())
			{
				if(qd.getQuadrantPosition() == QuadrantEnum.UL) { n_ul++; }
				else if(qd.getQuadrantPosition() == QuadrantEnum.UR) { n_ur++; }
				else if(qd.getQuadrantPosition() == QuadrantEnum.LL) { n_ll++; }
				else if(qd.getQuadrantPosition() == QuadrantEnum.LR) { n_lr++; }
			}
		}
		
		n = n_ul + n_ur + n_ll + n_lr;
		
		w = (float) (Gamma.gamma(n + 1) / (Gamma.gamma(n_ul + 1) * Gamma.gamma(n_ur + 1) * Gamma.gamma(n_ll + 1) * Gamma.gamma(n_lr + 1)));
		w_max = (float) (Gamma.gamma(n + 1) / (Math.pow(Gamma.gamma((n + 1) / 4), 4)));
		
		homogeneity = w / w_max;
		
		//System.out.println("Homogeneity: " + homogeneity);
		
		return homogeneity;
	}

	private float MeasureRhythm()
	{
		float rhythm;
		float rhm_x, rhm_y, rhm_a;
		float[] x, y, a;
		float[] x_n, y_n, a_n;
		
		x = new float[4];
		y = new float[4];
		a = new float[4];
		
		for(LayoutComponent component: SmartLayout.components)
		{
			ComponentData data = componentDataMap.get(component);
			
			for(QuadrantData quadrantData: data.getQuadrants())
			{
				int quadrantIndex;
				
				switch(quadrantData.getQuadrantPosition())
				{
				case UL:
					quadrantIndex = 0;
					
					break;
					
				case UR:
					quadrantIndex = 1;
					
					break;
					
				case LL:
					quadrantIndex = 2;

					break;
					
				case LR:
					quadrantIndex = 3;

					break;
					
				default:
					System.out.println("Error in rhythm measurement.");
					
					return -1;
				}
				
				x[quadrantIndex] += Math.abs(quadrantData.getCenterX() - frameCenterX);
				y[quadrantIndex] += Math.abs(quadrantData.getCenterY() - frameCenterY);
				a[quadrantIndex] += quadrantData.getArea();
			}
		}

		DecimalFormat df = new DecimalFormat("###.###");
		df.setRoundingMode(RoundingMode.CEILING);
		for(int i = 0; i < 4; i++)
		{
			x[i] = Float.parseFloat(df.format(x[i]).replace(",", "."));
			y[i] = Float.parseFloat(df.format(y[i]).replace(",", "."));
			a[i] = Float.parseFloat(df.format(a[i]).replace(",", "."));
		}
		
		x_n = Normalize(x);
		y_n = Normalize(y);
		a_n = Normalize(a);
		
		rhm_x = (Math.abs(x_n[0] - x_n[1]) + Math.abs(x_n[0] - x_n[3]) + Math.abs(x_n[0] - x_n[2])
			+ Math.abs(x_n[1] - x_n[3]) + Math.abs(x_n[1] - x_n[2]) + Math.abs(x_n[3] - x_n[2])) / 6;
		
		rhm_y = (Math.abs(y_n[0] - y_n[1]) + Math.abs(y_n[0] - y_n[3]) + Math.abs(y_n[0] - y_n[2])
			+ Math.abs(y_n[1] - y_n[3]) + Math.abs(y_n[1] - y_n[2]) + Math.abs(y_n[3] - y_n[2])) / 6;
		
		rhm_a = (Math.abs(a_n[0] - a_n[1]) + Math.abs(a_n[0] - a_n[3]) + Math.abs(a_n[0] - a_n[2])
			+ Math.abs(a_n[1] - a_n[3]) + Math.abs(a_n[1] - a_n[2]) + Math.abs(a_n[3] - a_n[2])) / 6;
		
		rhythm = 1 - (Math.abs(rhm_x) + Math.abs(rhm_y) + Math.abs(rhm_a)) / 3;
		
		//System.out.println("Rhythm: " + rhythm);
		
		return rhythm;
	}

	private float MeasureOrderAndComplexity(float[] measurements, float[] coefficients)
	{
		float orderAndComplexity;
		float measurementSum, coefficentSum;
		measurementSum = coefficentSum = 0;
		
		for(int i = 0; i < measurements.length; i++)
		{
			measurementSum += measurements[i] * coefficients[i];
			coefficentSum += coefficients[i];
		}
		
		orderAndComplexity = measurementSum / coefficentSum;
		
		//System.out.println("Order and Complexity: " + orderAndComplexity + "\n");
		
		return orderAndComplexity;
	}
	
	private void CalculateComponentAreas()
	{
		componentDataMap = new HashMap<LayoutComponent, ComponentData>();
		datas = new ArrayList<ComponentData>();
		maxAreas = new float[4];
		maxAreas[0] = maxAreas[1] = maxAreas[2] = maxAreas[3] = 0;
		
		for(LayoutComponent component: SmartLayout.components)
		{
			float totalArea, leftArea, rightArea, topArea, bottomArea;
			float distance, horizontalDistance, verticalDistance, leftDistance, rightDistance, topDistance, bottomDistance;
			
			float left, right, top, bottom, width, height;
			float centerX, centerY;
			
			width = component.getAssignedWidth();
			height = component.getAssignedHeight();
			
			left = component.getAssignedX();
			right = left + width;
			top = component.getAssignedY();
			bottom = top + height;
			
			centerX = (right + left) / 2;
			centerY = (bottom + top) / 2;
			
			if(left < frameCenterX)
			{
				if(right < frameCenterX)
				{
					leftArea = width * height;
					rightArea = 0;
					
					leftDistance = frameCenterX - centerX;
					rightDistance = 0;
				}
				else
				{
					float leftWidth = frameCenterX - left;
					float rightWidth = right - frameCenterX;
					
					leftArea = leftWidth * height;
					rightArea = rightWidth * height;
					
					float leftCenterX = (frameCenterX + left) / 2;
					float rightCenterX = (frameCenterX + right) / 2;
					
					leftDistance = frameCenterX - leftCenterX;
					rightDistance = rightCenterX - frameCenterX;
				}
			}
			else
			{
				leftArea = 0;
				rightArea = width * height;
				
				leftDistance = 0;
				rightDistance = centerX - frameCenterX;
			}
			
			if(top < frameCenterY)
			{
				if(bottom < frameCenterY)
				{
					topArea = width * height;
					bottomArea = 0;
					
					topDistance = frameCenterY - centerY;
					bottomDistance = 0;
				}
				else
				{
					float topHeight = frameCenterY - top;
					float bottomHeight = bottom - frameCenterY;
					
					topArea = width * topHeight;
					bottomArea = width * bottomHeight;
					
					float topCenterY = (frameCenterY + top) / 2;
					float bottomCenterY = (frameCenterY + bottom) / 2;
					
					topDistance = frameCenterY - topCenterY;
					bottomDistance = bottomCenterY - frameCenterY;
				}
			}
			else
			{
				topArea = 0;
				bottomArea = width * height;
				
				topDistance = 0;
				bottomDistance = centerY - frameCenterY;
			}
			
			if(maxAreas[0] < leftArea)
				maxAreas[0] = leftArea;
			
			if(maxAreas[1] < rightArea)
				maxAreas[1] = rightArea;
			
			if(maxAreas[2] < topArea)
				maxAreas[2] = topArea;
			
			if(maxAreas[3] < bottomArea)
				maxAreas[3] = bottomArea;
			
			totalArea = leftArea + rightArea;
			horizontalDistance = Math.abs(frameCenterX - centerX);
			verticalDistance = Math.abs(frameCenterY - centerY);
			distance = (float) Math.sqrt(Math.pow(horizontalDistance, 2) + Math.pow(verticalDistance, 2));
			
			ComponentData data = new ComponentData(totalArea, leftArea, rightArea, topArea, bottomArea, distance, horizontalDistance,
					verticalDistance, leftDistance, rightDistance, topDistance, bottomDistance, centerX, centerY);
			
			data.SetComponent(component);
			data.CalculateQuadrants(frameCenterX, frameCenterY);
			
			componentDataMap.put(component, data);
			datas.add(data);
		}
	}
	
	private float[] Normalize(float[] data)
	{
		float[] normalized = new float[data.length];
		float[] sortedData = new float[data.length];
		
		for(int i = 0; i < data.length; i++)
			sortedData[i] = data[i];
		
		Arrays.sort(sortedData);
		
		if(sortedData[0] == sortedData[sortedData.length - 1])
			for(int i = 0; i < normalized.length; i++)
				normalized[i] = 1;
		else
			for(int i = 0; i < normalized.length; i++)
				normalized[i] = (data[i] - sortedData[0]) / (sortedData[sortedData.length - 1] - sortedData[0]);
		
		return normalized;
	}
}


