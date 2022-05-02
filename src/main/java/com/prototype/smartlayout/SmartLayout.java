package com.prototype.smartlayout;

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
import java.util.ArrayList;
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
	
	public int width;	
	public int height;
	public AlignmentEnum horizontalAlignment;
	public AlignmentEnum verticalAlignment;
	
	private Layoutable root;
	private ExtendedArray<WidthHeightRange> finalLayoutCases;
	private ArrayList<ExtendedArrayList<WidthHeightRange>> layoutCases;
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
		layoutCases = new ArrayList<ExtendedArrayList<WidthHeightRange>>(5);
		
		ExtendedArrayList<WidthHeightRange> feasibleLayouts = new ExtendedArrayList<WidthHeightRange>(WidthHeightRange.class);
		ExtendedArrayList<WidthHeightRange> fillerLayouts = new ExtendedArrayList<WidthHeightRange>(WidthHeightRange.class);
		ExtendedArrayList<WidthHeightRange> verticalLayouts = new ExtendedArrayList<WidthHeightRange>(WidthHeightRange.class);
		ExtendedArrayList<WidthHeightRange> horizontalLayouts = new ExtendedArrayList<WidthHeightRange>(WidthHeightRange.class);
		ExtendedArrayList<WidthHeightRange> verticalAndHorizontalLayouts = new ExtendedArrayList<WidthHeightRange>(WidthHeightRange.class);
		
		layoutCases.add(feasibleLayouts);
		layoutCases.add(fillerLayouts);
		layoutCases.add(verticalLayouts);
		layoutCases.add(horizontalLayouts);
		layoutCases.add(verticalAndHorizontalLayouts);
	}
	
	public void Run(int width, int height)
	{
		SetSize(width, height);
		Calculate(width, height);
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
	
	private void Calculate(int width, int height)
	{
		root.Print(0);
		
		long startTime, elapsedTime;
		startTime = System.nanoTime();
		
		//((LayoutContainer) root).clearMemoization(); //root layout container değilse ne olacak?
		//finalLayoutCases.clear();
		//finalLayoutCases = new Vector<WidthHeightRange>();

		//Collections.addAll(finalLayoutCases, root.GetRanges(width, height));
		do
		{
			
		}
		while(finalLayoutCases == null);
		finalLayoutCases = root.GetRanges(width, height);

		elapsedTime = System.nanoTime() - startTime;
		System.out.println("CALCULATE - Get Ranges Time: " + elapsedTime);
		
		startTime = System.nanoTime();
		
		//for(WidthHeightRange flc: finalLayoutCases) { flc.ListRange(0); }
		
		elapsedTime = System.nanoTime() - startTime;
		System.out.println("CALCULATE - List Ranges Time: " + elapsedTime);
		
		startTime = System.nanoTime();
		
		//System.out.println(finalLayoutCases.size());
		
		comboBoxLayouts = new ExtendedArray<String>(String.class, finalLayoutCases.length);
		
		for(long i = 0; i < finalLayoutCases.length; i++)
		{
			if(finalLayoutCases.get(i) != null)
				comboBoxLayouts.set(finalLayoutCases.get(i).ToString(), i);
		}

		elapsedTime = System.nanoTime() - startTime;
		System.out.println("CALCULATE - Arrange Combobox Time: " + elapsedTime);
	}
	
	private void ClassifyLayouts()
	{
		long startTime, elapsedTime;
		ExtendedArrayList<WidthHeightRange> tempLayoutCases = new ExtendedArrayList<WidthHeightRange>(WidthHeightRange.class);
		
		for(ExtendedArrayList<WidthHeightRange> layoutCase: layoutCases)
		{
			layoutCase.clear();
		}
		
		startTime = System.nanoTime();
		
		for(long i = 0; i < finalLayoutCases.length; i++)
		{
			WidthHeightRange flc = finalLayoutCases.get(i);
			
			if(flc == null)
				return;
			
			boolean feasible = flc.TestFeasiblity(width, height);
			
			if(!feasible)
			{
				boolean needVerticalScroll = flc.TestVerticalScrollNeed(height);
				flc.setHasVerticalScroll(needVerticalScroll);
				
				boolean needHorizontalScroll = flc.TestHorizontalScrollNeed(width);
				flc.setHasHorizontalScroll(needHorizontalScroll);
				
				boolean needFiller = flc.TestFillerNeed(width, height);
				flc.setHasFiller(needFiller);
			}
			
			if(!flc.isHasHorizontalScroll() && !flc.isHasVerticalScroll() && !flc.isHasFiller())
			{//Feasible
				layoutCases.get(0).add(flc);
			}
			else if(!flc.isHasHorizontalScroll() && !flc.isHasVerticalScroll() && flc.isHasFiller())
			{//Filler
				layoutCases.get(1).add(flc);
			}
			else if(!flc.isHasHorizontalScroll() && flc.isHasVerticalScroll())
			{//Vertical
				layoutCases.get(2).add(flc);
			}
			else if(flc.isHasHorizontalScroll() && !flc.isHasVerticalScroll())
			{//Horizontal
				layoutCases.get(3).add(flc);
			}
			else
			{//Vertical and Horizontal
				layoutCases.get(4).add(flc);
			}
		}

		elapsedTime = System.nanoTime() - startTime;
		System.out.println("CLASSIFY LAYOUTS - Classify Layouts Time: " + elapsedTime);
		startTime = System.nanoTime();

		//finalLayoutCases.clear();
		tempLayoutCases.addAll(layoutCases.get(0));
		tempLayoutCases.addAll(layoutCases.get(1));
		tempLayoutCases.addAll(layoutCases.get(2));
		tempLayoutCases.addAll(layoutCases.get(3));
		tempLayoutCases.addAll(layoutCases.get(4));

		finalLayoutCases = new ExtendedArray<WidthHeightRange>(WidthHeightRange.class, finalLayoutCases.length);
		finalLayoutCases.makeArrayOf(tempLayoutCases);

		elapsedTime = System.nanoTime() - startTime;
		System.out.println("CLASSIFY LAYOUTS - Rearrange Layout Cases Time: " + elapsedTime);
	}
	
	private int TestAesthetics()
	{
		float maxAesthetics = 0;
		int finalLayoutId = -1;
		int idCounter = 0;
		
		long startTime, elapsedTime;
		startTime = System.nanoTime();

		for(ExtendedArrayList<WidthHeightRange> layoutCase: layoutCases)
		{
			if(layoutCase.length != 0)
			{
				for(long i = 0; i < layoutCase.length; i++)
				{
					WidthHeightRange layout = layoutCase.get(i);
					
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
					root.GetFinalLayout(0, 0, layoutWidth, layoutHeight, layout);
					float aesthetics = AestheticMeasureUtil.MeasureAesthetics(width, height, layoutWidth, layoutHeight);
					
					if(aesthetics > maxAesthetics)
					{
						maxAesthetics = aesthetics;
						finalLayoutId = idCounter;
					}
					
					idCounter++;
				}
				
				break;
			}
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
			ClassifyLayouts();
			
			int finalLayoutId = TestAesthetics();
			comboBoxSelectedId = finalLayoutId;
			
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
