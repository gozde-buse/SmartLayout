package com.prototype.smartlayout.model;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.prototype.smartlayout.SmartLayout;
import com.prototype.smartlayout.aesthetic.ComponentData;
import com.prototype.smartlayout.aesthetic.QuadrantData;
import com.prototype.smartlayout.aesthetic.QuadrantEnum;
import com.prototype.smartlayout.model.enums.ComponentDimensionEnum;
import com.prototype.smartlayout.utils.AestheticMeasureUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LayoutComponent implements Layoutable
{
	private final JComponent jcomponent;
	private final String label;
	private final WidthHeightRange widthHeightRange;
	private LayoutContainer parent;
	
	private int assignedX;
	private int assignedY;
	private int assignedWidth;
	private int assignedHeight;
	
	public LayoutComponent(String label, JComponent jcomponent, ComponentDimensionEnum size)
	{
		this.label = label;
		this.jcomponent = jcomponent;
		widthHeightRange = new WidthHeightRange(size);
	}
	
	public LayoutComponent(String label, JComponent jcomponent, WidthHeightRange widthHeightRange)
	{
		this.label = label;
		this.jcomponent = jcomponent;
		this.widthHeightRange = widthHeightRange;
	}
	
	@Override
	public ExtendedArray<WidthHeightRange> GetRanges()
	{
		ExtendedArray<WidthHeightRange> vec = new ExtendedArray<WidthHeightRange>(WidthHeightRange.class, 1);
		vec.set(widthHeightRange, 0);
		
		if(Control(vec.get(0)))
			return vec;
		
		return null;
	}

	@Override
	public void GetFinalLayout(int x, int y, int w, int h, WidthHeightRange whr)
	{
		if(whr.isHasFiller())
		{
			int[] newValues = DistributeFiller(x, y, w, h, whr);
			
			x = newValues[0];
			y = newValues[1];
			w = newValues[2];
			h = newValues[3];
		}
		
		setAssignedX(x);
		setAssignedY(y);
		setAssignedWidth(w);
		setAssignedHeight(h);
		
		CreateAestheticData();
	}
	
	private int[] DistributeFiller(int x, int y, int w, int h, WidthHeightRange whr)
	{
		if(whr.getMaxWidth() < w)
		{
			int fillerWidth, fillerHeight, fillerX, fillerY;
			WidthHeightRange fillerWHR;
			LayoutComponent filler;
			
			switch(SmartLayout.app.horizontalAlignment)
			{
			case START:
			default:
				
				fillerWidth = w - whr.getMaxWidth();
				fillerHeight = h;
				fillerX = x + whr.getMaxWidth();
				fillerY = y;
				
				fillerWHR = new WidthHeightRange(fillerWidth, fillerWidth, fillerHeight, fillerHeight);
				filler = new LayoutComponent("FILLER", new JPanel(), fillerWHR);
				SmartLayout.AddFiller(filler);
				filler.setParent(parent);
				filler.GetFinalLayout(fillerX, fillerY, fillerWidth, fillerHeight, fillerWHR);
				
				w = whr.getMaxWidth();
				
				break;
				
			case CENTER:
				
				fillerWidth = (int) Math.floor((w - whr.getMaxWidth()) / 2);
				fillerHeight = h;
				fillerX = x;
				fillerY = y;
				
				fillerWHR = new WidthHeightRange(fillerWidth, fillerWidth, fillerHeight, fillerHeight);
				filler = new LayoutComponent("FILLER", new JPanel(), fillerWHR);
				SmartLayout.AddFiller(filler);
				filler.setParent(parent);
				filler.GetFinalLayout(fillerX, fillerY, fillerWidth, fillerHeight, fillerWHR);
				
				int fillerWidth2 = w - whr.getMaxWidth() - fillerWidth;
				int fillerHeight2 = h;
				int fillerX2 = x + fillerWidth + whr.getMaxWidth();
				int fillerY2 = y;
				
				WidthHeightRange fillerWHR2 = new WidthHeightRange(fillerWidth2, fillerWidth2, fillerHeight2, fillerHeight2);
				LayoutComponent filler2 = new LayoutComponent("FILLER", new JPanel(), fillerWHR2);
				SmartLayout.AddFiller(filler2);
				filler2.setParent(parent);
				filler2.GetFinalLayout(fillerX2, fillerY2, fillerWidth2, fillerHeight2, fillerWHR2);
				
				x += fillerWidth;
				w = whr.getMaxWidth();
				
				break;
				
			case END:
				
				fillerWidth = w - whr.getMaxWidth();
				fillerHeight = h;
				fillerX = x;
				fillerY = y;
				
				fillerWHR = new WidthHeightRange(fillerWidth, fillerWidth, fillerHeight, fillerHeight);
				filler = new LayoutComponent("FILLER", new JPanel(), fillerWHR);
				SmartLayout.AddFiller(filler);
				filler.setParent(parent);
				filler.GetFinalLayout(fillerX, fillerY, fillerWidth, fillerHeight, fillerWHR);
				
				x += fillerWidth;
				w = whr.getMaxWidth();
				
				break;
			}
		}
		
		if(whr.getMaxHeight() < h)
		{
			int fillerWidth, fillerHeight, fillerX, fillerY;
			WidthHeightRange fillerWHR;
			LayoutComponent filler;

			switch(SmartLayout.app.horizontalAlignment)
			{
			case START:
			default:
				
				fillerHeight = h - whr.getMaxHeight();
				fillerWidth = w;
				fillerY = y + whr.getMaxHeight();
				fillerX = x;
				
				fillerWHR = new WidthHeightRange(fillerWidth, fillerWidth, fillerHeight, fillerHeight);
				filler = new LayoutComponent("FILLER", new JPanel(), fillerWHR);
				SmartLayout.AddFiller(filler);
				filler.setParent(parent);
				filler.GetFinalLayout(fillerX, fillerY, fillerWidth, fillerHeight, fillerWHR);
				
				h = whr.getMaxHeight();
				
				break;
				
			case CENTER:
				
				fillerHeight = (int) Math.floor((h - whr.getMaxHeight()) / 2);
				fillerWidth = w;
				fillerY = y;
				fillerX = x;
				
				fillerWHR = new WidthHeightRange(fillerWidth, fillerWidth, fillerHeight, fillerHeight);
				filler = new LayoutComponent("FILLER", new JPanel(), fillerWHR);
				SmartLayout.AddFiller(filler);
				filler.setParent(parent);
				filler.GetFinalLayout(fillerX, fillerY, fillerWidth, fillerHeight, fillerWHR);
				
				int fillerHeight2 = h - whr.getMaxHeight() - fillerHeight;
				int fillerWidth2 = w;
				int fillerY2 = y + fillerHeight + whr.getMaxHeight();
				int fillerX2 = x;
				
				WidthHeightRange fillerWHR2 = new WidthHeightRange(fillerWidth2, fillerWidth2, fillerHeight2, fillerHeight2);
				LayoutComponent filler2 = new LayoutComponent("FILLER", new JPanel(), fillerWHR2);
				SmartLayout.AddFiller(filler2);
				filler2.setParent(parent);
				filler2.GetFinalLayout(fillerX2, fillerY2, fillerWidth2, fillerHeight2, fillerWHR2);

				y += fillerHeight;
				h = whr.getMaxHeight();
				
				break;
				
			case END:
				
				fillerHeight = h - whr.getMaxHeight();
				fillerWidth = w;
				fillerY = y;
				fillerX = x;
				
				fillerWHR = new WidthHeightRange(fillerWidth, fillerWidth, fillerHeight, fillerHeight);
				filler = new LayoutComponent("FILLER", new JPanel(), fillerWHR);
				SmartLayout.AddFiller(filler);
				filler.setParent(parent);
				filler.GetFinalLayout(fillerX, fillerY, fillerWidth, fillerHeight, fillerWHR);

				y += fillerHeight;
				h = whr.getMaxHeight();
				
				break;
			}
		}
		
		int[] newValues = {x, y, w, h};
		return newValues;
	}
	
	private boolean Control(WidthHeightRange range)
	{
		int width = SmartLayout.app.width;
		int height = SmartLayout.app.height;
		
		boolean verticalScrollNeed = range.TestVerticalScrollNeed(height);
		boolean horizontalScrollNeed = range.TestHorizontalScrollNeed(width);
		
		switch(SmartLayout.app.processType)
		{
		case FEASIBLE:
			
			return !range.isHasFiller();
			
		case FILLER:
			
			return !verticalScrollNeed && !horizontalScrollNeed;
			
		case VERTICAL_SCROLL:
			
			return !horizontalScrollNeed;
			
		case HORIZONTAL_SCROLL:
			
			return !verticalScrollNeed;
			
		case NO_PREFERENCE:
			
			return true;
			
		default:
			
			return false;
		}
	}
	
	private void CreateAestheticData()
	{
		float totalArea, leftArea, rightArea, topArea, bottomArea;
		float distance, horizontalDistance, verticalDistance, leftDistance, rightDistance, topDistance, bottomDistance;
		
		float left, right, top, bottom, width, height;
		float centerX, centerY;
		
		width = getAssignedWidth();
		height = getAssignedHeight();
		
		left = getAssignedX();
		right = left + width;
		top = getAssignedY();
		bottom = top + height;
		
		centerX = (right + left) / 2;
		centerY = (bottom + top) / 2;
		
		if(left < AestheticMeasureUtil.aestheticMeasure.frameCenterX)
		{
			if(right < AestheticMeasureUtil.aestheticMeasure.frameCenterX)
			{
				leftArea = width * height;
				rightArea = 0;
				
				leftDistance = AestheticMeasureUtil.aestheticMeasure.frameCenterX - centerX;
				rightDistance = 0;
			}
			else
			{
				float leftWidth = AestheticMeasureUtil.aestheticMeasure.frameCenterX - left;
				float rightWidth = right - AestheticMeasureUtil.aestheticMeasure.frameCenterX;
				
				leftArea = leftWidth * height;
				rightArea = rightWidth * height;
				
				float leftCenterX = (AestheticMeasureUtil.aestheticMeasure.frameCenterX + left) / 2;
				float rightCenterX = (AestheticMeasureUtil.aestheticMeasure.frameCenterX + right) / 2;
				
				leftDistance = AestheticMeasureUtil.aestheticMeasure.frameCenterX - leftCenterX;
				rightDistance = rightCenterX - AestheticMeasureUtil.aestheticMeasure.frameCenterX;
			}
		}
		else
		{
			leftArea = 0;
			rightArea = width * height;
			
			leftDistance = 0;
			rightDistance = centerX - AestheticMeasureUtil.aestheticMeasure.frameCenterX;
		}
		
		if(top < AestheticMeasureUtil.aestheticMeasure.frameCenterY)
		{
			if(bottom < AestheticMeasureUtil.aestheticMeasure.frameCenterY)
			{
				topArea = width * height;
				bottomArea = 0;
				
				topDistance = AestheticMeasureUtil.aestheticMeasure.frameCenterY - centerY;
				bottomDistance = 0;
			}
			else
			{
				float topHeight = AestheticMeasureUtil.aestheticMeasure.frameCenterY - top;
				float bottomHeight = bottom - AestheticMeasureUtil.aestheticMeasure.frameCenterY;
				
				topArea = width * topHeight;
				bottomArea = width * bottomHeight;
				
				float topCenterY = (AestheticMeasureUtil.aestheticMeasure.frameCenterY + top) / 2;
				float bottomCenterY = (AestheticMeasureUtil.aestheticMeasure.frameCenterY + bottom) / 2;
				
				topDistance = AestheticMeasureUtil.aestheticMeasure.frameCenterY - topCenterY;
				bottomDistance = bottomCenterY - AestheticMeasureUtil.aestheticMeasure.frameCenterY;
			}
		}
		else
		{
			topArea = 0;
			bottomArea = width * height;
			
			topDistance = 0;
			bottomDistance = centerY - AestheticMeasureUtil.aestheticMeasure.frameCenterY;
		}
		
		if(AestheticMeasureUtil.aestheticMeasure.maxAreas[0] < leftArea)
			AestheticMeasureUtil.aestheticMeasure.maxAreas[0] = leftArea;
		
		if(AestheticMeasureUtil.aestheticMeasure.maxAreas[1] < rightArea)
			AestheticMeasureUtil.aestheticMeasure.maxAreas[1] = rightArea;
		
		if(AestheticMeasureUtil.aestheticMeasure.maxAreas[2] < topArea)
			AestheticMeasureUtil.aestheticMeasure.maxAreas[2] = topArea;
		
		if(AestheticMeasureUtil.aestheticMeasure.maxAreas[3] < bottomArea)
			AestheticMeasureUtil.aestheticMeasure.maxAreas[3] = bottomArea;
		
		totalArea = leftArea + rightArea;
		horizontalDistance = Math.abs(AestheticMeasureUtil.aestheticMeasure.frameCenterX - centerX);
		verticalDistance = Math.abs(AestheticMeasureUtil.aestheticMeasure.frameCenterY - centerY);
		distance = (float) Math.sqrt(Math.pow(horizontalDistance, 2) + Math.pow(verticalDistance, 2));
		
		ComponentData data = new ComponentData(totalArea, leftArea, rightArea, topArea, bottomArea, distance, horizontalDistance,
				verticalDistance, leftDistance, rightDistance, topDistance, bottomDistance, centerX, centerY);
		
		data.SetComponent(this);
		data.CalculateQuadrants(AestheticMeasureUtil.aestheticMeasure.frameCenterX, AestheticMeasureUtil.aestheticMeasure.frameCenterY);
		
		AestheticMeasureUtil.aestheticMeasure.componentDataMap.put(this, data);
		AestheticMeasureUtil.aestheticMeasure.datas.add(data);
		
		AestheticMeasureUtil.aestheticMeasure.equilibrium_totalComponentArea += data.getTotalArea();
		AestheticMeasureUtil.aestheticMeasure.equilibrium_centerFactors[0] += data.getTotalArea() * (data.getCenterX() - AestheticMeasureUtil.aestheticMeasure.frameCenterX);
		AestheticMeasureUtil.aestheticMeasure.equilibrium_centerFactors[1] += data.getTotalArea() * (data.getCenterY() - AestheticMeasureUtil.aestheticMeasure.frameCenterY);
		
		float cohesion_t_fo, cohesion_t_lo;
		float cohesion_f_fo, cohesion_f_lo;
		float objectRate = (float) getAssignedHeight() / (float) getAssignedWidth();
		
		cohesion_t_fo = objectRate / AestheticMeasureUtil.aestheticMeasure.cohesion_frameRate;
		cohesion_t_lo = objectRate / AestheticMeasureUtil.aestheticMeasure.cohesion_layoutRate;
		
		if(cohesion_t_fo <= 1)
			cohesion_f_fo = cohesion_t_fo;
		else
			cohesion_f_fo = 1 / cohesion_t_fo;
		
		if(cohesion_t_lo <= 1)
			cohesion_f_lo = cohesion_t_lo;
		else
			cohesion_f_lo = 1 / cohesion_t_lo;
		
		AestheticMeasureUtil.aestheticMeasure.cohesion_ffoSum += cohesion_f_fo;
		AestheticMeasureUtil.aestheticMeasure.cohesion_floSum += cohesion_f_lo;
		
		Coordinate size = new Coordinate(getAssignedWidth(), getAssignedHeight());
		AestheticMeasureUtil.aestheticMeasure.unity_sizes.add(size);
		
		float proportion_r_i = (float) getAssignedHeight() / (float) getAssignedWidth();
		float proportion_p_i = proportion_r_i <= 1 ? proportion_r_i : 1 / proportion_r_i;
		
		float proportion_min_p = Float.POSITIVE_INFINITY;
		
		for(int i = 0; i < 5; i++)
		{
			float proportion_p = Math.abs(AestheticMeasureUtil.aestheticMeasure.proportion_p_j[i] - proportion_p_i);
			
			if(proportion_p < proportion_min_p)
				proportion_min_p = proportion_p;
		}
		
		AestheticMeasureUtil.aestheticMeasure.proportion_tempObjectTotal += 1 - proportion_min_p / 0.5f;
		
		AestheticMeasureUtil.aestheticMeasure.simplicity_allVaps.add(getAssignedY());
		AestheticMeasureUtil.aestheticMeasure.simplicity_allVaps.add(getAssignedY() + getAssignedHeight());
		AestheticMeasureUtil.aestheticMeasure.simplicity_allHaps.add(getAssignedX());
		AestheticMeasureUtil.aestheticMeasure.simplicity_allHaps.add(getAssignedX() + getAssignedWidth());
		
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
			
			float centerDifferenceX = quadrantData.getCenterX() - AestheticMeasureUtil.aestheticMeasure.frameCenterX;
			float centerDifferenceY = quadrantData.getCenterY() - AestheticMeasureUtil.aestheticMeasure.frameCenterY;
			
			AestheticMeasureUtil.aestheticMeasure.symmetry_x[quadrantIndex] += Math.abs(centerDifferenceX);
			AestheticMeasureUtil.aestheticMeasure.symmetry_y[quadrantIndex] += Math.abs(centerDifferenceY);
			AestheticMeasureUtil.aestheticMeasure.symmetry_h[quadrantIndex] += quadrantData.getHeight();
			AestheticMeasureUtil.aestheticMeasure.symmetry_b[quadrantIndex] += quadrantData.getWidth();
			AestheticMeasureUtil.aestheticMeasure.symmetry_t[quadrantIndex] += Math.abs(centerDifferenceY / centerDifferenceX);
			AestheticMeasureUtil.aestheticMeasure.symmetry_r[quadrantIndex] += Math.sqrt(Math.pow(centerDifferenceX, 2) + Math.pow(centerDifferenceY, 2));
			
			AestheticMeasureUtil.aestheticMeasure.sequence_w[quadrantIndex] += quadrantData.getArea();
			
			if(quadrantData.getQuadrantPosition() == QuadrantEnum.UL) { AestheticMeasureUtil.aestheticMeasure.homogenity_n[0]++; }
			else if(quadrantData.getQuadrantPosition() == QuadrantEnum.UR) { AestheticMeasureUtil.aestheticMeasure.homogenity_n[1]++; }
			else if(quadrantData.getQuadrantPosition() == QuadrantEnum.LL) { AestheticMeasureUtil.aestheticMeasure.homogenity_n[2]++; }
			else if(quadrantData.getQuadrantPosition() == QuadrantEnum.LR) { AestheticMeasureUtil.aestheticMeasure.homogenity_n[3]++; }
			
			AestheticMeasureUtil.aestheticMeasure.rhythm_x[quadrantIndex] += AestheticMeasureUtil.aestheticMeasure.symmetry_x[quadrantIndex];
			AestheticMeasureUtil.aestheticMeasure.rhythm_y[quadrantIndex] += AestheticMeasureUtil.aestheticMeasure.symmetry_y[quadrantIndex];
			AestheticMeasureUtil.aestheticMeasure.rhythm_a[quadrantIndex] += quadrantData.getArea();
		}
	}
	
	@Override
	public void Print(int index)
	{
		for(int i = 0; i < index; i++)
		{
			System.out.print("  ");
		}
		
		System.out.print(label + "\n");
	}
}
