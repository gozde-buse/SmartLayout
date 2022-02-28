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
	private Vector<ComponentData> datas;
	private float[] maxAreas;
	
	private int componentSize;
	
	private int frameWidth;
	private int frameHeight;
	private int frameCenterX;
	private int frameCenterY;
	
	private int layoutWidth;
	private int layoutHeight;
	//private int layoutCenterX;
	//private int layoutCenterY;
	
	public static float MeasureAesthetics(int frameWidth, int frameHeight, int layoutWidth, int layoutHeight)
	{
		return 1;
		
		/*aestheticMeasure = new AestheticMeasureUtil();
		
		aestheticMeasure.componentSize = SmartLayout.components.size();
		
		aestheticMeasure.frameWidth = frameWidth;
		aestheticMeasure.frameHeight = frameHeight;
		aestheticMeasure.frameCenterX = frameWidth / 2;
		aestheticMeasure.frameCenterY = frameHeight / 2;
		
		aestheticMeasure.layoutWidth = layoutWidth;
		aestheticMeasure.layoutHeight = layoutHeight;
		//aestheticMeasure.layoutCenterX = layoutWidth / 2;
		//aestheticMeasure.layoutCenterY = layoutHeight / 2;
		
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
		
		return m_orderAndComplexity;*/
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
		
		System.out.println("Balance: " + balance);
		
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
		
		System.out.println("Equilibrium: " + equilibrium);
		
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
		
		System.out.println("Symmetry: " + symmetry);
		
		return symmetry;
	}
	
	private float MeasureSequence()
	{
		float sequence;
		int[] q = {4, 3, 2, 1};
		int[] v = new int[4];
		float[] w = new float[4];
		
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
		
		System.out.println("Sequence: " + sequence);
		
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
		
		System.out.println("Cohesion: " + cohesion);
		
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
		
		um_space = 1 - ((layoutWidth * layoutHeight - totalAreas) / (frameWidth * frameHeight - totalAreas));
		
		unity = (Math.abs(um_form) + Math.abs(um_space)) / 2;
		
		System.out.println("Unity: " + unity);
		
		//return unity;
		return 0;
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
		
		System.out.println("Proportion: " + proportion);
		
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
		
		System.out.println("Simplicity: " + simplicity);
		
		return simplicity;
	}

	private float MeasureDensity()
	{
		float density;
		float totalAreas = 0;
		
		for(ComponentData data: datas)
			totalAreas += data.getTotalArea();
		
		density = 1 - totalAreas / (frameWidth * frameHeight);
		
		System.out.println("Density: " + density);
		
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
		
		System.out.println("Regularity: " + regularity);
		
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
		
		System.out.println("Economy: " + economy);
		
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
		
		System.out.println("Homogeneity: " + homogeneity);
		
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
		
		System.out.println("Rhythm: " + rhythm);
		
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
		
		System.out.println("Order and Complexity: " + orderAndComplexity + "\n");
		
		return orderAndComplexity;
	}
	
	private void CalculateComponentAreas()
	{
		componentDataMap = new HashMap<LayoutComponent, ComponentData>();
		datas = new Vector<ComponentData>();
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static final double balanceFactor = 1;
	private static final double equilibriumFactor = 0;
	private static final double symmetryFactor = 1;
	private static final double sequenceFactor = 0.2;
	private static final double cohesionFactor = 1;
	private static final double unityFactor = 1;
	private static final double proportionFactor = 1;
	private static final double simplicityFactor = 1;
	private static final double densityFactor = 0;
	private static final double regularityFactor = 1;
	private static final double economyFactor = 1;
	private static final double homogeneityFactor = 1;
	private static final double rhythmFactor = 1;
	private static final double orderAndComplexityFactor = 1;

	private static final double[] rectangleRatios = {1, 1 / 1.414, 1 / 1.618, 1 / 1.732, 1 / 2.0};
	// This is not multi thread safe
	private static List<Double> leftAreaList = new ArrayList<>();
	private static List<Double> rightAreaList = new ArrayList<>();
	private static List<Double> topAreaList = new ArrayList<>();
	private static List<Double> bottomAreaList = new ArrayList<>();
	private static List<Double> areaList = new ArrayList<>();
	private static List<Coordinate> centerCoordinateList = new ArrayList<>();
	private static List<LayoutComponent> nodeList = new ArrayList<>();
	private static List<Coordinate> widthHeightList = new ArrayList<>();
	private static List<LayoutComponent> ulList = new ArrayList<>();
	private static List<LayoutComponent> urList = new ArrayList<>();
	private static List<LayoutComponent> llList = new ArrayList<>();
	private static List<LayoutComponent> lrList = new ArrayList<>();
	private static SymmetryData symUL = null;
	private static SymmetryData symUR = null;
	private static SymmetryData symLL = null;
	private static SymmetryData symLR = null;

	private static double xul;
	private static double xur;
	private static double xll;
	private static double xlr;
	private static double yul;
	private static double yur;
	private static double yll;
	private static double ylr;

	private static double frameWidth_o = 0;
	private static double frameHeight_o = 0;
	private static double screenWidth = 0;
	private static double screenHeight = 0;
	// https://stackoverflow.com/questions/49377139/performance-of-set-in-java-vs-list-in-java
	private static List<Integer> unorganizedHAlignmentPoints = new ArrayList<>();
	private static List<Integer> horizontalAlignmentPoints = new ArrayList<>();
	private static List<Integer> unorganizedVAlignmentPoints = new ArrayList<>();
	private static List<Integer> verticalAlignmentPoints = new ArrayList<>();
	// nSpacing looks at the unique spacing of row AND columns so we hold a single list of them
	// FIXME : change this into count and hold an int not HashSet
	private static HashSet<Integer> distinctDistances = new HashSet<>();
	private static int distinctAreaCount = 0;

	private AestheticMeasureUtil () {
	}

	public static double measureAesthetics (Layoutable tree, boolean logEnabled) {
		clearValues();
		frameWidth_o = tree.getAssignedWidth();
		frameHeight_o = tree.getAssignedHeight();
		// gets total screen size, supports multi-monitors as long as they all are the same resolution
		// Multi-screen support
		// DisplayMode dm = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		// screenWidth = dm.getWidth();
		// screenHeight = dm.getHeight();

		// Single screen support
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = screenSize.getWidth();
		screenHeight = screenSize.getHeight();

		traverseToLeafNodes(tree);
		getAlignmentPoints(unorganizedHAlignmentPoints, horizontalAlignmentPoints);
		getAlignmentPoints(unorganizedVAlignmentPoints, verticalAlignmentPoints);
		getDistinctDistances(horizontalAlignmentPoints, distinctDistances);
		getDistinctDistances(verticalAlignmentPoints, distinctDistances);
		getDistinctAreas();

		symUL = calculateSymmetryData(ulList);
		symUR = calculateSymmetryData(urList);
		symLL = calculateSymmetryData(llList);
		symLR = calculateSymmetryData(lrList);

		double[] xArr = {symUL.getX(), symUR.getX(), symLL.getX(), symLR.getX()};
		Arrays.sort(xArr);
		xul = normalize(symUL.getX(), xArr[0], xArr[xArr.length - 1]);
		xur = normalize(symUR.getX(), xArr[0], xArr[xArr.length - 1]);
		xll = normalize(symLL.getX(), xArr[0], xArr[xArr.length - 1]);
		xlr = normalize(symLR.getX(), xArr[0], xArr[xArr.length - 1]);

		double[] yArr = {symUL.getY(), symUR.getY(), symLL.getY(), symLR.getY()};
		Arrays.sort(yArr);
		yul = normalize(symUL.getY(), yArr[0], yArr[yArr.length - 1]);
		yur = normalize(symUR.getY(), yArr[0], yArr[yArr.length - 1]);
		yll = normalize(symLL.getY(), yArr[0], yArr[yArr.length - 1]);
		ylr = normalize(symLR.getY(), yArr[0], yArr[yArr.length - 1]);

		double balance = measureBalance();
		double equilibrium = measureEquilibrium();
		double symmetry = measureSymmetry();
		double sequence = measureSequence();
		double cohesion = measureCohesion();
		double unity = measureUnity();
		double proportion = measureProportion();
		double simplicity = measureSimplicity();
		double density = measureDensity();
		double regularity = measureRegularity();
		double economy = measureEconomy();
		double homogeneity = measureHomogeneity();
		double rhythm = measureRhythm();

		balance = Double.isNaN(balance) ? 0 : balance * balanceFactor;
		equilibrium = Double.isNaN(equilibrium) ? 0 : equilibrium * equilibriumFactor;
		symmetry = Double.isNaN(symmetry) ? 0 : symmetry * symmetryFactor;
		sequence = Double.isNaN(sequence) ? 0 : sequence * sequenceFactor;
		cohesion = Double.isNaN(cohesion) ? 0 : cohesion * cohesionFactor;
		unity = Double.isNaN(unity) ? 0 : unity * unityFactor;
		proportion = Double.isNaN(proportion) ? 0 : proportion * proportionFactor;
		simplicity = Double.isNaN(simplicity) ? 0 : simplicity * simplicityFactor;
		density = Double.isNaN(density) ? 0 : density * densityFactor;
		regularity = Double.isNaN(regularity) ? 0 : regularity * regularityFactor;
		economy = Double.isNaN(economy) ? 0 : economy * economyFactor;
		homogeneity = Double.isNaN(homogeneity) ? 0 : homogeneity * homogeneityFactor;
		rhythm = Double.isNaN(rhythm) ? 0 : rhythm * rhythmFactor;

		double orderAndComplexityNumber = balance + equilibrium + symmetry + sequence + cohesion +
				unity + proportion + simplicity + density + regularity + economy + homogeneity + rhythm;
		double orderAndComplexity = measureOrderAndComplexity(orderAndComplexityNumber) * orderAndComplexityFactor;

		if (logEnabled) {
			StringBuilder sb = new StringBuilder();
			sb.append("\n\tbalance: ").append(balance).append("\n")
					.append("\tequilibrium: ").append(equilibrium).append("\n")
					.append("\tsymmetry: ").append(symmetry).append("\n")
					.append("\tsequence: ").append(sequence).append("\n")
					.append("\tcohesion: ").append(cohesion).append("\n")
					.append("\tunity: ").append(unity).append("\n")
					.append("\tproportion: ").append(proportion).append("\n")
					.append("\tsimplicity: ").append(simplicity).append("\n")
					.append("\tdensity: ").append(density).append("\n")
					.append("\tregularity: ").append(regularity).append("\n")
					.append("\teconomy: ").append(economy).append("\n")
					.append("\thomogeneity: ").append(homogeneity).append("\n")
					.append("\trhythm: ").append(rhythm).append("\n")
					.append("\torderAndComplexity: ").append(orderAndComplexity);
			log.info(sb.toString());
		}

		return orderAndComplexityNumber + orderAndComplexity;
	}

	public static double measureBalance () {
		double bmVertical;
		double bmHorizontal;
		double wLeft = 0;
		double wRight = 0;
		double wTop = 0;
		double wBottom = 0;

		if (leftAreaList.isEmpty()) {
			return 0;
		}

		// All have the same amount of elements so one for loop is enough
		for (int i = 0; i < getSize(); i++) {
			wLeft += leftAreaList.get(i) / Collections.max(leftAreaList, null);
			wRight += rightAreaList.get(i) / Collections.max(rightAreaList, null);
			wTop += topAreaList.get(i) / Collections.max(topAreaList, null);
			wBottom += bottomAreaList.get(i) / Collections.max(bottomAreaList, null);
		}

		bmVertical = (wLeft - wRight) / Math.max(wLeft, wRight);
		bmHorizontal = (wTop - wBottom) / Math.max(wTop, wBottom);
		return 1 - ((Math.abs(bmVertical) + Math.abs(bmHorizontal)) / 2);
	}

	// Center of mass * Area equals out so result is always 0.
	public static double measureEquilibrium () {
		double upperEmX = 0;
		double emX = 0;
		double upperEmY = 0;
		double emY = 0;
		double sumArea = 0;
		for (int i = 0; i < getSize(); i++) {
			upperEmX += (areaList.get(i) * (centerCoordinateList.get(i).getX() - frameWidth_o / 2d));
			upperEmY += (areaList.get(i) * (centerCoordinateList.get(i).getY() - frameHeight_o / 2d));
			sumArea += areaList.get(i);
		}
		emX = (2 * upperEmX) / (getSize() * frameWidth_o * sumArea);
		emY = (2 * upperEmY) / (getSize() * frameHeight_o * sumArea);
		return 1 - ((Math.abs(emX) + Math.abs(emY)) / 2);
	}

	// Is it worth it? This much calculation just to see if it is symmetric or not?
	public static double measureSymmetry () {
		// Normalize values for equation
		double[] bArr = {symUL.getB(), symUR.getB(), symLL.getB(), symLR.getB()};
		Arrays.sort(bArr);
		double bul = normalize(symUL.getB(), bArr[0], bArr[bArr.length - 1]);
		double bur = normalize(symUR.getB(), bArr[0], bArr[bArr.length - 1]);
		double bll = normalize(symLL.getB(), bArr[0], bArr[bArr.length - 1]);
		double blr = normalize(symLR.getB(), bArr[0], bArr[bArr.length - 1]);

		double[] hArr = {symUL.getH(), symUR.getH(), symLL.getH(), symLR.getH()};
		Arrays.sort(hArr);
		double hul = normalize(symUL.getH(), hArr[0], hArr[hArr.length - 1]);
		double hur = normalize(symUR.getH(), hArr[0], hArr[hArr.length - 1]);
		double hll = normalize(symLL.getH(), hArr[0], hArr[hArr.length - 1]);
		double hlr = normalize(symLR.getH(), hArr[0], hArr[hArr.length - 1]);

		double[] tArr = {symUL.getTheta(), symUR.getTheta(), symLL.getTheta(), symLR.getTheta()};
		Arrays.sort(tArr);
		double tul = normalize(symUL.getTheta(), tArr[0], tArr[tArr.length - 1]);
		double tur = normalize(symUR.getTheta(), tArr[0], tArr[tArr.length - 1]);
		double tll = normalize(symLL.getTheta(), tArr[0], tArr[tArr.length - 1]);
		double tlr = normalize(symLR.getTheta(), tArr[0], tArr[tArr.length - 1]);

		double[] rArr = {symUL.getR(), symUR.getR(), symLL.getR(), symLR.getR()};
		Arrays.sort(rArr);
		double rul = normalize(symUL.getR(), rArr[0], rArr[rArr.length - 1]);
		double rur = normalize(symUR.getR(), rArr[0], rArr[rArr.length - 1]);
		double rll = normalize(symLL.getR(), rArr[0], rArr[rArr.length - 1]);
		double rlr = normalize(symLR.getR(), rArr[0], rArr[rArr.length - 1]);

		double symVertical = (Math.abs(xul - xur) + Math.abs(xll - xlr) + Math.abs(yul - yur) + Math.abs(yll - ylr) + Math.abs(hul - hur) + Math.abs(hll - hlr) + Math.abs(bul - bur) + Math.abs(bll - blr) + Math.abs(tul - tur) + Math.abs(tll - tlr) + Math.abs(rul - rur) + Math.abs(rll - rlr)) / 12d;
		double symHorizontal = (Math.abs(xul - xll) + Math.abs(xur - xlr) + Math.abs(yul - yll) + Math.abs(yur - ylr) + Math.abs(hul - hll) + Math.abs(hur - hlr) + Math.abs(bul - bll) + Math.abs(bur - blr) + Math.abs(tul - tll) + Math.abs(tur - tlr) + Math.abs(rul - rll) + Math.abs(rur - rlr)) / 12d;
		double symRadial = (Math.abs(xul - xlr) + Math.abs(xur - xll) + Math.abs(yul - ylr) + Math.abs(yur - yll) + Math.abs(hul - hlr) + Math.abs(hur - hll) + Math.abs(bul - blr) + Math.abs(bur - bll) + Math.abs(tul - tlr) + Math.abs(tur - tll) + Math.abs(rul - rlr) + Math.abs(rur - rll)) / 12d;

		return 1 - (Math.abs(symVertical) + Math.abs(symHorizontal) + Math.abs(symRadial)) / 3d;
	}

	public static SymmetryData calculateSymmetryData (List<LayoutComponent> list) {
		SymmetryData sym = new SymmetryData();
		for (int i = 0; i < list.size(); i++) {
			double xijMinusXc = list.get(i).getAssignedX() + (list.get(i).getAssignedWidth() / 2d) - (frameWidth_o / 2d);
			sym.setX(sym.getX() + Math.abs(xijMinusXc));

			double yijMinusYc = list.get(i).getAssignedY() + (list.get(i).getAssignedHeight() / 2d) - (frameHeight_o / 2d);
			sym.setY(sym.getY() + Math.abs(yijMinusYc));

			sym.setB(sym.getB() + list.get(i).getAssignedWidth());

			sym.setH(sym.getH() + list.get(i).getAssignedHeight());

			sym.setArea(sym.getArea() + (list.get(i).getAssignedWidth() * list.get(i).getAssignedHeight()));

			sym.setTheta(sym.getTheta() + Math.abs(yijMinusYc / xijMinusXc));

			sym.setR(sym.getR() + Math.sqrt(Math.pow(xijMinusXc, 2) + Math.pow(yijMinusYc, 2)));
		}
		return sym;
	}

	public static double measureSequence () {
		double wul = 4 * ulList.stream().mapToDouble(node -> node.getAssignedWidth() * node.getAssignedHeight()).sum();
		double wur = 3 * urList.stream().mapToDouble(node -> node.getAssignedWidth() * node.getAssignedHeight()).sum();
		double wll = 2 * llList.stream().mapToDouble(node -> node.getAssignedWidth() * node.getAssignedHeight()).sum();
		double wlr = lrList.stream().mapToDouble(node -> node.getAssignedWidth() * node.getAssignedHeight()).sum();
		List<Double> wArray = Arrays.asList(wul, wur, wll, wlr);
		Collections.sort(wArray);
		int vul = wArray.indexOf(wul) + 1;
		int vur = wArray.indexOf(wur) + 1;
		int vll = wArray.indexOf(wll) + 1;
		int vlr = wArray.indexOf(wlr) + 1;

		int absQjMinusVj = Math.abs(4 - vul);
		absQjMinusVj += Math.abs(3 - vur);
		absQjMinusVj += Math.abs(2 - vll);
		absQjMinusVj += Math.abs(1 - vlr);

		return 1 - absQjMinusVj / 8d;
	}

	public static double measureCohesion () {
		double cmFL;
		double cmLO;
		double fi = 0;
		double ti;
		for (int i = 0; i < getSize(); i++) {
			if (nodeList.get(i).getAssignedHeight() <= 0 || nodeList.get(i).getAssignedWidth() <= 0) {
				continue;
			}
			ti = ((double) nodeList.get(i).getAssignedHeight() / nodeList.get(i).getAssignedWidth()) / (frameHeight_o / frameWidth_o);
			fi += ti <= 1 ? ti : 1 / ti;
		}
		double tFL = (frameHeight_o / frameWidth_o) / (screenHeight / screenWidth);
		cmFL = tFL <= 1 ? tFL : 1 / tFL;
		cmLO = fi / getSize();
		return (Math.abs(cmFL) + Math.abs(cmLO)) / 2d;
	}

	// UMSpace still not clear.
	public static double measureUnity () {
		double umForm = 1 - (distinctAreaCount - 1d) / getSize();
		// UMSpace = 1 - (areaLayout - totalArea) / (areaFrame - totalArea) which is 0 in our case
		// 1 - (numberOfSizes - 1) / getSize()
		return umForm;
	}

	public static double measureProportion () {
		double pmObject = 0;
		double pmLayout = 0;

		for (LayoutComponent layoutComponent : nodeList) {
			double ri = (double) layoutComponent.getAssignedHeight() / layoutComponent.getAssignedWidth();
			double pi = ri <= 1 ? ri : 1 / ri;
			double[] pjMinusPi = {Math.abs(rectangleRatios[0] - pi), Math.abs(rectangleRatios[1] - pi), Math.abs(rectangleRatios[2] - pi), Math.abs(rectangleRatios[3] - pi), Math.abs(rectangleRatios[4] - pi)};
			Arrays.sort(pjMinusPi);
			pmObject += 1 - (pjMinusPi[0]) / 0.5;
		}
		pmObject = pmObject / getSize();

		double rl = frameHeight_o / frameWidth_o; // Assuming layout is as big as frame
		double pl = rl <= 1 ? rl : 1 / rl;
		double[] pjMinusPl = {Math.abs(rectangleRatios[0] - pl), Math.abs(rectangleRatios[1] - pl), Math.abs(rectangleRatios[2] - pl), Math.abs(rectangleRatios[3] - pl), Math.abs(rectangleRatios[4] - pl)};
		Arrays.sort(pjMinusPl);
		pmLayout = 1 - (pjMinusPl[0]) / 0.5;

		return (Math.abs(pmObject) + Math.abs(pmLayout)) / 2d;
	}

	public static double measureSimplicity () {
		return 3d / (verticalAlignmentPoints.size() + horizontalAlignmentPoints.size() + getSize());
	}

	// Not Applicable
	public static double measureDensity () {
		// TODO : 1 - (totalArea / totalArea)
		return 0;
	}

	public static double measureRegularity () {
		double rmAlignment = 1 - (verticalAlignmentPoints.size() + horizontalAlignmentPoints.size()) / (2d * getSize());
		double rmSpacing = getSize() == 1 ? 1 : 1 - (distinctDistances.size() - 1) / (2d * getSize() - 1);

		return (Math.abs(rmAlignment) + Math.abs(rmSpacing)) / 2d;
	}

	public static double measureEconomy () {
		return 1d / distinctAreaCount;
	}

	/**
	 * Hm = (1 - Ht)^2
	 * Ht = (sum j of |n/4 - nj| / (n/4)) / 6
	 * where j is the quadrant upperLeft, upperRight, lowerLeft, lowerRight
	 * and n is the number of elements
	 *
	 * @return
	 */
	public static double measureHomogeneity () {
		double ht = Math.abs(getSize() / 4d - ulList.size()) / (getSize() / 4d);
		ht += Math.abs(getSize() / 4d - urList.size()) / (getSize() / 4d);
		ht += Math.abs(getSize() / 4d - llList.size()) / (getSize() / 4d);
		ht += Math.abs(getSize() / 4d - lrList.size()) / (getSize() / 4d);
		ht = ht / 6d;
		return Math.pow(1 - ht, 2);
	}

	public static double measureRhythm () {
		double[] aArr = {symUL.getArea(), symUR.getArea(), symLL.getArea(), symLR.getArea()};
		Arrays.sort(aArr);
		double aul = normalize(symUL.getArea(), aArr[0], aArr[aArr.length - 1]);
		double aur = normalize(symUR.getArea(), aArr[0], aArr[aArr.length - 1]);
		double all = normalize(symLL.getArea(), aArr[0], aArr[aArr.length - 1]);
		double alr = normalize(symLR.getArea(), aArr[0], aArr[aArr.length - 1]);

		double rhmX = (Math.abs(xul - xur) + Math.abs(xul - xlr) + Math.abs(xul - xll) + Math.abs(xur - xlr) + Math.abs(xur - xll) + Math.abs(xlr - xll)) / 6d;
		double rhmY = (Math.abs(yul - yur) + Math.abs(yul - ylr) + Math.abs(yul - yll) + Math.abs(yur - ylr) + Math.abs(yur - yll) + Math.abs(ylr - yll)) / 6d;
		double rhmArea = (Math.abs(aul - aur) + Math.abs(aul - alr) + Math.abs(aul - all) + Math.abs(aur - alr) + Math.abs(aur - all) + Math.abs(alr - all)) / 6d;
		return 1 - (Math.abs(rhmX) + Math.abs(rhmY) + Math.abs(rhmArea)) / 3d;
	}

	public static double measureOrderAndComplexity (double orderAndComplexityNumber) {
		return orderAndComplexityNumber / 13d;
	}

	private static void traverseToLeafNodes (Layoutable node) {
		if (node instanceof LayoutContainer) {
			for (Layoutable child : ((LayoutContainer) node).getChildren()) {
				traverseToLeafNodes(child);
			}
		}
		if (node instanceof LayoutComponent) {
			findAligmentPoints(node);
			findLeftAreaOfComponent((LayoutComponent) node);
			findRightAreaOfComponent((LayoutComponent) node);
			findTopAreaOfComponent((LayoutComponent) node);
			findBottomAreaOfComponent((LayoutComponent) node);
			placeComponentInQuadrantList((LayoutComponent) node);
			findAreaOfComponent((LayoutComponent) node);
			findCenterCoordinateOfComponent((LayoutComponent) node);
			addComponentAndSize((LayoutComponent) node);
		}
	}


	/**
	 * this method adds components to a quadrant
	 * ___________
	 * |    |    |
	 * |    |  X |
	 * |____|____|
	 *
	 * @param node - Component that we try to place the quadrant on
	 */
	private static void placeComponentInQuadrantList (LayoutComponent node) {
		if (node.getAssignedX() + node.getAssignedWidth() / 2d <= (frameWidth_o / 2d)) {
			// Center of node is on the left
			if (node.getAssignedY() + node.getAssignedHeight() / 2d < frameHeight_o / 2d) {
				// Center of node is on the top
				ulList.add(node);
			} else {
				// Center of node is on the bottom
				llList.add(node);
			}
		} else {
			// Center of node is on the right
			if (node.getAssignedY() + node.getAssignedHeight() / 2d < frameHeight_o / 2d) {
				// Center of node is on the top
				urList.add(node);
			} else {
				// Center of node is on the bottom
				lrList.add(node);
			}
		}
	}

	/**
	 * this method returns the component's area with respect to left side of the screen
	 * ___________
	 * |    |    |
	 * |  X |    |
	 * |____|____|
	 *
	 * @param node - Component that we try to find the area of
	 */
	private static void findLeftAreaOfComponent (LayoutComponent node) {
		if (node.getAssignedX() >= (frameWidth_o / 2d)) {
			leftAreaList.add(0d);
			return;
		}
		if (node.getAssignedX() + node.getAssignedWidth() >= (frameWidth_o / 2d)) {
			leftAreaList.add(Math.abs((frameWidth_o / 2d - node.getAssignedX()) * node.getAssignedHeight()));
		} else {
			leftAreaList.add(Math.abs((double) (node.getAssignedWidth() * node.getAssignedHeight())));
		}
	}

	/**
	 * this method returns the component's area with respect to right side of the screen
	 * ___________
	 * |    |    |
	 * |    |  X |
	 * |____|____|
	 *
	 * @param node - Component that we try to find the area of
	 */
	private static void findRightAreaOfComponent (LayoutComponent node) {
		if (node.getAssignedX() + node.getAssignedWidth() <= (frameWidth_o / 2d)) {
			rightAreaList.add(0d);
			return;
		}
		if (node.getAssignedX() <= (frameWidth_o / 2d)) {
			rightAreaList.add(Math.abs((node.getAssignedX() + node.getAssignedWidth() - frameWidth_o / 2d) * node.getAssignedHeight()));
		} else {
			rightAreaList.add(Math.abs((double) (node.getAssignedWidth() * node.getAssignedHeight())));
		}
	}

	/**
	 * this method returns the component's area with respect to top side of the screen
	 * __________
	 * |    X   |
	 * |--------|
	 * |________|
	 *
	 * @param node - Component that we try to find the area of
	 */
	private static void findTopAreaOfComponent (LayoutComponent node) {
		if (node.getAssignedY() >= (frameHeight_o / 2d)) {
			topAreaList.add(0d);
			return;
		}
		if (node.getAssignedY() + node.getAssignedHeight() >= (frameHeight_o / 2d)) {
			topAreaList.add(Math.abs((frameHeight_o / 2d - node.getAssignedY()) * node.getAssignedWidth()));
		} else {
			topAreaList.add(Math.abs((double) (node.getAssignedHeight() * node.getAssignedWidth())));
		}
	}

	/**
	 * this method returns the component's area with respect to top side of the screen
	 * __________
	 * |        |
	 * |--------|
	 * |____X___|
	 *
	 * @param node - Component that we try to find the area of
	 */
	private static void findBottomAreaOfComponent (LayoutComponent node) {
		if (node.getAssignedY() + node.getAssignedHeight() <= (frameHeight_o / 2d)) {
			bottomAreaList.add(0d);
			return;
		}
		if (node.getAssignedY() <= (frameHeight_o / 2d)) {
			bottomAreaList.add(Math.abs((node.getAssignedY() + node.getAssignedHeight() - frameHeight_o / 2d) * node.getAssignedWidth()));
		} else {
			bottomAreaList.add(Math.abs((double) (node.getAssignedHeight() * node.getAssignedWidth())));
		}
	}

	private static void findAreaOfComponent (LayoutComponent node) {
		areaList.add(Math.abs((double) (node.getAssignedWidth() * node.getAssignedHeight())));
	}

	private static void findCenterCoordinateOfComponent (LayoutComponent node) {
		centerCoordinateList.add(new Coordinate(node.getAssignedX() + (node.getAssignedWidth() / 2d), node.getAssignedY() + (node.getAssignedHeight() / 2d)));
	}

	/**
	 * This method gets horizontal and vertical alignment points in a duplicated manner
	 *
	 * @param node
	 */
	private static void findAligmentPoints (Layoutable node) {
		unorganizedHAlignmentPoints.add(node.getAssignedX());
		unorganizedHAlignmentPoints.add(node.getAssignedX() + node.getAssignedWidth());
		unorganizedVAlignmentPoints.add(node.getAssignedY());
		unorganizedVAlignmentPoints.add(node.getAssignedY() + node.getAssignedHeight());
	}

	/**
	 * This method organizes alignment points and makes a new unique list of these points
	 *
	 * @param alignmentPoints    - unorganized alignment points
	 * @param newAlignmentPoints - organized alignment points
	 */
	private static void getAlignmentPoints (List<Integer> alignmentPoints, List<Integer> newAlignmentPoints) {
		Collections.sort(alignmentPoints);
		// Traverse the sorted array
		for (int i = 0; i < alignmentPoints.size(); i++) {
			// Move the index ahead while there are duplicates
			while (i < alignmentPoints.size() - 1 && alignmentPoints.get(i).equals(alignmentPoints.get(i + 1))) {
				i++;
			}
			// Add the unique value to a new list
			newAlignmentPoints.add(alignmentPoints.get(i));
		}
	}

	/**
	 * This method calculates distances of alignment points and stores the unique values in a set
	 * <site>https://stackoverflow.com/questions/17985029/hashset-vs-arraylist</site>
	 *
	 * @param alignmentPoints   - organizedAlignmentPoints
	 * @param distinctDistances - distinctDistances
	 */
	private static void getDistinctDistances (List<Integer> alignmentPoints, HashSet<Integer> distinctDistances) {
		if (alignmentPoints.size() < 2) {
			return;
		}
		for (int i = 1; i < alignmentPoints.size(); i++) {
			distinctDistances.add(alignmentPoints.get(i) - alignmentPoints.get(i - 1));
		}
	}

	private static void addComponentAndSize (LayoutComponent node) {
		nodeList.add(node);
		widthHeightList.add(new Coordinate(node.getAssignedWidth(), node.getAssignedHeight()));
	}

	private static void getDistinctAreas () {
		Collections.sort(widthHeightList);
		// Traverse the sorted array
		for (int i = 0; i < widthHeightList.size(); i++) {
			// Move the index ahead while there are duplicates
			while (i < widthHeightList.size() - 1 && widthHeightList.get(i).getX() == widthHeightList.get(i + 1).getX() && widthHeightList.get(i).getY() == widthHeightList.get(i + 1).getY()) {
				i++;
			}
			// Add the unique value to a new list
			distinctAreaCount++;
		}
	}

	private static int getSize () {
		return leftAreaList.size();
	}

	private static void clearValues () {
		leftAreaList.clear();
		rightAreaList.clear();
		topAreaList.clear();
		bottomAreaList.clear();
		areaList.clear();
		centerCoordinateList.clear();
		nodeList.clear();
		widthHeightList.clear();
		ulList.clear();
		urList.clear();
		llList.clear();
		lrList.clear();
		symUL = null;
		symUR = null;
		symLL = null;
		symLR = null;
		xul = 0;
		xur = 0;
		xll = 0;
		xlr = 0;
		yul = 0;
		yur = 0;
		yll = 0;
		ylr = 0;
		frameWidth_o = 0;
		frameHeight_o = 0;
		unorganizedHAlignmentPoints.clear();
		unorganizedVAlignmentPoints.clear();
		horizontalAlignmentPoints.clear();
		verticalAlignmentPoints.clear();
		distinctDistances.clear();
		distinctAreaCount = 0;
	}

	public static double normalize (double i, double min, double max) {
		return (i - min) / (max - min);
	}

	@Getter
	@Setter
	private static class SymmetryData {
		double x = 0;
		double y = 0;
		double h = 0;
		double b = 0;
		double theta = 0;
		double r = 0;
		double area = 0;
	}
}


