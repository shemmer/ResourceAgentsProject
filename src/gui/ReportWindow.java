package gui;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;

public class ReportWindow extends javax.swing.JFrame {
	private javax.swing.JPanel mainPanel = null;
	private int i;
	
	public ReportWindow(){
		initialize();
		i=1;
	}
	public void addAgent(SimpleEntry<String, Double> pair){
		if(mainPanel==null){
			setVisible(true);
			setContentPane(getJFrameContentPane());
		}else{
			
			javax.swing.JLabel name = new javax.swing.JLabel();
			name.setName("Label"+pair.getKey());
			name.setText("Agent_Name: " + pair.getKey());
			javax.swing.JLabel profit = new javax.swing.JLabel();
			profit.setName("Profit"+pair.getKey());
			profit.setText("Profit: " + pair.getValue());
			
			java.awt.GridBagConstraints constraintsName = new java.awt.GridBagConstraints();
			constraintsName.gridx = 0; constraintsName.gridy = i;
			constraintsName.weightx = 1.0;
			constraintsName.insets = new java.awt.Insets(4, 4, 4, 4);
			mainPanel.add(name, constraintsName);

			java.awt.GridBagConstraints constraintsProfit = new java.awt.GridBagConstraints();
			constraintsProfit.gridx = 1; constraintsProfit.gridy = i;
			constraintsProfit.weightx = 1.0;
			constraintsProfit.insets = new java.awt.Insets(4, 4, 4, 4);
			mainPanel.add(profit, constraintsProfit);
			
			i++;
			mainPanel.revalidate();
			mainPanel.repaint();
		}
	}
	
	private javax.swing.JPanel getJFrameContentPane() {
		if (mainPanel == null) {
			mainPanel = new javax.swing.JPanel();
			mainPanel.setName("JFrameContentPane");
			mainPanel.setLayout(new java.awt.GridBagLayout());
			mainPanel.setDoubleBuffered(true);
			mainPanel.setVisible(true);			
		}
		return mainPanel;
	}
	private void initialize() {
		setName("Stats");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setFont(new java.awt.Font("dialog", 0, 10));
		setSize(300, 200);
		setTitle("Stats");
	}
}
