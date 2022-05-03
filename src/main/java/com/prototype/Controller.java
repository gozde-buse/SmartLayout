package com.prototype;

import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.prototype.smartlayout.SmartLayout;
import com.prototype.smartlayout.model.Layoutable;
import com.prototype.smartlayout.model.enums.AlignmentEnum;

public class Controller extends JFrame
{
	public static Controller controller;
	
	private static final long serialVersionUID = 1L;
	private static TestScenario test;
	
	private JTextField widthTextField, heightTextField;
	private JComboBox<String> horAlignmentComboBox, verAlignmentComboBox;
	private JComboBox<String> layoutComboBox;
	private JButton changeButton;
	private JTextField fillerTrimTextField, verticalTrimTextField, horizontalTrimTextField;
	
	private Layoutable layout;

	public static void main(String[] args)
	{
		controller = new Controller();
		controller.CreateControlPanel();
		
		test = new TestScenario();
		controller.layout = test.SchengenVisa51CompApplicationForm();
	}
	
	private void CreateControlPanel()
	{
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		JLabel widthLabel = new JLabel("Width:");
		widthLabel.setPreferredSize(new Dimension(42, 24));
		widthTextField = new JTextField("");
		widthTextField.setPreferredSize(new Dimension(102, 24));
		
		JPanel widthPanel = new JPanel();
		widthPanel.setPreferredSize(new Dimension(150, 24));
		widthPanel.setLayout(new BoxLayout(widthPanel, BoxLayout.X_AXIS));
		widthPanel.add(widthLabel);
		widthPanel.add(Box.createHorizontalStrut(4));
		widthPanel.add(widthTextField);
		
		JLabel heightLabel = new JLabel("Height:");
		heightLabel.setPreferredSize(new Dimension(42, 24));
		heightTextField = new JTextField("");
		heightTextField.setPreferredSize(new Dimension(102, 24));
		
		JPanel heightPanel = new JPanel();
		heightPanel.setPreferredSize(new Dimension(150, 24));
		heightPanel.setLayout(new BoxLayout(heightPanel, BoxLayout.X_AXIS));
		heightPanel.add(heightLabel);
		heightPanel.add(Box.createHorizontalStrut(4));
		heightPanel.add(heightTextField);
		
		JPanel sizePanel = new JPanel();
		sizePanel.setPreferredSize(new Dimension(360, 24));
		sizePanel.setLayout(new BoxLayout(sizePanel, BoxLayout.X_AXIS));
		sizePanel.add(Box.createHorizontalStrut(24));
		sizePanel.add(widthPanel);
		sizePanel.add(Box.createHorizontalStrut(12));
		sizePanel.add(heightPanel);
		sizePanel.add(Box.createHorizontalStrut(24));
		
		String[] alignments = { AlignmentEnum.START.toString(), AlignmentEnum.CENTER.toString(), AlignmentEnum.END.toString() };
		
		JLabel horAlignmentLabel = new JLabel("Horizontal Align.:");
		horAlignmentLabel.setPreferredSize(new Dimension(124, 24));
		horAlignmentComboBox = new JComboBox<String>();
		horAlignmentComboBox.setPreferredSize(new Dimension(184, 24));
		
		JPanel horAlignmentPanel = new JPanel();
		horAlignmentPanel.setPreferredSize(new Dimension(312, 24));
		horAlignmentPanel.setLayout(new BoxLayout(horAlignmentPanel, BoxLayout.X_AXIS));
		horAlignmentPanel.add(Box.createHorizontalStrut(24));
		horAlignmentPanel.add(horAlignmentLabel);
		horAlignmentPanel.add(Box.createHorizontalStrut(4));
		horAlignmentPanel.add(horAlignmentComboBox);
		horAlignmentPanel.add(Box.createHorizontalStrut(24));
		
		JLabel verAlignmentLabel = new JLabel("Vertical Align.:");
		verAlignmentLabel.setPreferredSize(new Dimension(124, 24));
		verAlignmentComboBox = new JComboBox<String>();
		verAlignmentComboBox.setPreferredSize(new Dimension(184, 24));
		
		JPanel verAlignmentPanel = new JPanel();
		verAlignmentPanel.setPreferredSize(new Dimension(312, 24));
		verAlignmentPanel.setLayout(new BoxLayout(verAlignmentPanel, BoxLayout.X_AXIS));
		verAlignmentPanel.add(Box.createHorizontalStrut(24));
		verAlignmentPanel.add(verAlignmentLabel);
		verAlignmentPanel.add(Box.createHorizontalStrut(4));
		verAlignmentPanel.add(verAlignmentComboBox);
		verAlignmentPanel.add(Box.createHorizontalStrut(24));
		
		for(int i = 0; i < alignments.length; i++)
		{
			horAlignmentComboBox.addItem(alignments[i]);
			verAlignmentComboBox.addItem(alignments[i]);
		}
		
		JPanel alignmentPanel = new JPanel();
		alignmentPanel.setPreferredSize(new Dimension(312, 52));
		alignmentPanel.setLayout(new BoxLayout(alignmentPanel, BoxLayout.Y_AXIS));
		alignmentPanel.add(horAlignmentPanel);
		alignmentPanel.add(Box.createVerticalStrut(4));
		alignmentPanel.add(verAlignmentPanel);
		
		JLabel fillerTrimLabel = new JLabel("Filler Trim Ratio:");
		fillerTrimLabel.setPreferredSize(new Dimension(200, 24));
		fillerTrimTextField = new JTextField("0");
		fillerTrimTextField.setPreferredSize(new Dimension(24, 24));
		JLabel fillerTrimPercentLabel = new JLabel("/10");
		fillerTrimPercentLabel.setPreferredSize(new Dimension(24, 24));
		
		JPanel fillerTrimPanel = new JPanel();
		fillerTrimPanel.setPreferredSize(new Dimension(312, 24));
		fillerTrimPanel.setLayout(new BoxLayout(fillerTrimPanel, BoxLayout.X_AXIS));
		fillerTrimPanel.add(Box.createHorizontalStrut(24));
		fillerTrimPanel.add(fillerTrimLabel);
		fillerTrimPanel.add(Box.createHorizontalStrut(4));
		fillerTrimPanel.add(fillerTrimTextField);
		fillerTrimPanel.add(Box.createHorizontalStrut(4));
		fillerTrimPanel.add(fillerTrimPercentLabel);
		fillerTrimPanel.add(Box.createHorizontalStrut(24));
		
		JLabel verticalTrimLabel = new JLabel("Vertical Scroll Trim Ratio:");
		verticalTrimLabel.setPreferredSize(new Dimension(200, 24));
		verticalTrimTextField = new JTextField("0");
		verticalTrimTextField.setPreferredSize(new Dimension(24, 24));
		JLabel verticalTrimPercentLabel = new JLabel("/10");
		verticalTrimPercentLabel.setPreferredSize(new Dimension(24, 24));
		
		JPanel verticalTrimPanel = new JPanel();
		verticalTrimPanel.setPreferredSize(new Dimension(312, 24));
		verticalTrimPanel.setLayout(new BoxLayout(verticalTrimPanel, BoxLayout.X_AXIS));
		verticalTrimPanel.add(Box.createHorizontalStrut(24));
		verticalTrimPanel.add(verticalTrimLabel);
		verticalTrimPanel.add(Box.createHorizontalStrut(4));
		verticalTrimPanel.add(verticalTrimTextField);
		verticalTrimPanel.add(Box.createHorizontalStrut(4));
		verticalTrimPanel.add(verticalTrimPercentLabel);
		verticalTrimPanel.add(Box.createHorizontalStrut(24));
		
		JLabel horizontalTrimLabel = new JLabel("Horizontal Scroll Trim Ratio:");
		horizontalTrimLabel.setPreferredSize(new Dimension(200, 24));
		horizontalTrimTextField = new JTextField("0");
		horizontalTrimTextField.setPreferredSize(new Dimension(24, 24));
		JLabel horizontalTrimPercentLabel = new JLabel("/10");
		horizontalTrimPercentLabel.setPreferredSize(new Dimension(24, 24));
		
		JPanel horizontalTrimPanel = new JPanel();
		horizontalTrimPanel.setPreferredSize(new Dimension(312, 24));
		horizontalTrimPanel.setLayout(new BoxLayout(horizontalTrimPanel, BoxLayout.X_AXIS));
		horizontalTrimPanel.add(Box.createHorizontalStrut(24));
		horizontalTrimPanel.add(horizontalTrimLabel);
		horizontalTrimPanel.add(Box.createHorizontalStrut(4));
		horizontalTrimPanel.add(horizontalTrimTextField);
		horizontalTrimPanel.add(Box.createHorizontalStrut(4));
		horizontalTrimPanel.add(horizontalTrimPercentLabel);
		horizontalTrimPanel.add(Box.createHorizontalStrut(24));
		
		JPanel trimPanel = new JPanel();
		trimPanel.setPreferredSize(new Dimension(312, 80));
		trimPanel.setLayout(new BoxLayout(trimPanel, BoxLayout.Y_AXIS));
		trimPanel.add(fillerTrimPanel);
		trimPanel.add(Box.createVerticalStrut(4));
		trimPanel.add(verticalTrimPanel);
		trimPanel.add(Box.createVerticalStrut(4));
		trimPanel.add(horizontalTrimPanel);
		
		JButton runButton = new JButton("Run");
		runButton.setPreferredSize(new Dimension(312, 24));
		runButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) { Run(); }
		});
		
		JPanel runPanel = new JPanel();
		runPanel.setPreferredSize(new Dimension(312, 32));
		runPanel.add(runButton);
		
		JPanel settingsPanel = new JPanel();
		settingsPanel.setPreferredSize(new Dimension(312, 224));
		settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
		settingsPanel.add(sizePanel);
		settingsPanel.add(Box.createVerticalStrut(12));
		settingsPanel.add(alignmentPanel);
		settingsPanel.add(Box.createVerticalStrut(12));
		settingsPanel.add(trimPanel);
		settingsPanel.add(Box.createVerticalStrut(12));
		settingsPanel.add(runPanel);
		
		layoutComboBox = new JComboBox<String>();
		layoutComboBox.setPreferredSize(new Dimension(184, 24));
		layoutComboBox.setEnabled(false);
		
		changeButton = new JButton("Change");
		changeButton.setPreferredSize(new Dimension(124, 24));
		changeButton.setEnabled(false);
		changeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) { ChangeLayout(); }
		});

		JPanel layoutPanel = new JPanel();
		layoutPanel.setMaximumSize(new Dimension(360, 24));
		layoutPanel.setLayout(new BoxLayout(layoutPanel, BoxLayout.X_AXIS));
		layoutPanel.add(Box.createHorizontalStrut(24));
		layoutPanel.add(layoutComboBox);
		layoutPanel.add(Box.createHorizontalStrut(4));
		layoutPanel.add(changeButton);
		layoutPanel.add(Box.createHorizontalStrut(24));
		
		JPanel controlPanel = new JPanel();
		controlPanel.setMaximumSize(new Dimension(360, 180));
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		controlPanel.add(Box.createVerticalStrut(18));
		controlPanel.add(settingsPanel);
		controlPanel.add(Box.createVerticalStrut(24));
		controlPanel.add(layoutPanel);
		controlPanel.add(Box.createVerticalStrut(18));
		
		controller.add(controlPanel);
		controller.pack();
		setVisible(true);
	}
	
	private void Run()
	{
		String widthString = widthTextField.getText();
		String heightString = heightTextField.getText();
		String fillerTrimRatioString = fillerTrimTextField.getText();
		String verticalTrimRatioString = verticalTrimTextField.getText();
		String horizontalTrimRatioString = horizontalTrimTextField.getText();
		
		int width, height, fillerTrimRatio, verticalTrimRatio, horizontalTrimRatio;
		
		try
		{
			width = Integer.parseInt(widthString);
			height = Integer.parseInt(heightString);
			fillerTrimRatio = Integer.parseInt(fillerTrimRatioString);
			verticalTrimRatio = Integer.parseInt(verticalTrimRatioString);
			horizontalTrimRatio = Integer.parseInt(horizontalTrimRatioString);
	    }
		catch (NumberFormatException e)
		{
			System.out.println("Sayı girilmedi!!");
			
	        return;
	    }
		
		if(fillerTrimRatio < 0 || fillerTrimRatio > 10 || verticalTrimRatio < 0 || verticalTrimRatio > 10
				|| horizontalTrimRatio < 0 || horizontalTrimRatio > 10)
		{
			System.out.println("Trim değerleri 0-10 arasında olmalı!!");
			
			return;
		}
		

		SmartLayout.app = new SmartLayout(layout);
		SmartLayout.app.SetAlignment(horAlignmentComboBox.getSelectedIndex(), verAlignmentComboBox.getSelectedIndex());
		SmartLayout.app.Run(width, height, fillerTrimRatio, verticalTrimRatio, horizontalTrimRatio);
		
		for(int i = 0; i < SmartLayout.app.comboBoxLayouts.length; i++)
		{
			layoutComboBox.addItem(SmartLayout.app.comboBoxLayouts.get(i));
		}
		
		layoutComboBox.setSelectedIndex(SmartLayout.app.comboBoxSelectedId);
		layoutComboBox.setEnabled(true);
		changeButton.setEnabled(true);
	}
	
	private void ChangeLayout()
	{
		SmartLayout.app.comboBoxSelectedId = layoutComboBox.getSelectedIndex();
		
		String widthString = widthTextField.getText();
		String heightString = heightTextField.getText();
		
		int width, height;
		
		try
		{
			width = Integer.parseInt(widthString);
			height = Integer.parseInt(heightString);
	    }
		catch (NumberFormatException e)
		{
			System.out.println("Sayı girilmedi!!");
			
	        return;
	    }
		
		SmartLayout.app.width = width;
		SmartLayout.app.height = height;
		SmartLayout.app.ChangeLayout();
	}
	
	public void ChangeComboBox()
	{
		layoutComboBox.removeAllItems();
		
		for(int i = 0; i < SmartLayout.app.comboBoxLayouts.length; i++)
		{
			layoutComboBox.addItem(SmartLayout.app.comboBoxLayouts.get(i));
		}
		
		layoutComboBox.setSelectedIndex(SmartLayout.app.comboBoxSelectedId);
	}
}