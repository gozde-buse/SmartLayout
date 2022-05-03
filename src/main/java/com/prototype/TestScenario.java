package com.prototype;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.prototype.smartlayout.SmartLayout;
import com.prototype.smartlayout.model.Layoutable;
import com.prototype.smartlayout.model.enums.ComponentDimensionEnum;

public class TestScenario
{
	public Layoutable SimpleTest()
	{
		JLabel ALabel = new JLabel("A");
		JLabel BLabel = new JLabel("B");
		JLabel CLabel = new JLabel("C");
        
		Layoutable A = SmartLayout.CreateTreePiece("A", ALabel, ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable B = SmartLayout.CreateTreePiece("B", BLabel, ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable C = SmartLayout.CreateTreePiece("C", CLabel, ComponentDimensionEnum.MEDIUM_SMALLER);
		
		Layoutable testScenario = SmartLayout.CreateTreePiece("D", A, B, C);
		
		return testScenario;
	}
	
	public Layoutable SimpleTest2()
	{
		Layoutable A = SmartLayout.CreateTreePiece("A", new JLabel("A"), ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable B = SmartLayout.CreateTreePiece("B", new JLabel("B"), ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable C = SmartLayout.CreateTreePiece("C", new JLabel("C"), ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable D = SmartLayout.CreateTreePiece("D", A, B, C);
		Layoutable E = SmartLayout.CreateTreePiece("E", new JLabel("E"), ComponentDimensionEnum.MEDIUM_LARGER);
		
		Layoutable testScenario = SmartLayout.CreateTreePiece("F", D, E);
		
		return testScenario;
	}
	
	public Layoutable CreateDictionaryTestCase1()
	{
		Layoutable compA = SmartLayout.CreateTreePiece("A", new JLabel(), ComponentDimensionEnum.SMALL_SMALL);
		Layoutable compB = SmartLayout.CreateTreePiece("B", new JButton(), ComponentDimensionEnum.SMALL_SLACK_SMALL_SLACK);
		Layoutable compC = SmartLayout.CreateTreePiece("C", new JButton(), ComponentDimensionEnum.SMALL_SLACK_SMALL_SLACK);
		Layoutable compD = SmartLayout.CreateTreePiece("D", new JTextField(), ComponentDimensionEnum.LARGE_SLACK_LARGE_SLACK);
		Layoutable compE = SmartLayout.CreateTreePiece("E", new JTextArea(), ComponentDimensionEnum.SMALL_LARGE_SLACK);
		Layoutable compF = SmartLayout.CreateTreePiece("F", new JComboBox<String>(), ComponentDimensionEnum.SMALL_LARGE_SLACK);
		Layoutable compG = SmartLayout.CreateTreePiece("G", new JRadioButton(), ComponentDimensionEnum.LARGE_SLACK_SMALL);
		Layoutable contX = SmartLayout.CreateTreePiece("X", compA, compB, compC);
		Layoutable contY = SmartLayout.CreateTreePiece("Y", compE, compF);
		Layoutable contZ = SmartLayout.CreateTreePiece("Z", contY, compG);
		
		Layoutable testScenario = SmartLayout.CreateTreePiece("ROOT", contZ, contX, compD);
		
		return testScenario;
	}
	
	public Layoutable CreateDictionaryTestCase2()
	{
		Layoutable compA = SmartLayout.CreateTreePiece("A", new JLabel(), ComponentDimensionEnum.SMALL_SMALL);
		Layoutable compB = SmartLayout.CreateTreePiece("B", new JButton(), ComponentDimensionEnum.SMALL_SLACK_SMALL_SLACK);
		Layoutable compC = SmartLayout.CreateTreePiece("C", new JButton(), ComponentDimensionEnum.SMALL_SLACK_SMALL_SLACK);
		Layoutable compD = SmartLayout.CreateTreePiece("D", new JTextField(), ComponentDimensionEnum.LARGE_SLACK_LARGE_SLACK);
		Layoutable compE = SmartLayout.CreateTreePiece("E", new JTextArea(), ComponentDimensionEnum.SMALL_LARGE_SLACK);
		Layoutable compF = SmartLayout.CreateTreePiece("F", new JComboBox<String>(), ComponentDimensionEnum.SMALL_LARGE_SLACK);
		Layoutable compG = SmartLayout.CreateTreePiece("G", new JRadioButton(), ComponentDimensionEnum.LARGE_SLACK_SMALL);
		Layoutable compH = SmartLayout.CreateTreePiece("H", new JLabel(), ComponentDimensionEnum.SMALL_SLACK_SMALL_SLACK);
		Layoutable compI = SmartLayout.CreateTreePiece("I", new JButton(), ComponentDimensionEnum.LARGE_SLACK_LARGE_SLACK);
		Layoutable compJ = SmartLayout.CreateTreePiece("J", new JButton(), ComponentDimensionEnum.LARGE_SLACK_LARGE_SLACK);
		Layoutable compK = SmartLayout.CreateTreePiece("K", new JTextField(), ComponentDimensionEnum.SMALL_SMALL);
		Layoutable compL = SmartLayout.CreateTreePiece("L", new JTextArea(), ComponentDimensionEnum.SMALL_SMALL);

		Layoutable contY = SmartLayout.CreateTreePiece("Y", compE, compF);
		Layoutable contT = SmartLayout.CreateTreePiece("T", compK, compL);
		Layoutable contR = SmartLayout.CreateTreePiece("R", contT, compJ);
		Layoutable contN = SmartLayout.CreateTreePiece("N", compG, compH, compI);
		Layoutable contZ = SmartLayout.CreateTreePiece("Z", contY, contR, contN);
		Layoutable contX = SmartLayout.CreateTreePiece("X", compA, compB, compC);
		Layoutable testScenario = SmartLayout.CreateTreePiece("ROOT", contZ, contX, compD);
		
		return testScenario;
	}
	
	public Layoutable CreateDictionaryTestCase3()
	{
		Layoutable compA = SmartLayout.CreateTreePiece("A", new JLabel(), ComponentDimensionEnum.SMALL_SMALL);
		Layoutable compB = SmartLayout.CreateTreePiece("B", new JButton(), ComponentDimensionEnum.LARGE_LARGE);
		Layoutable compC = SmartLayout.CreateTreePiece("C", new JButton(), ComponentDimensionEnum.SMALL_SLACK_SMALL_SLACK);
		Layoutable compD = SmartLayout.CreateTreePiece("D", new JTextField(), ComponentDimensionEnum.LARGE_SLACK_LARGE_SLACK);
		Layoutable compE = SmartLayout.CreateTreePiece("E", new JTextArea(), ComponentDimensionEnum.SMALL_LARGE_SLACK);
		Layoutable compF = SmartLayout.CreateTreePiece("F", new JComboBox<String>(), ComponentDimensionEnum.SMALL_LARGE_SLACK);
		Layoutable compG = SmartLayout.CreateTreePiece("G", new JRadioButton(), ComponentDimensionEnum.LARGE_SLACK_SMALL);
		Layoutable compH = SmartLayout.CreateTreePiece("H", new JLabel(), ComponentDimensionEnum.SMALL_SLACK_SMALL_SLACK);
		Layoutable compI = SmartLayout.CreateTreePiece("I", new JButton(), ComponentDimensionEnum.LARGE_SLACK_LARGE_SLACK);
		Layoutable compJ = SmartLayout.CreateTreePiece("J", new JButton(), ComponentDimensionEnum.LARGE_SLACK_LARGE_SLACK);
		Layoutable compK = SmartLayout.CreateTreePiece("K", new JTextField(), ComponentDimensionEnum.SMALL_SMALL);
		Layoutable compL = SmartLayout.CreateTreePiece("L", new JTextArea(), ComponentDimensionEnum.SMALL_SMALL);

		Layoutable contY = SmartLayout.CreateTreePiece("Y", compE, compF);
		Layoutable contT = SmartLayout.CreateTreePiece("T", compK, compL);
		Layoutable contR = SmartLayout.CreateTreePiece("R", contT, compJ);
		Layoutable contN = SmartLayout.CreateTreePiece("N", compG, compH, compI);
		Layoutable contZ = SmartLayout.CreateTreePiece("Z", contY, contR, contN);
		Layoutable contX = SmartLayout.CreateTreePiece("X", compA, compB, compC);
		
		Layoutable testScenario = SmartLayout.CreateTreePiece("ROOT", contZ, contX, compD);;
		
		return testScenario;
	}
	
	public Layoutable DictionaryTestCaseWithComponents1()
	{
		Layoutable compA = SmartLayout.CreateTreePiece("A", new JLabel(), ComponentDimensionEnum.SMALLER_TINY);
		Layoutable compB = SmartLayout.CreateTreePiece("B", new JTextField(), ComponentDimensionEnum.SMALL_SLACK_TINY);
		Layoutable compC = SmartLayout.CreateTreePiece("C", new JComboBox<>(), ComponentDimensionEnum.MEDIUM_TINY);
		Layoutable compD = SmartLayout.CreateTreePiece("D", new JTextField(), ComponentDimensionEnum.SMALL_SLACK_TINY);
		Layoutable compE = SmartLayout.CreateTreePiece("E", new JLabel(), ComponentDimensionEnum.SMALLER_SMALL_SLACK);
		Layoutable compF = SmartLayout.CreateTreePiece("F", new JComboBox<>(), ComponentDimensionEnum.MEDIUM_TINY);
		Layoutable compG = SmartLayout.CreateTreePiece("G", new JLabel(), ComponentDimensionEnum.SMALLER_SMALL_SLACK);
		Layoutable compH = SmartLayout.CreateTreePiece("H", new JTextField(), ComponentDimensionEnum.SMALL_SLACK_TINY);
		Layoutable compI = SmartLayout.CreateTreePiece("I", new JButton(), ComponentDimensionEnum.SMALL_SLACK_TINY);
		Layoutable compJ = SmartLayout.CreateTreePiece("J", new JButton(), ComponentDimensionEnum.SMALL_SLACK_TINY);
		Layoutable compK = SmartLayout.CreateTreePiece("K", new JLabel(), ComponentDimensionEnum.SMALLER_SMALL_SLACK);
		Layoutable compL = SmartLayout.CreateTreePiece("L", new JTextField(), ComponentDimensionEnum.SMALL_SLACK_TINY);
		Layoutable compM = SmartLayout.CreateTreePiece("M", new JLabel(), ComponentDimensionEnum.SMALLER_SMALL_SLACK);
		Layoutable compN = SmartLayout.CreateTreePiece("N", new JTextField(), ComponentDimensionEnum.SMALL_SLACK_TINY);
		Layoutable compO = SmartLayout.CreateTreePiece("O", new JLabel(), ComponentDimensionEnum.SMALLER_SMALL_SLACK);
		Layoutable compP = SmartLayout.CreateTreePiece("P", new JComboBox<>(), ComponentDimensionEnum.MEDIUM_TINY);
		Layoutable compQ = SmartLayout.CreateTreePiece("Q", new JLabel(), ComponentDimensionEnum.SMALLER_SMALL_SLACK);
		Layoutable compR = SmartLayout.CreateTreePiece("R", new JTextField(), ComponentDimensionEnum.SMALL_SLACK_TINY);
		Layoutable compS = SmartLayout.CreateTreePiece("S", new JButton(), ComponentDimensionEnum.SMALL_SLACK_TINY);
		Layoutable compT = SmartLayout.CreateTreePiece("T", new JButton(), ComponentDimensionEnum.SMALL_SLACK_TINY);

		Layoutable cont1 = SmartLayout.CreateTreePiece("1", compA, compB, compC);
		Layoutable cont2 = SmartLayout.CreateTreePiece("2", compE, compD);
		Layoutable cont3 = SmartLayout.CreateTreePiece("3", cont1, cont2);
		Layoutable cont4 = SmartLayout.CreateTreePiece("4", compG, compF);
		Layoutable cont5 = SmartLayout.CreateTreePiece("5", compH, compI);
		Layoutable cont6 = SmartLayout.CreateTreePiece("6", cont4, cont5, compJ);
		Layoutable cont7 = SmartLayout.CreateTreePiece("7", cont3, cont6);
		Layoutable cont8 = SmartLayout.CreateTreePiece("8", compK, compL);
		Layoutable cont9 = SmartLayout.CreateTreePiece("9", compM, compN);
		Layoutable cont10 = SmartLayout.CreateTreePiece("10", compO, compP);
		Layoutable cont11 = SmartLayout.CreateTreePiece("11", cont8, cont9, cont10);
		Layoutable cont12 = SmartLayout.CreateTreePiece("12", compQ, compR);
		Layoutable cont13 = SmartLayout.CreateTreePiece("13", cont12, compS, compT);
		
		Layoutable testScenario = SmartLayout.CreateTreePiece("ROOT", cont7, cont11, cont13);
		
		return testScenario;
	}
	
	public Layoutable DictionaryTestCaseWithComponents2()
	{
		Layoutable compA = SmartLayout.CreateTreePiece("A", new JLabel(), ComponentDimensionEnum.SMALLER_SMALL_SLACK);
		Layoutable compB = SmartLayout.CreateTreePiece("B", new JTextField(), ComponentDimensionEnum.SMALL_SLACK_TINY);
		Layoutable compC = SmartLayout.CreateTreePiece("C", new JLabel(), ComponentDimensionEnum.SMALLER_SMALL_SLACK);
		Layoutable compD = SmartLayout.CreateTreePiece("D", new JTextField(), ComponentDimensionEnum.SMALL_SLACK_TINY);
		Layoutable compE = SmartLayout.CreateTreePiece("E", new JLabel(), ComponentDimensionEnum.SMALLER_SMALL_SLACK);
		Layoutable compF = SmartLayout.CreateTreePiece("F", new JComboBox<>(), ComponentDimensionEnum.MEDIUM_TINY);
		Layoutable compG = SmartLayout.CreateTreePiece("G", new JLabel(), ComponentDimensionEnum.SMALLER_SMALL_SLACK);
		Layoutable compH = SmartLayout.CreateTreePiece("H", new JTextField(), ComponentDimensionEnum.SMALL_SLACK_TINY);
		Layoutable compI = SmartLayout.CreateTreePiece("I", new JButton(), ComponentDimensionEnum.SMALL_SLACK_TINY);
		Layoutable compJ = SmartLayout.CreateTreePiece("J", new JButton(), ComponentDimensionEnum.SMALL_SLACK_TINY);

		
		Layoutable contY = SmartLayout.CreateTreePiece("Y", compA, compB); //first line
		Layoutable contT = SmartLayout.CreateTreePiece("T", compC, compD); //second line
		Layoutable contR = SmartLayout.CreateTreePiece("R", compE, compF); //third line
		Layoutable contN = SmartLayout.CreateTreePiece("N", compG, compH); //fourth line
		Layoutable contZ = SmartLayout.CreateTreePiece("Z", compI, compJ); //buttons
		Layoutable contX = SmartLayout.CreateTreePiece("X", contY, contT); //first two lines
		Layoutable contQ = SmartLayout.CreateTreePiece("Q", contR, contN); //last two lines
		Layoutable contU = SmartLayout.CreateTreePiece("U", contX, contQ); //all lines
		
		Layoutable testScenario = SmartLayout.CreateTreePiece("ROOT", contU, contZ); // all lines and buttons
		
		return testScenario;
	}
	
	public Layoutable SchengenVisa10CompApplicationForm()
	{
		Layoutable compALabel = SmartLayout.CreateTreePiece("Surname", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compATextField = SmartLayout.CreateTreePiece("Enter Surname", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable surnameField = SmartLayout.CreateTreePiece("Surname Field", compALabel, compATextField);
		
		Layoutable compBLabel = SmartLayout.CreateTreePiece("<html>Surname<br/>at Birth</html>", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compBTextField = SmartLayout.CreateTreePiece("Enter Surname at Birth", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable surnameAtBirthField = SmartLayout.CreateTreePiece("Surname At Birth Field", compBLabel, compBTextField);
		
		Layoutable compCLabel = SmartLayout.CreateTreePiece("First Name", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compCTextField = SmartLayout.CreateTreePiece("Enter First Name", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable firstNameField = SmartLayout.CreateTreePiece("First Name Field", compCLabel, compCTextField);
		
		Layoutable testScenario = SmartLayout.CreateTreePiece("ROOT", surnameField, surnameAtBirthField, firstNameField);
		
		return testScenario;
	}
	
	public Layoutable SchengenVisa31CompApplicationForm()
	{
		Layoutable compALabel = SmartLayout.CreateTreePiece("Surname", new JLabel("Surname"), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compATextField = SmartLayout.CreateTreePiece("Enter Surname", new JTextField("Enter Surname"), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable surnameField = SmartLayout.CreateTreePiece("Surname Field", compALabel, compATextField);
		
		Layoutable compBLabel = SmartLayout.CreateTreePiece("<html>Surname<br/>at Birth</html>", new JLabel("Surname at Birth"), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compBTextField = SmartLayout.CreateTreePiece("Enter Surname at Birth", new JTextField("Enter Surname at Birth"), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable surnameAtBirthField = SmartLayout.CreateTreePiece("Surname At Birth Field", compBLabel, compBTextField);
		
		Layoutable compCLabel = SmartLayout.CreateTreePiece("First Name", new JLabel("First Name"), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compCTextField = SmartLayout.CreateTreePiece("Enter First Name", new JTextField("Enter First Name"), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable firstNameField = SmartLayout.CreateTreePiece("First Name Field", compCLabel, compCTextField);
		
		Layoutable leftUpperGroup = SmartLayout.CreateTreePiece("Left Upper Group", surnameField, surnameAtBirthField, firstNameField);
		
		Layoutable noticeLabel = SmartLayout.CreateTreePiece("<html>FOR OFFICIAL<br/>USE ONLY</html>", new JLabel("FOR OFFICIAL USE ONLY"), ComponentDimensionEnum.SMALL_SLACK_SMALLER);
		
		Layoutable compDLabel = SmartLayout.CreateTreePiece("<html>Date of<br/>Application</html>", new JLabel("Date of Application"), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compDTextField = SmartLayout.CreateTreePiece("Enter Date of Application", new JTextField("Enter Date of Application"), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable dateOfApplicationField = SmartLayout.CreateTreePiece("Date of Application Field", compDLabel, compDTextField);
		
		Layoutable compELabel = SmartLayout.CreateTreePiece("<html>Application<br/>Number</html>", new JLabel("Application Number"), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compETextField = SmartLayout.CreateTreePiece("Enter Application Number", new JTextField("Enter Application Number"), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable applicationNumberField = SmartLayout.CreateTreePiece("Application Number Field", compELabel, compETextField);
		
		Layoutable rightUpperGroup = SmartLayout.CreateTreePiece("Right Upper Group", noticeLabel, dateOfApplicationField, applicationNumberField);
		
		Layoutable compFLabel = SmartLayout.CreateTreePiece("<html>Date of Birth<br/>(dd-mm-yyyy)</html>", new JLabel("Date of Birth (dd-mm-yyyy)"), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compFTextField = SmartLayout.CreateTreePiece("Enter Birth Date", new JTextField("Enter Birth Date"), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable birthDateField = SmartLayout.CreateTreePiece("Birth Date Field", compFLabel, compFTextField);
		
		Layoutable compGLabel = SmartLayout.CreateTreePiece("Place of Birth", new JLabel("Place of Birth"), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compGTextField = SmartLayout.CreateTreePiece("Enter Place of Birth", new JTextField("Enter Place of Birth"), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable placeOfBirthField = SmartLayout.CreateTreePiece("Place of Birth Field", compGLabel, compGTextField);
		
		Layoutable compHLabel = SmartLayout.CreateTreePiece("Country of Birth", new JLabel("Country of Birth"), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compHTextField = SmartLayout.CreateTreePiece("Enter Country of Birth", new JTextField("Enter Country of Birth"), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable countryOfBirthField = SmartLayout.CreateTreePiece("Country of Birth Field", compHLabel, compHTextField);
		
		Layoutable birthPlaceGroup = SmartLayout.CreateTreePiece("Birth Place Group", placeOfBirthField, countryOfBirthField);
		
		Layoutable lowerGroup = SmartLayout.CreateTreePiece("Lower Group", birthDateField, birthPlaceGroup);
		Layoutable upperGroup = SmartLayout.CreateTreePiece("Upper Group", leftUpperGroup, rightUpperGroup);
		
		Layoutable testScenario = SmartLayout.CreateTreePiece("ROOT", upperGroup, lowerGroup);
		
		return testScenario;
	}
	
	public Layoutable SchengenVisa51CompApplicationForm()
	{
		Layoutable compALabel = SmartLayout.CreateTreePiece("Surname", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compATextField = SmartLayout.CreateTreePiece("Enter Surname", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable surnameField = SmartLayout.CreateTreePiece("Surname Field", compALabel, compATextField);
		
		Layoutable compBLabel = SmartLayout.CreateTreePiece("<html>Surname<br/>at Birth</html>", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compBTextField = SmartLayout.CreateTreePiece("Enter Surname at Birth", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable surnameAtBirthField = SmartLayout.CreateTreePiece("Surname At Birth Field", compBLabel, compBTextField);
		
		Layoutable compCLabel = SmartLayout.CreateTreePiece("First Name", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compCTextField = SmartLayout.CreateTreePiece("Enter First Name", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable firstNameField = SmartLayout.CreateTreePiece("First Name Field", compCLabel, compCTextField);
		
		Layoutable leftUpperGroup = SmartLayout.CreateTreePiece("Left Upper Group", surnameField, surnameAtBirthField, firstNameField);
		
		Layoutable noticeLabel = SmartLayout.CreateTreePiece("<html>FOR OFFICIAL<br/>USE ONLY</html>", new JLabel(), ComponentDimensionEnum.SMALL_SLACK_SMALLER);
		
		Layoutable compDLabel = SmartLayout.CreateTreePiece("<html>Date of<br/>Application</html>", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compDTextField = SmartLayout.CreateTreePiece("Enter Date of Application", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable dateOfApplicationField = SmartLayout.CreateTreePiece("Date of Application Field", compDLabel, compDTextField);
		
		Layoutable compELabel = SmartLayout.CreateTreePiece("<html>Application<br/>Number</html>", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compETextField = SmartLayout.CreateTreePiece("Enter Application Number", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable applicationNumberField = SmartLayout.CreateTreePiece("Application Number Field", compELabel, compETextField);
		
		Layoutable rightUpperGroup = SmartLayout.CreateTreePiece("Right Upper Group", noticeLabel, dateOfApplicationField, applicationNumberField);
		
		Layoutable compFLabel = SmartLayout.CreateTreePiece("<html>Date of Birth<br/>(dd-mm-yyyy)</html>", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compFTextField = SmartLayout.CreateTreePiece("Enter Birth Date", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable birthDateField = SmartLayout.CreateTreePiece("Birth Date Field", compFLabel, compFTextField);
		
		Layoutable compGLabel = SmartLayout.CreateTreePiece("Place of Birth", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compGTextField = SmartLayout.CreateTreePiece("Enter Place of Birth", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable placeOfBirthField = SmartLayout.CreateTreePiece("Place of Birth Field", compGLabel, compGTextField);
		
		Layoutable compHLabel = SmartLayout.CreateTreePiece("Country of Birth", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compHTextField = SmartLayout.CreateTreePiece("Enter Country of Birth", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable countryOfBirthField = SmartLayout.CreateTreePiece("Country of Birth Field", compHLabel, compHTextField);
		
		Layoutable birthPlaceGroup = SmartLayout.CreateTreePiece("Birth Place Group", placeOfBirthField, countryOfBirthField);
		
		Layoutable compILabel = SmartLayout.CreateTreePiece("Current Nationality: ", new JLabel(), ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable compITextField = SmartLayout.CreateTreePiece("Enter Current Nationality", new JTextField(), ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable nationalityField = SmartLayout.CreateTreePiece("Current Nationality Field", compILabel, compITextField);
		
		Layoutable compJLabel = SmartLayout.CreateTreePiece("Nationality at Birth: ", new JLabel(), ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable compJTextField = SmartLayout.CreateTreePiece("Enter Nationality at Birth", new JTextField(), ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable nationalityAtBirthField = SmartLayout.CreateTreePiece("Nationality at birth, if different", compJLabel, compJTextField);
		
		Layoutable compKLabel = SmartLayout.CreateTreePiece("Other Nationalities: ", new JLabel(), ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable compKTextField = SmartLayout.CreateTreePiece("Enter Other nationalities", new JTextField(), ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable otherNationalitiesField = SmartLayout.CreateTreePiece("Other Nationalities Field", compKLabel, compKTextField);
		
		Layoutable nationalityGroup = SmartLayout.CreateTreePiece("Nationality Group", nationalityField, nationalityAtBirthField, otherNationalitiesField);
		Layoutable leftUpperMiddleGroup = SmartLayout.CreateTreePiece("Left Upper Middle Group", birthDateField, birthPlaceGroup, nationalityGroup);
		
		Layoutable genderLabel = SmartLayout.CreateTreePiece("Sex: ", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		
		ButtonGroup genderButtonGroup = new ButtonGroup();
		
		JRadioButton radioButtonA = new JRadioButton();
		JRadioButton radioButtonB = new JRadioButton();
		
		genderButtonGroup.add(radioButtonA);
		genderButtonGroup.add(radioButtonB);
		
		Layoutable compLLabel = SmartLayout.CreateTreePiece("Male", new JLabel(), ComponentDimensionEnum.SMALLER_TINY);
		Layoutable compLRadioButton = SmartLayout.CreateTreePiece("", radioButtonA, ComponentDimensionEnum.TINY_TINY);
		Layoutable genderMaleCheckbox = SmartLayout.CreateTreePiece("Gender Male", compLLabel, compLRadioButton);
		
		Layoutable compMLabel = SmartLayout.CreateTreePiece("Female", new JLabel(), ComponentDimensionEnum.SMALLER_TINY);
		Layoutable compMRadioButton = SmartLayout.CreateTreePiece("", radioButtonB, ComponentDimensionEnum.TINY_TINY);
		Layoutable genderFemaleCheckbox = SmartLayout.CreateTreePiece("Gender Female", compMLabel, compMRadioButton);
		
		Layoutable genderChoicesGroup = SmartLayout.CreateTreePiece("Gender Choices Group", genderMaleCheckbox, genderFemaleCheckbox);
		Layoutable genderGroup = SmartLayout.CreateTreePiece("Gender Group", genderLabel, genderChoicesGroup);
		
		Layoutable upperGroup = SmartLayout.CreateTreePiece("Upper Group", leftUpperGroup, rightUpperGroup);
		Layoutable lowerGroup = SmartLayout.CreateTreePiece("Lower Group", leftUpperMiddleGroup, genderGroup);
		
		Layoutable testScenario = SmartLayout.CreateTreePiece("ROOT", upperGroup, lowerGroup);
		
		return testScenario;
	}
	
	public Layoutable SchengenVisaApplicationForm()
	{
		Layoutable compALabel = SmartLayout.CreateTreePiece("Surname", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compATextField = SmartLayout.CreateTreePiece("Enter Surname", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable surnameField = SmartLayout.CreateTreePiece("Surname Field", compALabel, compATextField);
		
		Layoutable compBLabel = SmartLayout.CreateTreePiece("<html>Surname<br/>at Birth</html>", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compBTextField = SmartLayout.CreateTreePiece("Enter Surname at Birth", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable surnameAtBirthField = SmartLayout.CreateTreePiece("Surname At Birth Field", compBLabel, compBTextField);
		
		Layoutable compCLabel = SmartLayout.CreateTreePiece("First Name", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compCTextField = SmartLayout.CreateTreePiece("Enter First Name", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable firstNameField = SmartLayout.CreateTreePiece("First Name Field", compCLabel, compCTextField);
		
		Layoutable leftUpperGroup = SmartLayout.CreateTreePiece("Left Upper Group", surnameField, surnameAtBirthField, firstNameField);
		
		Layoutable noticeLabel = SmartLayout.CreateTreePiece("<html>FOR OFFICIAL<br/>USE ONLY</html>", new JLabel(), ComponentDimensionEnum.SMALL_SLACK_SMALLER);
		
		Layoutable compDLabel = SmartLayout.CreateTreePiece("<html>Date of<br/>Application</html>", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compDTextField = SmartLayout.CreateTreePiece("Enter Date of Application", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable dateOfApplicationField = SmartLayout.CreateTreePiece("Date of Application Field", compDLabel, compDTextField);
		
		Layoutable compELabel = SmartLayout.CreateTreePiece("<html>Application<br/>Number</html>", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compETextField = SmartLayout.CreateTreePiece("Enter Application Number", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable applicationNumberField = SmartLayout.CreateTreePiece("Application Number Field", compELabel, compETextField);
		
		Layoutable rightUpperGroup = SmartLayout.CreateTreePiece("Right Upper Group", noticeLabel, dateOfApplicationField, applicationNumberField);
		
		Layoutable compFLabel = SmartLayout.CreateTreePiece("<html>Date of Birth<br/>(dd-mm-yyyy)</html>", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compFTextField = SmartLayout.CreateTreePiece("Enter Birth Date", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable birthDateField = SmartLayout.CreateTreePiece("Birth Date Field", compFLabel, compFTextField);
		
		Layoutable compGLabel = SmartLayout.CreateTreePiece("Place of Birth", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compGTextField = SmartLayout.CreateTreePiece("Enter Place of Birth", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable placeOfBirthField = SmartLayout.CreateTreePiece("Place of Birth Field", compGLabel, compGTextField);
		
		Layoutable compHLabel = SmartLayout.CreateTreePiece("Country of Birth", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable compHTextField = SmartLayout.CreateTreePiece("Enter Country of Birth", new JTextField(), ComponentDimensionEnum.SMALL_SMALLER);
		Layoutable countryOfBirthField = SmartLayout.CreateTreePiece("Country of Birth Field", compHLabel, compHTextField);
		
		Layoutable birthPlaceGroup = SmartLayout.CreateTreePiece("Birth Place Group", placeOfBirthField, countryOfBirthField);
		
		Layoutable compILabel = SmartLayout.CreateTreePiece("Current Nationality: ", new JLabel(), ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable compITextField = SmartLayout.CreateTreePiece("Enter Current Nationality", new JTextField(), ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable nationalityField = SmartLayout.CreateTreePiece("Current Nationality Field", compILabel, compITextField);
		
		Layoutable compJLabel = SmartLayout.CreateTreePiece("Nationality at Birth: ", new JLabel(), ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable compJTextField = SmartLayout.CreateTreePiece("Enter Nationality at Birth", new JTextField(), ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable nationalityAtBirthField = SmartLayout.CreateTreePiece("Nationality at birth, if different", compJLabel, compJTextField);
		
		Layoutable compKLabel = SmartLayout.CreateTreePiece("Other Nationalities: ", new JLabel(), ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable compKTextField = SmartLayout.CreateTreePiece("Enter Other nationalities", new JTextField(), ComponentDimensionEnum.MEDIUM_SMALLER);
		Layoutable otherNationalitiesField = SmartLayout.CreateTreePiece("Other Nationalities Field", compKLabel, compKTextField);
		
		Layoutable nationalityGroup = SmartLayout.CreateTreePiece("Nationality Group", nationalityField, nationalityAtBirthField, otherNationalitiesField);
		Layoutable leftUpperMiddleGroup = SmartLayout.CreateTreePiece("Left Upper Middle Group", birthDateField, birthPlaceGroup, nationalityGroup);
		
		Layoutable genderLabel = SmartLayout.CreateTreePiece("Sex: ", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		
		ButtonGroup genderButtonGroup = new ButtonGroup();
		
		JRadioButton radioButtonA = new JRadioButton();
		JRadioButton radioButtonB = new JRadioButton();
		
		genderButtonGroup.add(radioButtonA);
		genderButtonGroup.add(radioButtonB);
		
		Layoutable compLLabel = SmartLayout.CreateTreePiece("Male", new JLabel(), ComponentDimensionEnum.SMALLER_TINY);
		Layoutable compLRadioButton = SmartLayout.CreateTreePiece("", radioButtonA, ComponentDimensionEnum.TINY_TINY);
		Layoutable genderMaleCheckbox = SmartLayout.CreateTreePiece("Gender Male", compLLabel, compLRadioButton);
		
		Layoutable compMLabel = SmartLayout.CreateTreePiece("Female", new JLabel(), ComponentDimensionEnum.SMALLER_TINY);
		Layoutable compMRadioButton = SmartLayout.CreateTreePiece("", radioButtonB, ComponentDimensionEnum.TINY_TINY);
		Layoutable genderFemaleCheckbox = SmartLayout.CreateTreePiece("Gender Female", compMLabel, compMRadioButton);
		
		Layoutable genderChoicesGroup = SmartLayout.CreateTreePiece("Gender Choices Group", genderMaleCheckbox, genderFemaleCheckbox);
		Layoutable genderGroup = SmartLayout.CreateTreePiece("Gender Group", genderLabel, genderChoicesGroup);
		
		Layoutable civilStatusLabel = SmartLayout.CreateTreePiece("Civil Status: ", new JLabel(), ComponentDimensionEnum.SMALL_SMALLER);
		
		ButtonGroup civilStatusButtonGroup = new ButtonGroup();

		JRadioButton radioButtonC = new JRadioButton();
		JRadioButton radioButtonD = new JRadioButton();
		JRadioButton radioButtonE = new JRadioButton();
		JRadioButton radioButtonF = new JRadioButton();

		civilStatusButtonGroup.add(radioButtonC);
		civilStatusButtonGroup.add(radioButtonD);
		civilStatusButtonGroup.add(radioButtonE);
		civilStatusButtonGroup.add(radioButtonF);
		
		Layoutable compNLabel = SmartLayout.CreateTreePiece("Single", new JLabel(), ComponentDimensionEnum.SMALLER_TINY);
		Layoutable compNRadioButton = SmartLayout.CreateTreePiece("", radioButtonC, ComponentDimensionEnum.TINY_TINY);
		Layoutable singleRadioButton = SmartLayout.CreateTreePiece("Single", compNLabel, compNRadioButton);
		
		Layoutable compOLabel = SmartLayout.CreateTreePiece("Married", new JLabel(), ComponentDimensionEnum.SMALLER_TINY);
		Layoutable compORadioButton = SmartLayout.CreateTreePiece("", radioButtonD, ComponentDimensionEnum.TINY_TINY);
		Layoutable marriedRadioButton = SmartLayout.CreateTreePiece("Married", compOLabel, compORadioButton);
		
		Layoutable compPLabel = SmartLayout.CreateTreePiece("Divorced", new JLabel(), ComponentDimensionEnum.SMALLER_TINY);
		Layoutable compPRadioButton = SmartLayout.CreateTreePiece("", radioButtonE, ComponentDimensionEnum.TINY_TINY);
		Layoutable divorcedRadioButton = SmartLayout.CreateTreePiece("Divorced", compPLabel, compPRadioButton);
		
		Layoutable compRLabel = SmartLayout.CreateTreePiece("Seperated", new JLabel(), ComponentDimensionEnum.SMALLER_TINY);
		Layoutable compRRadioButton = SmartLayout.CreateTreePiece("", radioButtonF, ComponentDimensionEnum.TINY_TINY);
		Layoutable seperatedRadioButton = SmartLayout.CreateTreePiece("Seperated", compRLabel, compRRadioButton);
		
		Layoutable civilStatusChoicesGroup = SmartLayout.CreateTreePiece("Civil Status Choices Group", singleRadioButton,
				marriedRadioButton, divorcedRadioButton, seperatedRadioButton);
		Layoutable civilStatusGroup = SmartLayout.CreateTreePiece("Civil Status Group", civilStatusLabel, civilStatusChoicesGroup);

		Layoutable lodgedLabel = SmartLayout.CreateTreePiece("Application lodged at: ", new JLabel(), ComponentDimensionEnum.MEDIUM_TINY);
		
		ButtonGroup lodgePlaceButtonGroup = new ButtonGroup();

		JRadioButton radioButtonG = new JRadioButton();
		JRadioButton radioButtonH = new JRadioButton();
		JRadioButton radioButtonI = new JRadioButton();
		JRadioButton radioButtonJ = new JRadioButton();
		JRadioButton radioButtonK = new JRadioButton();

		lodgePlaceButtonGroup.add(radioButtonG);
		lodgePlaceButtonGroup.add(radioButtonH);
		lodgePlaceButtonGroup.add(radioButtonI);
		lodgePlaceButtonGroup.add(radioButtonJ);
		lodgePlaceButtonGroup.add(radioButtonK);
		
		Layoutable compSLabel = SmartLayout.CreateTreePiece("Embassy", new JLabel(), ComponentDimensionEnum.MEDIUM_TINY);
		Layoutable compSRadioButton = SmartLayout.CreateTreePiece("", radioButtonG, ComponentDimensionEnum.TINY_TINY);
		Layoutable embassyRadioButton = SmartLayout.CreateTreePiece("Embassy", compSLabel, compSRadioButton);
		
		Layoutable compTLabel = SmartLayout.CreateTreePiece("Service Provider", new JLabel(), ComponentDimensionEnum.MEDIUM_TINY);
		Layoutable compTRadioButton = SmartLayout.CreateTreePiece("", radioButtonH, ComponentDimensionEnum.TINY_TINY);
		Layoutable serviceProviderRadioButton = SmartLayout.CreateTreePiece("Service Provider", compTLabel, compTRadioButton);
		
		Layoutable compULabel = SmartLayout.CreateTreePiece("Commercial Intermediary", new JLabel(), ComponentDimensionEnum.MEDIUM_TINY);
		Layoutable compURadioButton = SmartLayout.CreateTreePiece("", radioButtonI, ComponentDimensionEnum.TINY_TINY);
		Layoutable commercialIntermediaryRadioButton = SmartLayout.CreateTreePiece("Commercial Intermediary", compULabel, compURadioButton);
		
		Layoutable compVLabel = SmartLayout.CreateTreePiece("Border", new JLabel(), ComponentDimensionEnum.MEDIUM_TINY);
		Layoutable compVRadioButton = SmartLayout.CreateTreePiece("", radioButtonJ, ComponentDimensionEnum.TINY_TINY);
		Layoutable borderRadioButton = SmartLayout.CreateTreePiece("Border", compVLabel, compVRadioButton);
		
		Layoutable compYLabel = SmartLayout.CreateTreePiece("Other", new JLabel(), ComponentDimensionEnum.MEDIUM_TINY);
		Layoutable compYRadioButton = SmartLayout.CreateTreePiece("", radioButtonK, ComponentDimensionEnum.TINY_TINY);
		Layoutable otherRadioButton = SmartLayout.CreateTreePiece("Other", compYLabel, compYRadioButton);
		
		Layoutable lodgePlaceChoicesGroup = SmartLayout.CreateTreePiece("Lodge Place Choices Group", embassyRadioButton,
				serviceProviderRadioButton, commercialIntermediaryRadioButton, borderRadioButton, otherRadioButton);

		Layoutable lodgedAtGroup = SmartLayout.CreateTreePiece("Lodged At Group", lodgedLabel, lodgePlaceChoicesGroup);

		Layoutable firstLine = SmartLayout.CreateTreePiece("First Line", leftUpperGroup, leftUpperMiddleGroup, rightUpperGroup);
		Layoutable secondLine = SmartLayout.CreateTreePiece("Second Line", genderGroup, civilStatusGroup, lodgedAtGroup);

		Layoutable testScenario = SmartLayout.CreateTreePiece("ROOT", firstLine, secondLine);
		
		return testScenario;
	}
}
