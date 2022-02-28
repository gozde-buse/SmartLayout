package com.prototype.smartlayout.model;

import com.prototype.smartlayout.model.enums.OrientationEnum;
import com.prototype.smartlayout.utils.ArrayIndexComparator;
import java.util.Arrays;
import java.util.Vector;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
public class LayoutContainer implements Layoutable
{
	private final String label;
	private final Vector<Layoutable> children;
	private Vector<WidthHeightRange> memo = null;
	private LayoutContainer parent;
	
	private int assignedX;
	private int assignedY;
	private int assignedWidth;
	private int assignedHeight;
	private OrientationEnum assignedOrientation;
	
	public LayoutContainer (String label)
	{
		this.label = label;
		children = new Vector<>();
	}

	public LayoutContainer (String label, Layoutable... layoutables)
	{
		this.label = label;
		children = new Vector<>();
		children.addAll(Arrays.asList(layoutables));
		
		for(Layoutable child: children) { child.setParent(this); }
	}

	@Override
	public Vector<WidthHeightRange> GetRanges()
	{
		if (memo != null) { return memo; }
		
		Vector<WidthHeightRange> vec = new Vector<>();
		vec.addAll(GetRangesByOrientation(OrientationEnum.HORIZONTAL));
		vec.addAll(GetRangesByOrientation(OrientationEnum.VERTICAL));
		memo = vec;
		
		return vec;
	}
	
	private Vector<WidthHeightRange> GetRangesByOrientation(OrientationEnum orientation)
	{
		Vector<WidthHeightRange> movingRanges = new Vector<>();
		Vector<WidthHeightRange> tempRanges = new Vector<>();

		for (Layoutable layoutable : children)
		{
			Vector<WidthHeightRange> compVec = layoutable.GetRanges();
			tempRanges.clear();

			if (movingRanges.isEmpty())
			{
				for (WidthHeightRange whr : compVec)
				{
					WidthHeightRange newRange = new WidthHeightRange(whr, orientation);
					newRange.addSubRange(whr);
					movingRanges.add(newRange);
				}
			}
			else
			{
				for (WidthHeightRange whr : movingRanges)
				{
					for (WidthHeightRange whrNew : compVec)
					{
						int minWidth, maxWidth, minHeight, maxHeight;
						WidthHeightRange newRange = null;
						
						switch (orientation)
						{
						case HORIZONTAL:
							minWidth = whr.getMinWidth() + whrNew.getMinWidth();
							maxWidth = whr.getMaxWidth() + whrNew.getMaxWidth();
							minHeight = Math.max(whr.getMinHeight(), whrNew.getMinHeight());
							maxHeight = Math.min(whr.getMaxHeight(), whrNew.getMaxHeight());

							if (maxHeight >= minHeight)
							{
								newRange = new WidthHeightRange(minWidth, maxWidth, minHeight, maxHeight, OrientationEnum.HORIZONTAL);
							}
							else
							{
								newRange = new WidthHeightRange(minWidth, maxWidth, minHeight, minHeight, OrientationEnum.HORIZONTAL);
								newRange.setHasFiller(true);
							}
							
							break;
							
						case VERTICAL:
							minWidth = Math.max(whr.getMinWidth(), whrNew.getMinWidth());
							maxWidth = Math.min(whr.getMaxWidth(), whrNew.getMaxWidth());
							minHeight = whr.getMinHeight() + whrNew.getMinHeight();
							maxHeight = whr.getMaxHeight() + whrNew.getMaxHeight();

							if (maxWidth >= minWidth)
							{
								newRange = new WidthHeightRange(minWidth, maxWidth, minHeight, maxHeight, OrientationEnum.VERTICAL);
							}
							else
							{
								newRange = new WidthHeightRange(minWidth, minWidth, minHeight, maxHeight, OrientationEnum.VERTICAL);
								newRange.setHasFiller(true);
							}
							
							break;
							
						default:
							log.debug("Böyle bir durum olamaz! HATA!!!");
							
							break;
						}
						
						if(newRange != null)
						{
							newRange.addSubRanges(whr.getSubRanges());
							newRange.addSubRange(whrNew);
							
							if(whrNew.isHasFiller())
								newRange.setHasFiller(true);
							
							tempRanges.add(newRange);
						}
					}
				}
				
				movingRanges.clear();
				movingRanges.addAll(tempRanges);
			}
		}
		
		return movingRanges;
	}
	
	@Override
	public void GetFinalLayout(int x, int y, int w, int h, WidthHeightRange whr)
	{
		setAssignedX(x);
		setAssignedY(y);
		setAssignedWidth(w);
		setAssignedHeight(h);
		setAssignedOrientation(whr.getOrientation());
		
		Vector<WidthHeightRange> subRanges = whr.getSubRanges();
		
		int remaining;
		int[] minValues = new int[subRanges.size()];
		int[] maxValues = new int[subRanges.size()];
		
		if(whr.getOrientation() == OrientationEnum.HORIZONTAL)
		{
			remaining = w;
			
			for (int i = 0; i < subRanges.size(); i++)//Bu döngü GetRanges kısmında hesaplanabilir
			{
				minValues[i] = subRanges.get(i).getMinWidth();
				maxValues[i] = subRanges.get(i).getMaxWidth();
			}
		}
		else if(whr.getOrientation() == OrientationEnum.VERTICAL)
		{
			remaining = h;
			
			for (int i = 0; i < subRanges.size(); i++)//Bu döngü GetRanges kısmında hesaplanabilir
			{
				minValues[i] = subRanges.get(i).getMinHeight();
				maxValues[i] = subRanges.get(i).getMaxHeight();
			}
		}
		else
		{
			//Bir sorun var demek!
			return;
		}
		
		int[] distribution = StrategyBalance(remaining, minValues, maxValues);
		int distance = 0;
		
		int fillerUnit = 0;
		int fillerRemaining = 0;
		
		if(whr.getOrientation() == OrientationEnum.HORIZONTAL)
		{
			if(whr.isHasFiller() && whr.getMaxWidth() < w)
			{
				int fillerWidth = w - whr.getMaxWidth();
					
				fillerUnit = (int) Math.floor(fillerWidth / children.size());
				fillerRemaining = fillerWidth - fillerUnit * children.size();
			}
		}
		else if(whr.getOrientation() == OrientationEnum.VERTICAL)
		{
			if(whr.isHasFiller() && whr.getMaxHeight() < h)
			{
				int fillerHeight = h - whr.getMaxHeight();
					
				fillerUnit = (int) Math.floor(fillerHeight / children.size());
				fillerRemaining = fillerHeight - fillerUnit * children.size();
			}
		}
		
		for(int i = 0; i < children.size(); i++)
		{
			int tempdistribution = distribution[i] + fillerUnit;
			
			if(i >= children.size() - fillerRemaining)
				tempdistribution++;
			
			if(whr.getOrientation() == OrientationEnum.HORIZONTAL)
			{
				if(subRanges.get(i).getMaxWidth() < x + distance || subRanges.get(i).getMaxHeight() < h)
					subRanges.get(i).setHasFiller(true);
				
				children.get(i).GetFinalLayout(x + distance, y, tempdistribution, h, subRanges.get(i));
			}
			else if(whr.getOrientation() == OrientationEnum.VERTICAL)
			{
				if(subRanges.get(i).getMaxWidth() < x || subRanges.get(i).getMaxHeight() < h + distance)
					subRanges.get(i).setHasFiller(true);
				
				children.get(i).GetFinalLayout(x, y + distance, w, tempdistribution, subRanges.get(i));
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*@Override
	public void SetAestheticAreas()
	{
		float parentCenterX = parent.getAssignedWidth() / 2;
		float parentCenterY = parent.getAssignedHeight() / 2;
		
		float totalArea = assignedWidth * assignedHeight;
		float horizontalDistance = Math.abs(parentCenterX - (assignedX + assignedWidth / 2));
		float verticalDistance = Math.abs(parentCenterY - (assignedY + assignedHeight / 2));
		float distance = (float) Math.sqrt(Math.pow(horizontalDistance, 2) + Math.pow(verticalDistance, 2));
		
		float leftPart, topPart;
		float leftDistance, rightDistance, topDistance, bottomDistance;
		
		if(parentCenterX > assignedX && parentCenterX < assignedX + assignedWidth)
		{
			leftPart = parentCenterX - assignedX;
			leftDistance = parentCenterX - leftPart / 2;
			rightDistance = parentCenterX - (assignedWidth - leftPart) / 2;
		}
		else if(parentCenterX <= assignedX)
		{
			leftPart = 0;
			leftDistance = 0;
			rightDistance = assignedX + assignedWidth / 2 - parentCenterX;
		}
		else
		{
			leftPart = assignedWidth;
			rightDistance = 0;
			leftDistance = parentCenterX - assignedX - assignedWidth / 2;
		}
		
		if(parentCenterY > assignedY && parentCenterY < assignedY + assignedHeight)
		{
			topPart = parentCenterY - assignedY;
			topDistance = parentCenterY - topPart / 2;
			bottomDistance = parentCenterY - (assignedHeight - topPart) / 2;
		}
		else if(parentCenterY <= assignedY)
		{
			topPart = 0;
			topDistance = 0;
			bottomDistance = assignedY + assignedHeight / 2 - parentCenterY;
		}
		else
		{
			topPart = assignedHeight;
			bottomDistance = 0;
			topDistance = parentCenterY - assignedY - assignedHeight / 2;
		}
		
		float leftArea = assignedHeight * leftPart;
		float topArea = assignedWidth * topPart;
		
		aestheticAreas = new Area(totalArea, leftArea, topArea, distance, leftDistance, rightDistance, topDistance, bottomDistance);
	}
	
	@Override
	public float GetBalance()
	{
		float maxLeftArea, maxRightArea, maxTopArea, maxBottomArea;
		maxLeftArea = maxRightArea = maxTopArea = maxBottomArea = 0;
		
		for(Layoutable child: children)
		{
			Area aestheticArea = child.getAestheticAreas();
			
			if(aestheticArea.getLeftArea() > maxLeftArea) { maxLeftArea = aestheticArea.getLeftArea(); }
			if(aestheticArea.getRightArea() > maxRightArea) { maxRightArea = aestheticArea.getRightArea(); }
			if(aestheticArea.getTopArea() > maxTopArea) { maxTopArea = aestheticArea.getTopArea(); }
			if(aestheticArea.getBottomArea() > maxBottomArea) { maxBottomArea = aestheticArea.getBottomArea(); }
		}
		
		float wLeft, wRight, wTop, wBottom;
		wLeft = wRight = wTop = wBottom = 0;

		for(Layoutable child: children)
		{
			Area aestheticArea = child.getAestheticAreas();
			
			wLeft += child.GetBalance() * aestheticArea.getLeftDistance() * (aestheticArea.getLeftArea() / maxLeftArea);
			wRight += child.GetBalance() * aestheticArea.getRightDistance() * (aestheticArea.getRightArea() / maxRightArea);
			wTop += child.GetBalance() * aestheticArea.getTopDistance() * (aestheticArea.getTopArea() / maxTopArea);
			wBottom += child.GetBalance() * aestheticArea.getBottomDistance() * (aestheticArea.getBottomArea() / maxBottomArea);
		}
		
		float balanceHorizontal = (wTop - wBottom) / Math.max(Math.abs(wTop), Math.abs(wBottom));
		float balanceVertical = (wLeft - wRight) / Math.max(Math.abs(wLeft), Math.abs(wRight));
		
		float balance = 1 - (Math.abs(balanceVertical) + Math.abs(balanceHorizontal)) / 2;
		
		return balance;
	}*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	//private String id;
	
	

	/*public LayoutContainer (String id)
	{
		//this.id = id;
		children = new Vector<>();
	}

	public LayoutContainer (String id, Layoutable... layoutables) {
		this(id);
		children.addAll(Arrays.asList(layoutables));
	}*/

	@Override
	public Vector<WidthHeightRange> getRanges () {
		// vec is what we will return at the end.
		// movingRanges and tempRanges are used temporarily for creating all possible layouts.

		// if we already calculated it no need to do it again.
		if (memo != null) {
			return memo;
		}

		Vector<WidthHeightRange> movingRanges = new Vector<>();
		Vector<WidthHeightRange> tempRanges = new Vector<>();

		// First, the HORIZONTAL orientation strategy

		// We will iterate over all children and one by one integrate them to the solution
		for (Layoutable layoutable : children) {
			// Get all possible ranges (layouts) for the layoutable child, layoutable
			Vector<WidthHeightRange> compVec = layoutable.getRanges(); // inside recursion, keep the calculated values.
			tempRanges.clear();

			if (movingRanges.isEmpty()) {
				// If this is the first component, we simply fill the movingRanges data
				// based on this component

				for (WidthHeightRange whr : compVec) {
					WidthHeightRange newRange = new WidthHeightRange(whr);
					newRange.setOrientation(OrientationEnum.HORIZONTAL);
					newRange.addSubRange(whr);
					movingRanges.add(newRange);
				}
			} else {
				// If this is not the first child, then we already have some ranges computed
				// in movingRanges.
				// For all other children, we compute the product with existing
				// movingRanges data and obtain the new movingRanges.

				for (WidthHeightRange whr : movingRanges) {
					for (WidthHeightRange whrNew : compVec) {
						// Since this is horizontal, we know how to compute the new range:
						int newMinWidth = whr.getMinWidth() + whrNew.getMinWidth();
						int newMaxWidth = whr.getMaxWidth() + whrNew.getMaxWidth();
						int newMinHeight = Math.max(whr.getMinHeight(), whrNew.getMinHeight());
						int newMaxHeight = Math.min(whr.getMaxHeight(), whrNew.getMaxHeight());

						// Make sure that newMaxHeight is greater than newMinHeight.
						// Otherwise we have an infeasible layout. This only should happen
						// if the height ranges of the two children are not intersecting.

						if (newMaxHeight >= newMinHeight) {
							// Now create the new range object and add it to the temp vector:
							WidthHeightRange newRange =
									new WidthHeightRange(
											OrientationEnum.HORIZONTAL,
											newMinWidth,
											newMaxWidth,
											newMinHeight,
											newMaxHeight);
							newRange.addSubRanges(whr.getSubRanges());
							newRange.addSubRange(whrNew);
							tempRanges.add(newRange);
						} // else koşulunda max - min lik bir filler yapılabilir
					}
				}

				//==============
				// Check if tempRAnges is empty
				// If so, we cannot feasibly layout this container.
				// We hould immediately stop the horizontal layout process. and also do not update vec below.
				movingRanges.clear();
				movingRanges.addAll(tempRanges);
			}
		}

		// If the above check failed, do not update vec.
		Vector<WidthHeightRange> vec = new Vector<>(movingRanges);

		// Now, the VERTICAL orientation strategy

		movingRanges.clear();

		for (Layoutable layoutable : children) {
			Vector<WidthHeightRange> compVec = layoutable.getRanges(); // inside recursion, keep the calculated values.
			tempRanges.clear();

			if (movingRanges.isEmpty()) {
				// If this is the first component, we simply fill the movingRanges data
				// based on this component

				for (WidthHeightRange whr : compVec) {
					WidthHeightRange newRange = new WidthHeightRange(whr);
					newRange.setOrientation(OrientationEnum.VERTICAL);
					newRange.addSubRange(whr);
					movingRanges.add(newRange);
				}
			} else {
				// For all other children, we compute the product with existing
				// movingRanges data and obtain the new movingRanges.

				for (WidthHeightRange whr : movingRanges) {
					for (WidthHeightRange whrNew : compVec) {
						// Since this is vertical, we know how to compute the new range:
						int newMinWidth = Math.max(whr.getMinWidth(), whrNew.getMinWidth());
						int newMaxWidth = Math.min(whr.getMaxWidth(), whrNew.getMaxWidth());
						int newMinHeight = whr.getMinHeight() + whrNew.getMinHeight();
						int newMaxHeight = whr.getMaxHeight() + whrNew.getMaxHeight();

						// Make sure that newMaxWidth is greater than newMinWidth.
						// Otherwise we have an infeasible layout. This only should happen
						// if the width ranges of the two children are not intersecting.

						if (newMaxWidth >= newMinWidth) {
							// Now create the new range object and add it to the temp vector:
							WidthHeightRange newRange =
									new WidthHeightRange(
											OrientationEnum.VERTICAL,
											newMinWidth,
											newMaxWidth,
											newMinHeight,
											newMaxHeight);
							newRange.addSubRanges(whr.getSubRanges());
							newRange.addSubRange(whrNew);
							tempRanges.add(newRange);
						} // else koşulunda max - min lik bir filler yapılabilir
					}
				}

				//==============
				// Similaryl check feasibility and only update if feasible layouts exist
				movingRanges.clear();
				movingRanges.addAll(tempRanges);
			}
		}

		// Update only if feasiblity checks returned true
		vec.addAll(movingRanges);
		memo = vec;

		return vec;
	}

	public void clearMemoization () {
		for (Layoutable layoutable : children) {
			if (layoutable instanceof LayoutContainer) {
				((LayoutContainer) layoutable).clearMemoization();
				((LayoutContainer) layoutable).memo = null;
			}
		}
	}

	@Override
	public boolean layout (int x, int y, int w, int h, WidthHeightRange whr) {
		if (whr == null || (whr.getMinHeight() > h && whr.getMaxHeight() < h && whr.getMinWidth() > w && whr.getMaxWidth() < w)) {
			return false;
		}
		// This is the main method that does the computation of layout
		setAssignedX(x);
		setAssignedY(y);
		setAssignedWidth(w);
		setAssignedHeight(h);

		Vector<WidthHeightRange> subRanges = whr.getSubRanges();
		int[] minWidthValues = new int[subRanges.size()];
		int[] maxWidthValues = new int[subRanges.size()];
		//int totalMinWidthOfChildren = 0;
		//int totalMaxWidthOfChildren = 0;
		int[] minHeightValues = new int[subRanges.size()];
		int[] maxHeightValues = new int[subRanges.size()];
		//int totalMinHeightOfChildren = 0;
		//int totalMaxHeightOfChildren = 0;
		for (int i = 0; i < subRanges.size(); i++) {
			minHeightValues[i] = subRanges.get(i).getMinHeight();
			maxHeightValues[i] = subRanges.get(i).getMaxHeight();
			//totalMinHeightOfChildren += subRanges.get(i).getMinHeight();
			//totalMaxHeightOfChildren += subRanges.get(i).getMaxHeight();

			minWidthValues[i] = subRanges.get(i).getMinWidth();
			maxWidthValues[i] = subRanges.get(i).getMaxWidth();
			//totalMinWidthOfChildren += subRanges.get(i).getMinWidth();
			//totalMaxWidthOfChildren += subRanges.get(i).getMaxWidth();
		}


		boolean feasible = false;
		if (whr.getOrientation() == OrientationEnum.HORIZONTAL || whr.getOrientation() == OrientationEnum.VERTICAL) {
			// Weight strategy by max values
//			feasible = strategyWeight(x, y, isHorizontal(whr) ? totalMaxWidthOfChildren : totalMaxHeightOfChildren, subRanges, whr.getOrientationStrategy(), w, h, isHorizontal(whr) ? maxWidthValues : maxHeightValues);
			// Weight strategy by min values
//			feasible = strategyWeight(x, y, isHorizontal(whr) ? totalMinWidthOfChildren : totalMinHeightOfChildren, subRanges, whr.getOrientationStrategy(), w, h, isHorizontal(whr) ? minWidthValues : minHeightValues);

//			// this stream gets the statistics of the int array so we can find minimum or maximum values of this array.
//			IntSummaryStatistics statWidth = Arrays.stream(maxWidthValues).summaryStatistics();
//			IntSummaryStatistics statHeight = Arrays.stream(maxHeightValues).summaryStatistics();
//			// Max values strategy
//			feasible = strategyValues(x, y, subRanges, whr.getOrientationStrategy(), (w > statWidth.getMin() ? statWidth.getMin() : w), (h > statHeight.getMin() ? statHeight.getMin() : h), isHorizontal(whr) ? maxWidthValues : maxHeightValues);
//			// Min values strategy
//			feasible = strategyValues(x, y, subRanges, whr.getOrientationStrategy(), (w > statWidth.getMin() ? statWidth.getMin() : w), (h > statHeight.getMin() ? statHeight.getMin() : h), isHorizontal(whr) ? minWidthValues : minHeightValues);

			// Balance min
//			feasible = strategyFair(x, y, subRanges, whr.getOrientationStrategy(), w, h, isHorizontal(whr) ? minWidthValues : minHeightValues);
			// Balance max
//			feasible = strategyFair(x, y, subRanges, whr.getOrientationStrategy(), w, h, isHorizontal(whr) ? minWidthValues : minHeightValues, isHorizontal(whr) ? maxWidthValues : maxHeightValues);

			feasible = strategyBalance(x, y, subRanges, whr.getOrientation(), w, h, isHorizontal(whr) ? minWidthValues : minHeightValues, isHorizontal(whr) ? maxWidthValues : maxHeightValues);
		} else {
			//log.debug("Shouldn't be here - Probably infeasible layout. ID: " + this.id);
		}

		// Just check if the given boundaries match with given width & height.
		return feasible;
	}

	private boolean isHorizontal (WidthHeightRange whr) {
		return OrientationEnum.HORIZONTAL.equals(whr.getOrientation());
	}

	/**
	 * This strategy gives the components values according to values parameter.
	 *
	 * @param x                   x position of Component/Container
	 * @param y                   y position of Component/Container
	 * @param subRanges           sub ranges of given root node
	 * @param orientationStrategy horizontal or vertical
	 * @param w                   actual width value
	 * @param h                   actual height value
	 * @param values              values to consider while distributing weight ratio
	 */
	/*private boolean strategyValues (int x, int y, Vector<WidthHeightRange> subRanges, WidthHeightRangeEnum orientationStrategy, int w, int h, int[] values) {
		int cum = 0;
		return layoutRecursively(x, y, subRanges, orientationStrategy, w, h, cum, values);
	}*/

	/**
	 * This method shares remaining values as the minimum of the components.
	 *
	 * @param x
	 * @param y
	 * @param subRanges
	 * @param orientationStrategy
	 * @param w
	 * @param h
	 * @param minValues
	 * @param capacityValues
	 */
	/*private boolean strategyFair (int x, int y, Vector<WidthHeightRange> subRanges, WidthHeightRangeEnum orientationStrategy, int w, int h, int[] minValues, int[] capacityValues) {
		int length = capacityValues.length;
		int cum = 0;
		int[] distribution = new int[subRanges.size()];
		boolean[] removedIndex = new boolean[subRanges.size()];
		int remaining = WidthHeightRangeEnum.HORIZONTAL.equals(orientationStrategy) ? w : h;
		for (int i = 0; i < subRanges.size(); i++) {
			distribution[i] = minValues[i];
			remaining -= minValues[i];
			capacityValues[i] -= minValues[i];
			length = LayoutContainerUtils.checkDistributionComplete(length, removedIndex, i, capacityValues[i]);
			if (remaining <= 0) {
				break;
			}
		}

		//while it can still be distributed, distribute
		while (remaining > 0) {
			// get the smallest number after 0 since 0 means that node is distributed.
			IntSummaryStatistics stats = Arrays.stream(capacityValues).filter(num -> num > 0).summaryStatistics();
			// if total remaining over number of children less than minimum value a child can get, distribute this value instead of minimum value
			if (remaining / length <= stats.getMin()) {
				// if there is still something left to distribute in this node
				int remainingOverLength = remaining / length;
				for (int i = 0; i < subRanges.size(); i++) {
					if (!removedIndex[i]) {
						// distribute and subtract the amount from remaining
						if (remainingOverLength == 0) { // TODO : remaining <= length - add one by one
							distribution[i] += remaining;
							remaining = 0;
							break;
						} else {
							remaining = LayoutContainerUtils.distribute(capacityValues, distribution, remaining, remainingOverLength, i);
							length = LayoutContainerUtils.checkDistributionComplete(length, removedIndex, i, capacityValues[i]);
						}
					}
				}
			} else {
				int remainingOverLength = remaining / length;
				for (int i = 0; i < subRanges.size(); i++) {
					if (!removedIndex[i]) {
						// distribute and subtract the amount from remaining
						if (remainingOverLength == 0) {
							distribution[i] += remaining;
							remaining = 0;
						} else {
							remaining = LayoutContainerUtils.distribute(capacityValues, distribution, remaining, stats.getMin(), i);
							length = LayoutContainerUtils.checkDistributionComplete(length, removedIndex, i, capacityValues[i]);
						}
					}
				}
			}
			if (remaining < 0) {
				log.error("Remaining can't be negative!");
				break;
			}
			if (length <= 0) {
//				log.error("All is distributed. Remaining: " + remaining);
			}
		}

		LayoutContainerUtils.checkForDistributionCompletedSuccessfully(removedIndex, id);

		return layoutRecursively(x, y, subRanges, orientationStrategy, w, h, cum, distribution);
	}*/

	// TODO: private boolean strategyWeighted();

	/**
	 * This method acts like a water scale. adds minimum then adds only to least numbers
	 *
	 * @param x
	 * @param y
	 * @param subRanges
	 * @param orientationStrategy
	 * @param w
	 * @param h
	 * @param minValues
	 * @param capacityValues
	 * @return
	 */
	private boolean strategyBalance (int x, int y, Vector<WidthHeightRange> subRanges, OrientationEnum orientationStrategy, int w, int h, int[] minValues, int[] capacityValues) {
		int cum = 0; //Buna neden gerek var anlamadım.
		int[] maxValues = new int[subRanges.size()];
		int[] distribution = new int[subRanges.size()];
		int remaining = OrientationEnum.HORIZONTAL.equals(orientationStrategy) ? w : h;
		for (int i = 0; i < subRanges.size(); i++) {
			maxValues[i] = OrientationEnum.HORIZONTAL.equals(orientationStrategy) ? subRanges.get(i).getMaxWidth() : subRanges.get(i).getMaxHeight();
			distribution[i] = minValues[i];
			remaining -= minValues[i];
			capacityValues[i] -= minValues[i];
			if (remaining <= 0) {
				break;
			}
		}
		// This creates the index array of distribution array and then sorts the distribution
		// and returns as indexes so that we know the positions of sorted array and keep the original array.
		ArrayIndexComparator comparator = new ArrayIndexComparator(distribution);
		Integer[] indexOrder = comparator.createIndexArray();
		Arrays.sort(indexOrder, comparator);

		//while it can still be distributed, distribute
		while (remaining > 0) {
			if (distribution[indexOrder[0]] == maxValues[indexOrder[0]]) {
				// This slot is full, remove it from indexOrder
				indexOrder = Arrays.copyOfRange(indexOrder, 1, indexOrder.length);
			} else {
				// This slot can still be filled
				// Increase by 1.
				// Can we increase more? Maybe, but requires very smart computation
				distribution[indexOrder[0]] = distribution[indexOrder[0]] + 1;
				remaining--;
				int p = 0;
				int q = 1;
				// Now we move this item up the list if this is not the smallest
				// item anymore. This is basically one step of bubble sort
				// takes O(n) in the worst case. But n is small here.
				while (q < indexOrder.length && distribution[indexOrder[p]] > distribution[indexOrder[q]]) {
					// swap indexOrder[p] with indexOrder[q] and increment pointers.
					int temp = indexOrder[p];
					indexOrder[p] = indexOrder[q];
					indexOrder[q] = temp;
					p = q;
					q = p + 1;
				}
			}
			if (indexOrder.length < 1) {
				log.trace("Layout's constraints exceeds max values for components!");
				break;
			}
			if (remaining < 0) {
				log.error("Remaining can't be negative!");
				break;
			}
		}

		return layoutRecursively(x, y, subRanges, orientationStrategy, w, h, cum, distribution);
	}

	private boolean layoutRecursively (int x, int y, Vector<WidthHeightRange> subRanges, OrientationEnum orientationStrategy, int w, int h, int cum, int[] distribution) {
		for (int i = 0; i < subRanges.size(); i++) {
			// Resize the component to fit the window
			if (OrientationEnum.HORIZONTAL.equals(orientationStrategy)) {
				if (!children.get(i).layout(x + cum, y, distribution[i], h, subRanges.get(i))) {
					return false;
				}
			} else {
				if (!children.get(i).layout(x, y + cum, w, distribution[i], subRanges.get(i))) {
					return false;
				}
			}
			cum += distribution[i];
		}
		return true;
	}

	/**
	 * This strategy weights the components according to the values parameter.
	 * To distribute according to maxValues give values maxValues array.
	 * To distribute according to minValues give values minValues array.
	 *
	 * @param x                   x position of Component/Container
	 * @param y                   y position of Component/Container
	 * @param totalOfChildren     total pixels occupied according to orientationStrategy
	 * @param subRanges           sub ranges of given root node
	 * @param orientationStrategy horizontal or vertical
	 * @param w                   actual width value
	 * @param h                   actual height value
	 * @param values              values to consider while distributing weight ratio
	 */
	/*private boolean strategyWeight (int x, int y, int totalOfChildren, Vector<WidthHeightRange> subRanges, WidthHeightRangeEnum orientationStrategy, int w, int h, int[] values) {
		int cum = 0;
		// find the width ratio according to max values
		float ratio = totalOfChildren / (WidthHeightRangeEnum.HORIZONTAL.equals(orientationStrategy) ? (float) w : (float) h);
		for (int i = 0; i < subRanges.size(); i++) {
			// Resize the component to fit the window
			int value = (int) Math.ceil(values[i] / ratio);
			if (WidthHeightRangeEnum.HORIZONTAL.equals(orientationStrategy)) {
				if (!children.get(i).layout(x + cum, y, value, h, subRanges.get(i))) {
					return false;
				}
			} else {
				if (!children.get(i).layout(x, y + cum, w, value, subRanges.get(i))) {
					return false;
				}
			}
			cum += value;
			// TODO width height tan çıkara çıkara ilerlenebilir. Böylelikle 2-3 pixel artık kalmaz
		}
		return true;
	}*/

	public void addComponent (Layoutable... comp) {
		children.addAll(Arrays.asList(comp));
	}

	public LayoutComponent findComponent (String label) {
		for (Layoutable component : children) {
			if (component instanceof LayoutComponent && label.equals(((LayoutComponent) component).getLabel())) {
				return (LayoutComponent) component;
			} else if (component instanceof LayoutContainer) {
				LayoutComponent comp = ((LayoutContainer) component).findComponent(label);
				if (comp != null) {
					return comp;
				}
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	
	/** YEDEK **/
	
	/*
	
	@Override
	public void GetFinalLayout(int x, int y, int w, int h, WidthHeightRange whr)
	{
		setAssignedX(x);
		setAssignedY(y);
		setAssignedWidth(w);
		setAssignedHeight(h); //SetAssigned kısmı başka bir yere alınırsa memoyu etkiler mi? Etkilemezse başka yere alalım.
		
		Vector<WidthHeightRange> subRanges = whr.getSubRanges();
		
		int remaining;
		int[] minValues = new int[subRanges.size()];
		int[] maxValues = new int[subRanges.size()];
		
		if(whr.getOrientation() == OrientationEnum.HORIZONTAL)
		{
			remaining = w;
			
			for (int i = 0; i < subRanges.size(); i++)
			{
				minValues[i] = subRanges.get(i).getMinWidth();
				maxValues[i] = subRanges.get(i).getMaxWidth();
			}
		}
		else if(whr.getOrientation() == OrientationEnum.VERTICAL)
		{
			remaining = w;
			
			for (int i = 0; i < subRanges.size(); i++)
			{
				minValues[i] = subRanges.get(i).getMinHeight();
				maxValues[i] = subRanges.get(i).getMaxHeight();
			}
		}
		else
		{
			//Bir sorun var demek!
			return;
		}
		
		int[] distribution = StrategyBalance(remaining, minValues, maxValues);
		int position = whr.getOrientation() == OrientationEnum.HORIZONTAL ? x : y;
		int distance = 0;
		
		for(int i = 0; i < children.size(); i++)
		{
			if(whr.getOrientation() == OrientationEnum.HORIZONTAL)
			{
				int hTemp = h;
				
				if(whr.isHasFiller() && subRanges.get(i).getMaxHeight() < h)
				{
					hTemp = subRanges.get(i).getMaxHeight();
					
					LayoutComponent lComponent = (LayoutComponent) SmartLayout.CreateFillerPiece();
					lComponent.GetFinalLayout(position + distance, y + hTemp, distribution[i], h - hTemp, null);
				}
				
				children.get(i).GetFinalLayout(position + distance, y, distribution[i], hTemp, subRanges.get(i));
			}
			else if(whr.getOrientation() == OrientationEnum.VERTICAL)
			{
				int wTemp = w;
				
				if(whr.isHasFiller() && subRanges.get(i).getMaxWidth() < w)
				{
					wTemp = subRanges.get(i).getMaxWidth();

					LayoutComponent lComponent = (LayoutComponent) SmartLayout.CreateFillerPiece();
					lComponent.GetFinalLayout(x + wTemp, position + distance, w - wTemp, distribution[i], null);
				}
				
				children.get(i).GetFinalLayout(x, position + distance, wTemp, distribution[i], subRanges.get(i));
			}
			
			distance += distribution[i];
		}
	}	*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}