package com.prototype.smartlayout;

import com.prototype.Controller;
import com.prototype.smartlayout.listeners.ComponentResizeEndListener;
import com.prototype.smartlayout.model.ExtendedArray;
import com.prototype.smartlayout.model.ExtendedArrayList;
import com.prototype.smartlayout.model.LayoutComponent;
import com.prototype.smartlayout.model.LayoutContainer;
import com.prototype.smartlayout.model.Layoutable;
import com.prototype.smartlayout.model.WidthHeightRange;
import com.prototype.smartlayout.model.enums.AlignmentEnum;
import com.prototype.smartlayout.model.enums.ComponentDimensionEnum;
import com.prototype.smartlayout.utils.AestheticMeasureUtil;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class SmartLayout extends JFrame
{
	private static final long serialVersionUID = 6944709955451188697L;
	public static SmartLayout app;
	
	public static Vector<LayoutComponent> components = new Vector<LayoutComponent>();
	public static Vector<LayoutComponent> fillers = new Vector<LayoutComponent>();
	
	public int width, height;
	public int fillerTrimRatio, verticalTrimRatio, horizontalTrimRatio;
	public AlignmentEnum horizontalAlignment, verticalAlignment;
	
	private Layoutable root;
	private ExtendedArray<WidthHeightRange> finalLayoutCases;
	private JFrame resultFrame;
	
	public ExtendedArray<String> comboBoxLayouts;
	public int comboBoxSelectedId;
	
	private boolean started = false;
	
	public ProcessEnum processType;
	public enum ProcessEnum {FEASIBLE, FILLER, VERTICAL_SCROLL, HORIZONTAL_SCROLL, NO_PREFERENCE}
	
	public SmartLayout(Layoutable root)
	{
		this.root = root;
		processType = ProcessEnum.FEASIBLE;
	}
	
	public void Run(int width, int height, int fillerTrimRatio, int verticalTrimRatio, int horizontalTrimRatio)
	{
		SetSize(width, height);
		this.fillerTrimRatio = fillerTrimRatio;
		this.verticalTrimRatio = verticalTrimRatio;
		this.horizontalTrimRatio = horizontalTrimRatio;
		
		Calculate();
		ClassifyLayouts();
		
		int finalLayoutId = TestAesthetics();
		comboBoxSelectedId = finalLayoutId;
		
		ChangeLayout();
	}
	
	private void SetSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}
	
	private void Calculate()
	{
		//root.Print(0);
		
		long startTime, elapsedTime;
		startTime = System.nanoTime();
		
		while(finalLayoutCases == null)
		{
			finalLayoutCases = root.GetRanges();
			
			if(finalLayoutCases == null)
			{
				int index = processType.ordinal();
				index++;
				
				if(index >= 5)
					return;
				
				processType = ProcessEnum.values()[index];
			}
		}

		elapsedTime = System.nanoTime() - startTime;
		System.out.println("CALCULATE - Get Ranges Time: " + elapsedTime);
		
		startTime = System.nanoTime();
		
		//for(int i = 0; i < finalLayoutCases.length; i++) { finalLayoutCases.get(i).ListRange(0); }
		
		elapsedTime = System.nanoTime() - startTime;
		System.out.println("CALCULATE - List Ranges Time: " + elapsedTime);
	}
	
	private void ClassifyLayouts()
	{
		long startTime, elapsedTime;
		ExtendedArrayList<WidthHeightRange> classifiedLayoutCases = new ExtendedArrayList<WidthHeightRange>(WidthHeightRange.class);
		ExtendedArrayList<WidthHeightRange> eliminatedLayoutCases = new ExtendedArrayList<WidthHeightRange>(WidthHeightRange.class);
		
		startTime = System.nanoTime();
		
		while(classifiedLayoutCases.length == 0)
		{
			for(long i = 0; i < finalLayoutCases.length; i++)
			{
				WidthHeightRange flc = finalLayoutCases.get(i);
				
				if(flc != null)
				{
					if(processType == ProcessEnum.FEASIBLE)
					{
						if(flc.TestFeasiblity(width, height))
							classifiedLayoutCases.add(flc);
						else
							eliminatedLayoutCases.add(flc);
					}
					else
					{
						boolean fillerNeed = flc.TestFillerNeed(width, height);
						boolean verticalNeed = flc.TestVerticalScrollNeed(height);
						boolean horizontalNeed = flc.TestHorizontalScrollNeed(width);

						flc.setHasFiller(fillerNeed);
						flc.setHasVerticalScroll(verticalNeed);
						flc.setHasHorizontalScroll(horizontalNeed);
						
						if(Trim(flc))
							classifiedLayoutCases.add(flc);
						else
							eliminatedLayoutCases.add(flc);
					}
				}
			}
			
			if(classifiedLayoutCases.length == 0)
			{
				if(processType == ProcessEnum.FEASIBLE)
				{
					processType = ProcessEnum.FILLER;
					classifiedLayoutCases.MakeArrayListOf(eliminatedLayoutCases);
					
					break;
				}
				else if(processType == ProcessEnum.FILLER)
					fillerTrimRatio--;
				else if(processType == ProcessEnum.VERTICAL_SCROLL)
					verticalTrimRatio--;
				else if(processType == ProcessEnum.HORIZONTAL_SCROLL)
					horizontalTrimRatio--;
				else
				{
					if(verticalTrimRatio > 0)
						verticalTrimRatio--;
					
					if(horizontalTrimRatio > 0)
						horizontalTrimRatio--;
				}

				finalLayoutCases = new ExtendedArray<WidthHeightRange>(WidthHeightRange.class, eliminatedLayoutCases.length);
				finalLayoutCases.makeArrayOf(eliminatedLayoutCases);
				eliminatedLayoutCases.clear();
			}
		}

		elapsedTime = System.nanoTime() - startTime;
		System.out.println("CLASSIFY LAYOUTS - Classify Layouts Time: " + elapsedTime);

		finalLayoutCases = new ExtendedArray<WidthHeightRange>(WidthHeightRange.class, classifiedLayoutCases.length);
		finalLayoutCases.makeArrayOf(classifiedLayoutCases);

		startTime = System.nanoTime();
		
		comboBoxLayouts = new ExtendedArray<String>(String.class, finalLayoutCases.length);
		
		for(long i = 0; i < finalLayoutCases.length; i++)
		{
			if(finalLayoutCases.get(i) != null)
				comboBoxLayouts.set(finalLayoutCases.get(i).ToString(), i);
		}

		elapsedTime = System.nanoTime() - startTime;
		System.out.println("CLASSIFY LAYOUTS - Arrange Combobox Time: " + elapsedTime);
	}
	
	private boolean Trim(WidthHeightRange range)
	{
		switch(processType)
		{
		case FILLER:
			
			if(fillerTrimRatio != 0)
			{
				float maxWidth = width * fillerTrimRatio / 10;
				float maxHeight = height * fillerTrimRatio / 10;
				
				if(range.getMaxWidth() < maxWidth || range.getMaxHeight() < maxHeight)
					return false;
			}
			
			return true;
			
		case VERTICAL_SCROLL:

			if(verticalTrimRatio != 0)
			{
				float minHeight = height * (verticalTrimRatio + 10) / 10;
				
				if(range.getMaxHeight() < minHeight)
					return false;
			}

			return true;
			
		case HORIZONTAL_SCROLL:

			if(horizontalTrimRatio != 0)
			{
				float minWidth = height * (horizontalTrimRatio + 10) / 10;
				
				if(range.getMaxWidth() < minWidth)
					return false;
			}

			return true;
			
		case NO_PREFERENCE:

			if(verticalTrimRatio != 0)
			{
				float minHeight = height * (verticalTrimRatio + 10) / 10;
				
				if(range.getMaxHeight() < minHeight)
					return false;
			}

			if(horizontalTrimRatio != 0)
			{
				float minWidth = height * (horizontalTrimRatio + 10) / 10;
				
				if(range.getMaxWidth() < minWidth)
					return false;
			}
			
			return true;
			
		default:
				
			return true;
		}
	}
	
	private int TestAesthetics()
	{
		float maxAesthetics = 0;
		int finalLayoutId = -1;
		int idCounter = 0;
		
		System.out.println(processType + " " + finalLayoutCases.length);
		
		long startTime, elapsedTime;
		startTime = System.nanoTime();
		
		for(long i = 0; i < finalLayoutCases.length; i++)
		{
			WidthHeightRange layout = finalLayoutCases.get(i);
			
			int layoutWidth, layoutHeight;
			
			if(layout.isHasVerticalScroll())
				layoutHeight = layout.getMinHeight();
			else
				layoutHeight = height;
			
			if(layout.isHasHorizontalScroll())
				layoutWidth = layout.getMinWidth();
			else
				layoutWidth = width;
			
			fillers.clear();

			//long startTime2, elapsedTime2;
			//startTime2 = System.nanoTime();
			
			root.GetFinalLayout(0, 0, layoutWidth, layoutHeight, layout);

			//elapsedTime2 = System.nanoTime() - startTime2;
			//System.out.println("TEST AESTHETICS - Get Layout Time: " + elapsedTime2);
			
			
			float aesthetics = AestheticMeasureUtil.MeasureAesthetics(width, height, layoutWidth, layoutHeight);
			
			if(aesthetics > maxAesthetics)
			{
				maxAesthetics = aesthetics;
				finalLayoutId = idCounter;
			}
			
			idCounter++;
		}
		
		elapsedTime = System.nanoTime() - startTime;
		System.out.println("TEST AESTHETICS - Aesthetic Measure Time: " + elapsedTime);
		
		System.out.println("Aesthetic Measure: " + maxAesthetics);

		return finalLayoutId;
	}
	
	private void Draw(int layoutWidth, int layoutHeight)
	{
		if(resultFrame == null)
		{
			resultFrame = new JFrame("Smart Layout");
			resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			resultFrame.addComponentListener(new ComponentResizeEndListener()
			{
				@Override
				public void resizeTimedOut() { Resize(); }
			});
		}
		else
		{
			resultFrame.getContentPane().removeAll();
		}
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(layoutWidth, layoutHeight));
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		System.out.println("************COMPONENTS************");
		
		for(LayoutComponent component: components)
		{
			JComponent jComponent = component.getJcomponent();
			panel.add(jComponent);
			jComponent.setBounds(component.getAssignedX(), component.getAssignedY(), component.getAssignedWidth(), component.getAssignedHeight());
			jComponent.setBorder(BorderFactory.createLineBorder(Color.GREEN));
			
			System.out.println("\tX: " + component.getAssignedX() + " Y: " + component.getAssignedY() + " W: " + component.getAssignedWidth() + " H: " + component.getAssignedHeight());
		}

		System.out.println("************FILLERS************");
		
		for(LayoutComponent filler: fillers)
		{
			JComponent jComponent = filler.getJcomponent();
			panel.add(jComponent);
			jComponent.setBounds(filler.getAssignedX(), filler.getAssignedY(), filler.getAssignedWidth(), filler.getAssignedHeight());
			jComponent.setBorder(BorderFactory.createLineBorder(Color.ORANGE));

			System.out.println("\tX: " + filler.getAssignedX() + " Y: " + filler.getAssignedY() + " W: " + filler.getAssignedWidth() + " H: " + filler.getAssignedHeight());
		}
		
        JScrollPane scrollPane = new JScrollPane(panel);
        
        WidthHeightRange layout = finalLayoutCases.get(comboBoxSelectedId);
        
		if(layout.isHasVerticalScroll())
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		else
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		
		if(layout.isHasHorizontalScroll())
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		else
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        scrollPane.setPreferredSize(new Dimension(width, height));
        
        JPanel contentPane = new JPanel();
        contentPane.setPreferredSize(new Dimension(width, height));
        contentPane.add(scrollPane);
		
		resultFrame.add(scrollPane);
		resultFrame.pack();
		resultFrame.setVisible(true);
	}
	
	public void ChangeLayout()
	{
		if(comboBoxSelectedId == -1 || finalLayoutCases.length == 0)
			return;
		
		int width = this.width;
		int height = this.height;
		
		WidthHeightRange layout = finalLayoutCases.get(comboBoxSelectedId);
		
		if(layout.isHasVerticalScroll())
			height = layout.getMinHeight();
		
		if(layout.isHasHorizontalScroll())
			width = layout.getMinWidth();
		
		fillers.clear();
		root.GetFinalLayout(0, 0, width, height, layout);
		
		Draw(width, height);
	}
	
	public void Resize()
	{
		if(!started)
		{
			started = true;
			return;
		}
		
		int width, height;
		
		width = (int) resultFrame.getContentPane().getSize().getWidth();
		height = (int) resultFrame.getContentPane().getSize().getHeight();
		
		if(this.width != width || this.height != height)
		{
			SetSize(width, height);
			processType = ProcessEnum.FEASIBLE;

			finalLayoutCases = null;
			Calculate();
			ClassifyLayouts();
			
			int finalLayoutId = TestAesthetics();
			comboBoxSelectedId = finalLayoutId;
			Controller.controller.ChangeComboBox();
			
			ChangeLayout();
			
			System.out.println("Resized.");
		}
	}
	
	public static Layoutable CreateTreePiece(String label, JComponent jcomponent, ComponentDimensionEnum size)
	{
		LayoutComponent newComponent = new LayoutComponent(label, jcomponent, size);
		components.add(newComponent);
		
		return newComponent;
	}
	
	public static Layoutable CreateTreePiece(String label, Layoutable... layoutables)
	{
		return new LayoutContainer(label, layoutables);
	}
	
	public static void AddFiller(LayoutComponent fillerComponent)
	{
		fillers.add(fillerComponent);
	}
	
	public void SetAlignment(int hor, int ver)
	{
		horizontalAlignment = AlignmentEnum.values()[hor];
		verticalAlignment = AlignmentEnum.values()[ver];
	}
}
