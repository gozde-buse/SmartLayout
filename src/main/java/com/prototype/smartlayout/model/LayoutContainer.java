package com.prototype.smartlayout.model;

import com.prototype.smartlayout.model.enums.OrientationEnum;
import com.prototype.smartlayout.utils.ArrayIndexComparator;

import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LayoutContainer implements Layoutable
{
	private final String label;
	private final Layoutable[] children;
	private ExtendedArray<WidthHeightRange> memory;
	private LayoutContainer parent;
	
	private int assignedX;
	private int assignedY;
	private int assignedWidth;
	private int assignedHeight;
	private OrientationEnum assignedOrientation;

	public LayoutContainer (String label, Layoutable... layoutables)
	{
		this.label = label;
		children = layoutables;
		
		for(Layoutable child: children) { child.setParent(this); }
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ExtendedArray<WidthHeightRange> GetRanges(int width, int height)
	{
		long rangeSize = 1;
		int childSize = children.length;
		ExtendedArray<WidthHeightRange>[] childRanges = new ExtendedArray[childSize];
		int[] currentIndices;
		long[] maxIndices;
		currentIndices = new int[childSize];
		maxIndices = new long[childSize];
		
		for (int i = 0; i < children.length; i++)
		{
			childRanges[i] = children[i].GetRanges(width, height);
			rangeSize *= childRanges[i].length;
			maxIndices[i] = childRanges[i].length;
			currentIndices[i] = 0;
		}
		
		ArrayIndexComparator comparator = new ArrayIndexComparator(maxIndices);
		Integer[] sortedIndices = comparator.createIndexArray();
		Arrays.sort(sortedIndices, comparator);
		
		ExtendedArray<WidthHeightRange> rangesH = new ExtendedArray<WidthHeightRange>(WidthHeightRange.class, rangeSize);
		ExtendedArray<WidthHeightRange> rangesV = new ExtendedArray<WidthHeightRange>(WidthHeightRange.class, rangeSize);
		
		int pivot = childSize - 1;
		
		for(int i = 0; i < rangeSize; i++)
		{
			int minWidthH, maxWidthH, minHeightH, maxHeightH;
			int minWidthV, maxWidthV, minHeightV, maxHeightV;
			
			minWidthH = maxWidthH = minHeightH = 0;
			maxHeightH = Integer.MAX_VALUE;
			
			minWidthV = maxHeightV = minHeightV = 0;
			maxWidthV = Integer.MAX_VALUE;
			
			WidthHeightRange tempRangeH = new WidthHeightRange();
			WidthHeightRange tempRangeV = new WidthHeightRange();
			tempRangeH.CreateSubRangeValues(childSize);
			tempRangeV.CreateSubRangeValues(childSize);
			
			for(int j = 0; j < childSize; j++)
			{
				WidthHeightRange childRange = childRanges[j].get(currentIndices[j]);
				
				minWidthH += childRange.getMinWidth();
				maxWidthH += childRange.getMaxWidth();
				minHeightH = Math.max(minHeightH, childRange.getMinHeight());
				maxHeightH = Math.min(maxHeightH, childRange.getMaxHeight());

				if (maxHeightH < minHeightH)
				{
					maxHeightH = minHeightH;
					tempRangeH.setHasFiller(true);
				}
				
				tempRangeH.AddSubRangeValues(j, childRange);
				
				minWidthV = Math.max(minWidthV, childRange.getMinWidth());
				maxWidthV = Math.min(maxWidthV, childRange.getMaxWidth());
				minHeightV += childRange.getMinHeight();
				maxHeightV += childRange.getMaxHeight();

				if (maxWidthV < minWidthV)
				{
					maxWidthV = minWidthV;
					tempRangeV.setHasFiller(true);
				}
				
				tempRangeV.AddSubRangeValues(j, childRange);
				
				if(childRange.isHasFiller())
				{
					tempRangeH.setHasFiller(true);
					tempRangeV.setHasFiller(true);
				}
			}
			
			WidthHeightRange rangeH = new WidthHeightRange(minWidthH, maxWidthH, minHeightH, maxHeightH, OrientationEnum.HORIZONTAL);
			rangeH.AddAllSubRangeValues(tempRangeH);
			rangesH.set(rangeH, i);

			WidthHeightRange rangeV = new WidthHeightRange(minWidthV, maxWidthV, minHeightV, maxHeightV, OrientationEnum.VERTICAL);
			rangeV.AddAllSubRangeValues(tempRangeV);
			rangesV.set(rangeV, i);

			currentIndices[sortedIndices[pivot]]++;
			
			if(currentIndices[sortedIndices[pivot]] >= maxIndices[sortedIndices[pivot]])
			{
				currentIndices[sortedIndices[pivot]] = 0;
				
				boolean newPivotFound = false;
				
				while(!newPivotFound)
				{
					pivot--;
					
					if(pivot < 0)
						break;

					currentIndices[sortedIndices[pivot]]++;
					
					if(currentIndices[sortedIndices[pivot]] >= maxIndices[sortedIndices[pivot]])
					{
						currentIndices[sortedIndices[pivot]] = 0;
					}
					else
					{
						newPivotFound = true;
						pivot = childSize - 1;
					}
				}
			}
		}

		ExtendedArray<WidthHeightRange> ranges = new ExtendedArray<WidthHeightRange>(WidthHeightRange.class, rangeSize * 2);
		ranges = rangesH.Merge(rangesH, rangesV);
		
		return ranges;
	}
	
	@Override
	public void GetFinalLayout(int x, int y, int w, int h, WidthHeightRange whr)
	{
		setAssignedX(x);
		setAssignedY(y);
		setAssignedWidth(w);
		setAssignedHeight(h);
		setAssignedOrientation(whr.getOrientation());
		
		WidthHeightRange[] subRanges = whr.getSubRanges();
		
		int remaining;
		int[] minValues, maxValues;
		
		if(whr.getOrientation() == OrientationEnum.HORIZONTAL)
		{
			remaining = w;
			minValues = whr.getMinWidthValues();
			maxValues = whr.getMaxWidthValues();
		}
		else if(whr.getOrientation() == OrientationEnum.VERTICAL)
		{
			remaining = h;
			minValues = whr.getMinHeightValues();
			maxValues = whr.getMaxHeightValues();
		}
		else
			//Bir sorun var demek!
			return;
		
		int[] distribution = StrategyBalance(remaining, minValues, maxValues);
		int distance = 0;
		
		int fillerUnit = 0;
		int fillerRemaining = 0;
		
		if(whr.getOrientation() == OrientationEnum.HORIZONTAL)
		{
			if(whr.isHasFiller() && whr.getMaxWidth() < w)
			{
				int fillerWidth = w - whr.getMaxWidth();
					
				fillerUnit = (int) Math.floor(fillerWidth / children.length);
				fillerRemaining = fillerWidth - fillerUnit * children.length;
			}
		}
		else if(whr.getOrientation() == OrientationEnum.VERTICAL)
		{
			if(whr.isHasFiller() && whr.getMaxHeight() < h)
			{
				int fillerHeight = h - whr.getMaxHeight();
					
				fillerUnit = (int) Math.floor(fillerHeight / children.length);
				fillerRemaining = fillerHeight - fillerUnit * children.length;
			}
		}
		
		for(int i = 0; i < children.length; i++)
		{
			int tempdistribution = distribution[i] + fillerUnit;
			
			if(i >= children.length - fillerRemaining)
				tempdistribution++;
			
			if(whr.getOrientation() == OrientationEnum.HORIZONTAL)
			{
				if(subRanges[i].getMaxWidth() < x + distance || subRanges[i].getMaxHeight() < h)
					subRanges[i].setHasFiller(true);
				
				children[i].GetFinalLayout(x + distance, y, tempdistribution, h, subRanges[i]);
			}
			else if(whr.getOrientation() == OrientationEnum.VERTICAL)
			{
				if(subRanges[i].getMaxWidth() < x || subRanges[i].getMaxHeight() < h + distance)
					subRanges[i].setHasFiller(true);
				
				children[i].GetFinalLayout(x, y + distance, w, tempdistribution, subRanges[i]);
			}
			
			distance += tempdistribution;
		}
	}	

	private int[] StrategyBalance (int remaining, int[] minValues, int[] maxValues)
	{
		int[] capacityValues = new int[maxValues.length];
		int[] distribution = new int[minValues.length];
		
		for (int i = 0; i < maxValues.length; i++)
		{
			capacityValues[i] = maxValues[i];
			distribution[i] = minValues[i];
			
			remaining -= minValues[i];
			capacityValues[i] -= minValues[i];
			
			if (remaining <= 0) { break; }
		}
		
		ArrayIndexComparator comparator = new ArrayIndexComparator(capacityValues);
		Integer[] indexOrder = comparator.createIndexArray();
		Arrays.sort(indexOrder, comparator);
		
		while (remaining > 0)
		{
			if(remaining < indexOrder.length)
			{
				for(int i = 0; i < indexOrder.length; i++)
				{
					int index = indexOrder[i];
					
					distribution[index]++;
					capacityValues[index]--;
					remaining--;
					
					if(remaining == 0) { break; }
				}
				
				break;
			}
			
			int tempDistribution = (int) Math.floor(remaining / indexOrder.length);
			int tempRemaining = remaining - tempDistribution * indexOrder.length;
			Integer[] tempIndexOrder = indexOrder;
			
			for(int i = 0; i < indexOrder.length; i++)
			{
				int index = indexOrder[i];
				
				if(capacityValues[index] >= tempDistribution)
				{
					capacityValues[index] -= tempDistribution;
					distribution[index] += tempDistribution;
				}
				else
				{
					tempRemaining += tempDistribution - capacityValues[index];
					distribution[index] += capacityValues[index];
					capacityValues[index] = 0;
				}
				
				if(capacityValues[index] == 0)
				{
					tempIndexOrder = Arrays.copyOfRange(tempIndexOrder, 1, tempIndexOrder.length);
				}
			}
			
			indexOrder = tempIndexOrder;
			remaining = tempRemaining;
			
			if(indexOrder.length == 0)
				break;
		}
		
		return distribution;
	}
	
	/*private boolean ControlLayoutProcess(WidthHeightRange range, int width, int height)
	{
		boolean fillerNeed = range.TestFillerNeed(width, height);
		boolean verticalScrollNeed = range.TestVerticalScrollNeed(height);
		boolean horizontalScrollNeed = range.TestHorizontalScrollNeed(width);
		
		switch(SmartLayout.app.process)
		{
		case FEASIBLE:
			
			return range.TestFeasiblity(width, height) && !range.isHasFiller();
			
		case FILLER:
			
			return fillerNeed && !(verticalScrollNeed || horizontalScrollNeed);
			
		case VERTICAL_SCROLL:
			
			return verticalScrollNeed && !horizontalScrollNeed;
			
		case HORIZONTAL_SCROLL:
			
			return !verticalScrollNeed && horizontalScrollNeed;
			
		case NO_PREFERENCE:
			
			return true;
			
		default:
			
			return false;
		}
	}*/
	
	@Override
	public void Print(int index)
	{
		for(int i = 0; i < index; i++)
		{
			System.out.print("  ");
		}
		
		System.out.print(label + "\n");
		
		for(Layoutable child: children)
		{
			child.Print(index + 1);
		}
	}
}