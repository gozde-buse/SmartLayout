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
	private ArrayList<ComponentData> datas; //Array yapılabilir -> Boyut: components.size()
	private float[] maxAreas;
	
	private int componentSize;
	
	private int frameWidth;
	private int frameHeight;
	private int frameCenterX;
	private int frameCenterY;
	
	private int layoutWidth;
	private int layoutHeight;
	
	private float[] balance_w;
	private float equilibrium_totalComponentArea;
	private float[] equilibrium_centerFactors;
	private float[] symmetry_x, symmetry_y, symmetry_h, symmetry_b, symmetry_t, symmetry_r;
	private float[] sequence_w;
	private float cohesion_ffoSum, cohesion_floSum;
	private float unity_totalAreas, unity_sizeCount;
	private float proportion_tempObjectTotal;
	private float simplicity_n_vap, simplicity_n_hap;
	private float density_totalAreas;
	private float regularity_n_vap, regularity_n_hap;
	private float economy_sizeCount;
	private float[] homogenity_n;
	private float[] rhythm_x, rhythm_y, rhythm_a;
	
	public AestheticMeasureUtil()
	{
		datas = new ArrayList<ComponentData>();
		balance_w = new float[4];
		equilibrium_centerFactors = new float[2];
		symmetry_x = new float[4]; symmetry_y = new float[4];
		symmetry_b = new float[4]; symmetry_h = new float[4];
		symmetry_t = new float[4]; symmetry_r = new float[4];
		sequence_w = new float[4];
		unity_sizeCount = 1;
		simplicity_n_vap = simplicity_n_hap = 1;
		regularity_n_vap = regularity_n_hap = 1;
		economy_sizeCount = 1;
		homogenity_n = new float[4];
		rhythm_x = new float[4];
		rhythm_y = new float[4];
		rhythm_a = new float[4];
	}
	
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
		
		bm_horizontal = (balance_w[2] - balance_w[3]) / Math.max(Math.abs(balance_w[2]), Math.abs(balance_w[3]));
		bm_vertical = (balance_w[0] - balance_w[1]) / Math.max(Math.abs(balance_w[0]), Math.abs(balance_w[1]));
		
		balance = 1 - (Math.abs(bm_vertical) + Math.abs(bm_horizontal)) / 2;
		
		//System.out.println("Balance: " + balance);
		
		return balance;
	}
	
	private float MeasureEquilibrium()
	{
		float equilibrium;
		float em_x, em_y;
		
		em_x = (2 * equilibrium_centerFactors[0]) / (componentSize * frameWidth * equilibrium_totalComponentArea);
		em_y = (2 * equilibrium_centerFactors[1]) / (componentSize * frameHeight * equilibrium_totalComponentArea);
		
		equilibrium = 1 - (Math.abs(em_x) + Math.abs(em_y)) / 2;
		
		//System.out.println("Equilibrium: " + equilibrium);
		
		return equilibrium;
	}
	
	private float MeasureSymmetry()
	{
		float symmetry;
		float sym_v, sym_h, sym_r;
		float[] x_n, y_n, b_n, h_n, t_n, r_n;

		DecimalFormat df = new DecimalFormat("###.###");
		df.setRoundingMode(RoundingMode.CEILING);
		
		for(int i = 0; i < 4; i++)
		{
			symmetry_x[i] = Float.parseFloat(df.format(symmetry_x[i]).replace(",", "."));
			symmetry_y[i] = Float.parseFloat(df.format(symmetry_y[i]).replace(",", "."));
			symmetry_h[i] = Float.parseFloat(df.format(symmetry_h[i]).replace(",", "."));
			symmetry_b[i] = Float.parseFloat(df.format(symmetry_b[i]).replace(",", "."));
			symmetry_t[i] = Float.parseFloat(df.format(symmetry_t[i]).replace(",", "."));
			symmetry_r[i] = Float.parseFloat(df.format(symmetry_r[i]).replace(",", "."));
		}
		
		x_n = Normalize(symmetry_x);
		y_n = Normalize(symmetry_y);
		h_n = Normalize(symmetry_h);
		b_n = Normalize(symmetry_b);
		t_n = Normalize(symmetry_t);
		r_n = Normalize(symmetry_r);
		
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
		
		for(int i = 0; i < sequence_w.length; i++)
			sequence_w[i] *= q[i];
		
		List<Float> w_sorted = Arrays.asList(sequence_w[0], sequence_w[1], sequence_w[2], sequence_w[3]);
		Collections.sort(w_sorted);
		
		for(int i = 0; i < v.length; i++)
			v[i] = w_sorted.indexOf(sequence_w[i]) + 1;
			
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
		
		cm_fo = cohesion_ffoSum / n;
		cm_lo = cohesion_floSum / n;
		
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
		int n = SmartLayout.components.size();
		
		um_form = 1 - (unity_sizeCount - 1) / n;
		
		if(frameWidth * frameHeight == unity_totalAreas)
			um_space = 1;
		else
			um_space = 1 - ((layoutWidth * layoutHeight - unity_totalAreas) / (frameWidth * frameHeight - unity_totalAreas));
		
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
		
		int n = SmartLayout.components.size();
		pm_object = proportion_tempObjectTotal / n;
		
		proportion = (Math.abs(pm_layout) + Math.abs(pm_object)) / 2;
		
		//System.out.println("Proportion: " + proportion);
		
		return proportion;
	}

	private float MeasureSimplicity()
	{
		float simplicity;
		float n = SmartLayout.components.size();
		
		simplicity = 3 / (simplicity_n_vap + simplicity_n_hap + n);
		
		//System.out.println("Simplicity: " + simplicity);
		
		return simplicity;
	}

	private float MeasureDensity()
	{
		float density;
		
		density = 1 - density_totalAreas / (layoutWidth * layoutHeight);
		
		//System.out.println("Density: " + density);
		
		return density;
	}

	private float MeasureRegularity()
	{
		float regularity;
		float rm_alignment, rm_spacing;
		
		float n = SmartLayout.components.size();
		float n_spacing;
		
		ArrayList<Integer> allSpacings;
		allSpacings = new ArrayList<Integer>();
		
		n_spacing = 1;
		
		Collections.sort(allSpacings);
		
		for(int i = 0; i < allSpacings.size() - 1; i ++)
		{
			if(allSpacings.get(i) != allSpacings.get(i + 1))
				n_spacing++;
		}
		
		rm_alignment = 1 - (regularity_n_vap + regularity_n_hap) / (2 * n);
		rm_spacing = n != 1 ? 1 - (n_spacing - 1) / (2 * (n - 1)) : 1;
		
		regularity = (Math.abs(rm_alignment) + Math.abs(rm_spacing)) / 2;
		
		//System.out.println("Regularity: " + regularity);
		
		return regularity;
	}

	private float MeasureEconomy()
	{
		float economy;
		
		economy = 1 / economy_sizeCount;
		
		//System.out.println("Economy: " + economy);
		
		return economy;
	}

	private float MeasureHomogeneity()
	{
		float homogeneity;
		float w, w_max;
		float n;
		
		n = homogenity_n[0] + homogenity_n[1] + homogenity_n[2] + homogenity_n[3];
		
		w = (float) (Gamma.gamma(n + 1) / (Gamma.gamma(homogenity_n[0] + 1) * Gamma.gamma(homogenity_n[1] + 1)
				* Gamma.gamma(homogenity_n[2] + 1) * Gamma.gamma(homogenity_n[3] + 1)));
		w_max = (float) (Gamma.gamma(n + 1) / (Math.pow(Gamma.gamma((n + 1) / 4), 4)));
		
		homogeneity = w / w_max;
		
		//System.out.println("Homogeneity: " + homogeneity);
		
		return homogeneity;
	}

	private float MeasureRhythm()
	{
		float rhythm;
		float rhm_x, rhm_y, rhm_a;
		float[] x_n, y_n, a_n;

		DecimalFormat df = new DecimalFormat("###.###");
		df.setRoundingMode(RoundingMode.CEILING);
		
		for(int i = 0; i < 4; i++)
		{
			rhythm_x[i] = Float.parseFloat(df.format(rhythm_x[i]).replace(",", "."));
			rhythm_y[i] = Float.parseFloat(df.format(rhythm_y[i]).replace(",", "."));
			rhythm_a[i] = Float.parseFloat(df.format(rhythm_a[i]).replace(",", "."));
		}
		
		x_n = Normalize(rhythm_x);
		y_n = Normalize(rhythm_y);
		a_n = Normalize(rhythm_a);
		
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
		
		//Bağımsızların atamaları
		equilibrium_centerFactors = new float[2];
		symmetry_x = new float[4]; symmetry_y = new float[4];
		symmetry_b = new float[4]; symmetry_h = new float[4];
		symmetry_t = new float[4]; symmetry_r = new float[4];
		sequence_w = new float[4];
		float cohesion_frameRate = (float) frameHeight / (float) frameWidth;
		float cohesion_layoutRate = (float) layoutHeight / (float) layoutWidth;
		ArrayList<Coordinate> unity_sizes = new ArrayList<Coordinate>();
		float[] proportion_p_j = {1f, 1/1.414f, 1/1.618f, 1/1.732f, 1/2f};
		ArrayList<Integer> simplicity_allVaps = new ArrayList<Integer>();
		ArrayList<Integer> simplicity_allHaps = new ArrayList<Integer>();
		homogenity_n = new float[4];
		rhythm_x = new float[4];
		rhythm_y = new float[4];
		rhythm_a = new float[4];
		
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
			
			//Bağımsızların hesaplanması
			equilibrium_totalComponentArea += data.getTotalArea();
			equilibrium_centerFactors[0] += data.getTotalArea() * (data.getCenterX() - frameCenterX);
			equilibrium_centerFactors[1] += data.getTotalArea() * (data.getCenterY() - frameCenterY);
			
			float cohesion_t_fo, cohesion_t_lo;
			float cohesion_f_fo, cohesion_f_lo;
			float objectRate = (float) component.getAssignedHeight() / (float) component.getAssignedWidth();
			
			cohesion_t_fo = objectRate / cohesion_frameRate;
			cohesion_t_lo = objectRate / cohesion_layoutRate;
			
			if(cohesion_t_fo <= 1)
				cohesion_f_fo = cohesion_t_fo;
			else
				cohesion_f_fo = 1 / cohesion_t_fo;
			
			if(cohesion_t_lo <= 1)
				cohesion_f_lo = cohesion_t_lo;
			else
				cohesion_f_lo = 1 / cohesion_t_lo;
			
			cohesion_ffoSum += cohesion_f_fo;
			cohesion_floSum += cohesion_f_lo;
			
			Coordinate size = new Coordinate(component.getAssignedWidth(), component.getAssignedHeight());
			unity_sizes.add(size);
			
			float proportion_r_i = (float) component.getAssignedHeight() / (float) component.getAssignedWidth();
			float proportion_p_i = proportion_r_i <= 1 ? proportion_r_i : 1 / proportion_r_i;
			
			float proportion_min_p = Float.POSITIVE_INFINITY;
			
			for(int i = 0; i < 5; i++)
			{
				float proportion_p = Math.abs(proportion_p_j[i] - proportion_p_i);
				
				if(proportion_p < proportion_min_p)
					proportion_min_p = proportion_p;
			}
			
			proportion_tempObjectTotal += 1 - proportion_min_p / 0.5f;
			
			simplicity_allVaps.add(component.getAssignedY());
			simplicity_allVaps.add(component.getAssignedY() + component.getAssignedHeight());
			simplicity_allHaps.add(component.getAssignedX());
			simplicity_allHaps.add(component.getAssignedX() + component.getAssignedWidth());
			
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
					
					return;
				}
				
				float centerDifferenceX = quadrantData.getCenterX() - frameCenterX;
				float centerDifferenceY = quadrantData.getCenterY() - frameCenterY;
				
				symmetry_x[quadrantIndex] += Math.abs(centerDifferenceX);
				symmetry_y[quadrantIndex] += Math.abs(centerDifferenceY);
				symmetry_h[quadrantIndex] += quadrantData.getHeight();
				symmetry_b[quadrantIndex] += quadrantData.getWidth();
				symmetry_t[quadrantIndex] += Math.abs(centerDifferenceY / centerDifferenceX);
				symmetry_r[quadrantIndex] += Math.sqrt(Math.pow(centerDifferenceX, 2) + Math.pow(centerDifferenceY, 2));
				
				sequence_w[quadrantIndex] += quadrantData.getArea();
				
				if(quadrantData.getQuadrantPosition() == QuadrantEnum.UL) { homogenity_n[0]++; }
				else if(quadrantData.getQuadrantPosition() == QuadrantEnum.UR) { homogenity_n[1]++; }
				else if(quadrantData.getQuadrantPosition() == QuadrantEnum.LL) { homogenity_n[2]++; }
				else if(quadrantData.getQuadrantPosition() == QuadrantEnum.LR) { homogenity_n[3]++; }
				
				rhythm_x[quadrantIndex] += symmetry_x[quadrantIndex];
				rhythm_y[quadrantIndex] += symmetry_y[quadrantIndex];
				rhythm_a[quadrantIndex] += quadrantData.getArea();
			}
		}
		
		unity_totalAreas = equilibrium_totalComponentArea;
		density_totalAreas = equilibrium_totalComponentArea;
		
		Collections.sort(unity_sizes);
		Collections.sort(simplicity_allVaps);
		Collections.sort(simplicity_allHaps);
		
		balance_w = new float[4];
		
		int unity_sizeCounter = 0;
		int simplicity_counter = 0;
		
		for(LayoutComponent component: SmartLayout.components)
		{
			ComponentData data = componentDataMap.get(component);
			
			balance_w[0] += data.getLeftDistance() * (data.getLeftArea() / maxAreas[0]);
			balance_w[1] += data.getRightDistance() * (data.getRightArea() / maxAreas[1]);
			balance_w[2] += data.getTopDistance() * (data.getTopArea() / maxAreas[2]);
			balance_w[3] += data.getBottomDistance() * (data.getBottomArea() / maxAreas[3]);
			
			if(unity_sizeCounter < unity_sizes.size() - 1)
			{
				if(unity_sizes.get(unity_sizeCounter).getX() != unity_sizes.get(unity_sizeCounter + 1).getX())
				{
					unity_sizeCount++;
					economy_sizeCount++;
				}
				else
				{
					if(unity_sizes.get(unity_sizeCounter).getY() != unity_sizes.get(unity_sizeCounter + 1).getY())
					{
						unity_sizeCount++;
						economy_sizeCount++;
					}
				}
			}
			
			if(simplicity_counter < simplicity_allVaps.size() - 1)
			{
				if(simplicity_allVaps.get(simplicity_counter) != simplicity_allVaps.get(simplicity_counter + 1))
				{
					simplicity_n_vap++;
					regularity_n_vap++;
				}

				if(simplicity_allHaps.get(simplicity_counter) != simplicity_allHaps.get(simplicity_counter + 1))
				{
					simplicity_n_hap++;
					regularity_n_hap++;
				}
			}
			
			if(simplicity_counter < simplicity_allVaps.size() - 2)
			{
				if(simplicity_allVaps.get(simplicity_counter + 1) != simplicity_allVaps.get(simplicity_counter + 2))
				{
					simplicity_n_vap++;
					regularity_n_vap++;
				}

				if(simplicity_allHaps.get(simplicity_counter + 1) != simplicity_allHaps.get(simplicity_counter + 2))
				{
					simplicity_n_hap++;
					regularity_n_hap++;
				}
			}
			
			unity_sizeCounter++;
			simplicity_counter += 2;
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


