package com.prototype.smartlayout.utils;

import com.prototype.smartlayout.SmartLayout;
import com.prototype.smartlayout.aesthetic.ComponentData;
import com.prototype.smartlayout.model.Coordinate;
import com.prototype.smartlayout.model.LayoutComponent;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


import org.apache.commons.math3.special.Gamma;

/**
 * These measures does not include color shape etc.
 * formulas gained from <site>http://www.mi.sanu.ac.rs/vismath/ngo/index.html</site>
 */
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
	
	public HashMap<LayoutComponent, ComponentData> componentDataMap;
	public ArrayList<ComponentData> datas;
	public float[] maxAreas;
	
	public float cohesion_frameRate;
	public float cohesion_layoutRate;
	
	public float[] proportion_p_j = {1f, 1/1.414f, 1/1.618f, 1/1.732f, 1/2f};

	public ArrayList<Coordinate> unity_sizes = new ArrayList<Coordinate>();
	public ArrayList<Integer> simplicity_allVaps = new ArrayList<Integer>();
	public ArrayList<Integer> simplicity_allHaps = new ArrayList<Integer>();
	
	public int componentSize;
	
	public int frameWidth;
	public int frameHeight;
	public int frameCenterX;
	public int frameCenterY;
	
	public int layoutWidth;
	public int layoutHeight;
	
	private float[] balance_w;
	public float equilibrium_totalComponentArea;
	public float[] equilibrium_centerFactors;
	public float[] symmetry_x, symmetry_y, symmetry_h, symmetry_b, symmetry_t, symmetry_r;
	public float[] sequence_w;
	public float cohesion_ffoSum, cohesion_floSum;
	public float unity_totalAreas, unity_sizeCount;
	public float proportion_tempObjectTotal;
	public float simplicity_n_vap, simplicity_n_hap;
	public float density_totalAreas;
	public float regularity_n_vap, regularity_n_hap;
	public float economy_sizeCount;
	public float[] homogenity_n;
	public float[] rhythm_x, rhythm_y, rhythm_a;
	
	public AestheticMeasureUtil(int frameWidth, int frameHeight, int layoutWidth, int layoutHeight)
	{
		aestheticMeasure = this;
		
		componentSize = SmartLayout.components.size();
		
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		this.frameCenterX = frameWidth / 2;
		this.frameCenterY = frameHeight / 2;
		
		this.layoutWidth = layoutWidth;
		this.layoutHeight = layoutHeight;
		
		cohesion_frameRate = (float) frameHeight / (float) frameWidth;
		cohesion_layoutRate = (float) layoutHeight / (float) layoutWidth;
		
		componentDataMap = new HashMap<LayoutComponent, ComponentData>();
		datas = new ArrayList<ComponentData>();
		maxAreas = new float[4];
		maxAreas[0] = maxAreas[1] = maxAreas[2] = maxAreas[3] = 0;

		unity_sizeCount = 1;
		simplicity_n_vap = simplicity_n_hap = 1;
		regularity_n_vap = regularity_n_hap = 1;
		economy_sizeCount = 1;
		
		balance_w = new float[4];
		equilibrium_centerFactors = new float[2];
		symmetry_x = new float[4]; symmetry_y = new float[4];
		symmetry_b = new float[4]; symmetry_h = new float[4];
		symmetry_t = new float[4]; symmetry_r = new float[4];
		sequence_w = new float[4];
		homogenity_n = new float[4];
		rhythm_x = new float[4];
		rhythm_y = new float[4];
		rhythm_a = new float[4];
	}
	
	public float MeasureAesthetics()
	{
		//return 1;
		
		CalculateComponentAreas();
		
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


