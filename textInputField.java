package graph;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class textInputField extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1L;
		
	private JTextField textField;
    private JButton submitButton;
    private JLabel inputLabel;
    
    private String labelDefaultContents;
    private String currentInput;
    private String defaultInput;
        
    public JButton generateSubmitButton (String buttonLabel) {
    		
    	JButton submitButton = new JButton (buttonLabel);  	
    	submitButton.setFocusable(false);
    	submitButton.addActionListener(this);
    	
    	return submitButton;
    }
    
    public textInputField (String textFieldLabel, String newDefaultInput) {
    	currentInput = newDefaultInput;
    	defaultInput = newDefaultInput;
    	textField = new JTextField(20);
    	submitButton = generateSubmitButton("Wczytaj");
    	inputLabel = new JLabel(textFieldLabel + " " + currentInput);
    	
    	labelDefaultContents = textFieldLabel;
    	
    	this.setPreferredSize(new Dimension(250, 80));
    	
    	this.add(textField);
    	this.add(submitButton);
    	this.add(inputLabel);
    }

	public void setCurrentInput (String newInput) {
		currentInput = newInput;
	}
    
	@Override
	public void actionPerformed(ActionEvent e) {
		String label = textField.getText();
		if (label.isEmpty()) {
			inputLabel.setText(labelDefaultContents + " " + defaultInput);
		} else {
			inputLabel.setText(labelDefaultContents + " " + label);
		}
		currentInput = label;
		textField.setText("");
		
	}
	
	public String getCurrentInput () {
		return currentInput;
	}

}
