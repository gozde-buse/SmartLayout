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
	private static final long serialVersionUID = 1L;
	private static Controller controller;
	private static TestScenario test;
	
	private JTextField widthTextField, heightTextField;
	private JComboBox<String> horAlignmentComboBox, verAlignmentComboBox;
	private JComboBox<String> layoutComboBox;
	private JButton changeButton;
	
	private Layoutable layout;

	public static void main(String[] args)
	{
		controller = new Controller();
		controller.CreateControlPanel();
		
		test = new TestScenario();
		controller.layout = test.SchengenVisaApplicationForm();
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
		settingsPanel.setPreferredSize(new Dimension(312, 132));
		settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
		settingsPanel.add(sizePanel);
		settingsPanel.add(Box.createVerticalStrut(12));
		settingsPanel.add(alignmentPanel);
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

		SmartLayout.app = new SmartLayout(layout);
		SmartLayout.app.SetAlignment(horAlignmentComboBox.getSelectedIndex(), verAlignmentComboBox.getSelectedIndex());
		SmartLayout.app.Run(width, height);
		
		for(int i = 0; i < SmartLayout.app.comboBoxLayouts.length; i++)
		{
			layoutComboBox.addItem(SmartLayout.app.comboBoxLayouts[i]);
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
}

















