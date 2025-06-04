package graph;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI {
	
	private JFrame frame;
	
	private JPanel inputPanel;
	private JPanel graphFieldPanel;
	
	private textInputField uploadTxtField;
	private textInputField uploadBinField;
	
	private textInputField partitionsInputField;
	private textInputField marginInputField;
	
	private JButton loadGraphButtonTxt = new JButton("Wczytaj graf (plik tekstowy)");
	private JButton loadGraphButtonBin = new JButton("Wczytaj graf (plik binarny)");
	
	private JPanel loadButtonPanel = new JPanel();
	
	public void setTxtLoadButtonListener (ActionListener listener) {
		loadGraphButtonTxt.addActionListener(listener);
	}
	
	public void setBinLoadButtonListener (ActionListener listener) {
		loadGraphButtonBin.addActionListener(listener);
	}
	
	public textInputField getUploadTxtField() {
		return uploadTxtField;
	}
	
	public textInputField getUploadBinField() {
		return uploadBinField;
	}
	
	public textInputField getPartitionsInputField() {
		return partitionsInputField;
	}
	
	public textInputField getMarginInputField() {
		return marginInputField;
	}
	
	public void setGraphFieldPanel (JPanel panel) {
		frame.remove(graphFieldPanel);
		graphFieldPanel = panel;
        frame.getContentPane().add(graphFieldPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
	}
	
	public GUI () {
		
        if (frame == null) {
            frame = new JFrame("Graph Partitioning");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(750, 900);
            frame.setLayout(new BorderLayout(20, 20));
        } else {
            frame.getContentPane().removeAll();
            frame.revalidate();
            frame.repaint();
        }
        
        frame.setLocationRelativeTo(null);
        
        inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        graphFieldPanel = new JPanel();
        
        uploadTxtField = new textInputField("Wczytaj plik tekstowy, obecnie:", "example.txt");
        uploadBinField = new textInputField("Wczytaj plik binarny, obecnie:", "example.bin");
        
        partitionsInputField = new textInputField("Liczba części, obecnie:", "2");
        marginInputField = new textInputField("Margines procentowy, obecnie:", "0.1");

        inputPanel.add(uploadTxtField);
        inputPanel.add(uploadBinField);
        
        inputPanel.add(partitionsInputField);
        inputPanel.add(marginInputField);
        
        loadGraphButtonTxt.setFocusable(false);
        loadGraphButtonBin.setFocusable(false);
        loadButtonPanel.add(loadGraphButtonTxt);
        loadButtonPanel.add(loadGraphButtonBin);
                
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(loadButtonPanel, BorderLayout.SOUTH);
        
    }
	
	public void show() {
		this.frame.setVisible(true);
	}
	
}
