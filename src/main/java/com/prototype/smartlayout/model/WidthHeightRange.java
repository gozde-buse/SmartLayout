package com.prototype.smartlayout.model;

import com.prototype.smartlayout.model.enums.ComponentDimensionEnum;
import com.prototype.smartlayout.model.enums.OrientationEnum;
import java.util.Vector;
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
	private Vector<WidthHeightRange> subRanges;
	
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
		//subRanges = new Vector<WidthHeightRange>();//Null olunca hata veriyor. Listelemede. Daha sonra başka yerlerde de hata veriyorsa null kontrolü yapılacak.
	}
	
	public WidthHeightRange(int minWidth, int maxWidth, int minHeight, int maxHeight)
	{
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		
		orientation = OrientationEnum.SINGLE;
		//subRanges = new Vector<WidthHeightRange>();//Null olunca hata veriyor. Listelemede. Daha sonra başka yerlerde de hata veriyorsa null kontrolü yapılacak.
	}
	
	public WidthHeightRange(int minWidth, int maxWidth, int minHeight, int maxHeight, OrientationEnum orientation)
	{
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		
		this.orientation = orientation;
		subRanges = new Vector<WidthHeightRange>();
	}
	
	public WidthHeightRange(WidthHeightRange widthHeightRange, OrientationEnum orientation)
	{
		minWidth = widthHeightRange.minWidth;
		maxWidth = widthHeightRange.maxWidth;
		minHeight = widthHeightRange.minHeight;
		maxHeight = widthHeightRange.maxHeight;

		hasFiller = widthHeightRange.isHasFiller();
		
		this.orientation = orientation;
		subRanges = new Vector<WidthHeightRange>();
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	private ComponentDimensionEnum size;

	public WidthHeightRange (
			OrientationEnum orientationStrategy,
			int minWidth,
			int maxWidth,
			int minHeight,
			int maxHeight) {
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		subRanges = new Vector<>();
		this.orientation = orientationStrategy;
	}

	public WidthHeightRange (OrientationEnum orientationStrategy, ComponentDimensionEnum item) {
		this.size = item;
		this.minWidth = item.minWidth;
		this.maxWidth = item.maxWidth;
		this.minHeight = item.minHeight;
		this.maxHeight = item.maxHeight;
		subRanges = new Vector<>();
		this.orientation = orientationStrategy;
	}

	public WidthHeightRange (WidthHeightRange widthHeightRange) {
		minWidth = widthHeightRange.getMinWidth();
		maxWidth = widthHeightRange.getMaxWidth();
		minHeight = widthHeightRange.getMinHeight();
		maxHeight = widthHeightRange.getMaxHeight();
		subRanges = new Vector<>();
		orientation = widthHeightRange.getOrientation();
	}

	public void addSubRange (WidthHeightRange whr) {
		subRanges.add(whr);
	}

	public void addSubRanges (Vector<WidthHeightRange> subRangeVector) {
		subRanges.addAll(subRangeVector);
	}

	private String getOrientationStrategyString () {
		if (getOrientation() == OrientationEnum.SINGLE) {
			return "S";
		} else if (getOrientation() == OrientationEnum.HORIZONTAL) {
			return "H";
		} else if (getOrientation() == OrientationEnum.VERTICAL) {
			return "V";
		} else if (getOrientation() == OrientationEnum.OTHER) {
			return "O";
		} else {
			return "!!!";
		}
	}

	private String getOrientationTreeString () {
		StringBuilder builder = new StringBuilder("<");
		builder.append(getOrientationStrategyString());
		if (!subRanges.isEmpty()) {
			for (WidthHeightRange whr : subRanges) {
				builder.append(whr.getOrientationTreeString());
			}
		}
		builder.append(">");
		return builder.toString();
	}

	@Override
	public String toString () {
		return "{"
				+ getOrientationTreeString()
				+ "("
				+ minWidth
				+ ","
				+ maxWidth
				+ ")-("
				+ minHeight
				+ ","
				+ maxHeight
				+ ")}";
	}
}
