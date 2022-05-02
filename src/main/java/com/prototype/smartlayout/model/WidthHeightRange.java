package com.prototype.smartlayout.model;

import com.prototype.smartlayout.model.enums.ComponentDimensionEnum;
import com.prototype.smartlayout.model.enums.OrientationEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WidthHeightRange
{
	private int minWidth;
	private int maxWidth;
	private int minHeight;
	private int maxHeight;
	
	private OrientationEnum orientation;
	private WidthHeightRange[] subRanges;
	private int[] minWidthValues;
	private int[] maxWidthValues;
	private int[] minHeightValues;
	private int[] maxHeightValues;
	
	private boolean hasFiller = false;
	private boolean hasVerticalScroll = false;
	private boolean hasHorizontalScroll = false;
	
	public WidthHeightRange(ComponentDimensionEnum size)
	{
		minWidth = size.minWidth;
		maxWidth = size.maxWidth;
		minHeight = size.minHeight;
		maxHeight = size.maxHeight;
		
		orientation = OrientationEnum.SINGLE;
	}
	
	public WidthHeightRange(int minWidth, int maxWidth, int minHeight, int maxHeight)
	{
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		
		orientation = OrientationEnum.SINGLE;
	}
	
	public WidthHeightRange(int minWidth, int maxWidth, int minHeight, int maxHeight, OrientationEnum orientation)
	{
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		
		this.orientation = orientation;
	}

	public boolean TestFeasiblity(int w, int h)
	{
		if (minWidth > w || maxWidth < w) { return false; }
		if (minHeight > h || maxHeight < h) { return false; }
		
		return true;
	}
	
	public boolean TestVerticalScrollNeed(int h)
	{
		if (minHeight > h) { return true; }
		
		return false;
	}
	
	public boolean TestHorizontalScrollNeed(int w)
	{
		if (minWidth > w) { return true; }
		
		return false;
	}
	
	public boolean TestFillerNeed(int w, int h)
	{
		if (maxWidth < w || maxHeight < h) { return true; }
		
		return false;
	}
	
	public void ListRange(int index)
	{
		for(int i = 0; i < index; i++)
		{
			System.out.print("\t");
		}
		
		System.out.print(orientation.toString().charAt(0) + "[w(" + minWidth + ", " + maxWidth + "), h(" + minHeight + ", " + maxHeight + ")]\n");
		
		if(subRanges == null)
			return;
		
		for(WidthHeightRange subRange: subRanges)
		{
			subRange.ListRange(index + 1);
		}
	}
	
	public String ToString()
	{
		return orientation.toString().charAt(0) + "[w(" + minWidth + ", " + maxWidth + "), h(" + minHeight + ", " + maxHeight + ")]";
	}
	
	public void CreateSubRangeValues(int length)
	{
		subRanges = new WidthHeightRange[length];
		minWidthValues = new int[length];
		maxWidthValues = new int[length];
		minHeightValues = new int[length];
		maxHeightValues = new int[length];
	}
	
	public void AddSubRangeValues(int index, WidthHeightRange subRange)
	{
		subRanges[index] = subRange;
		minWidthValues[index] = subRange.getMinWidth();
		maxWidthValues[index] = subRange.getMaxWidth();
		minHeightValues[index] = subRange.getMinHeight();
		maxHeightValues[index] = subRange.getMaxHeight();
	}
	
	public void AddAllSubRangeValues(WidthHeightRange tempRange)
	{
		subRanges = tempRange.getSubRanges();
		minWidthValues = tempRange.getMinWidthValues();
		maxWidthValues = tempRange.getMaxWidthValues();
		minHeightValues = tempRange.getMinHeightValues();
		maxHeightValues = tempRange.getMaxHeightValues();
	}
}
