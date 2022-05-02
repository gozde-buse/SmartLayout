package com.prototype.smartlayout.model;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.prototype.smartlayout.SmartLayout;
import com.prototype.smartlayout.model.enums.ComponentDimensionEnum;

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
	public ExtendedArray<WidthHeightRange> GetRanges(int width, int height)
	{
		ExtendedArray<WidthHeightRange> vec = new ExtendedArray<WidthHeightRange>(WidthHeightRange.class, 1);
		vec.set(widthHeightRange, 0);
		
		return vec;
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
	}
}
