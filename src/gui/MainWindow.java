package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import agent.serviceAgent.ServiceAggregatorAgent;
import jade.core.Agent;
import offer.OfferFactory;
import offer.Resource;

/**
 * Die Beschreibung des Typs hier eingeben.
 * Erstellungsdatum: (08.12.2002 14:29:39)
 * @author: 
 */
public class MainWindow extends javax.swing.JFrame implements java.awt.event.KeyListener {
	
	private javax.swing.JPanel mainPanel = null;
	private OfferCanvas offerCanvas = null;
	private OfferFactory offer = null;
	public void setOffer(OfferFactory offer) {
		this.offer = offer;
	}
	private CapacityCanvas capacityCanvas = null;
	private AgentCapacityCanvas agentCanvas = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JButton rejectButton = null;
	private javax.swing.JButton acceptButton = null;
	private javax.swing.JButton clearButton = null;
	private javax.swing.JButton startButton = null;
	private javax.swing.JButton startAutomatedButton = null;
	private javax.swing.JLabel pricePerResLabel = null;
	private javax.swing.JLabel priceOrderLabel = null;
	private javax.swing.JLabel turnoverLabel = null;
	private javax.swing.JTextField pricePerResTextField = null;
	private javax.swing.JTextField priceOrderTextField = null;
	private javax.swing.JTextField turnOverTextField = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTextArea logTextArea = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JLabel stepLabel = null;
	private javax.swing.JTextField stepTextField = null;
	private javax.swing.JTabbedPane controlLogTabbedPane = null;
	private javax.swing.JCheckBox limitStepsCheckBox = null;

	private javax.swing.JLabel aggCostLabel = null;
	private javax.swing.JTextField aggCostField = null;
	private javax.swing.JFrame reportWindow= null;

	private javax.swing.JLabel incomeLbl= null;
	private javax.swing.JTextField incomeTxtField = null;

	private javax.swing.JLabel incomeTotalLbl= null;
	private javax.swing.JTextField incomeTotalTxtField = null;
	
	private javax.swing.JSlider profitSlider = null;
	
	private ServiceAggregatorAgent serviceAggregator;

	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.awt.event.WindowListener, Serializable {

		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == MainWindow.this.getAcceptButton()){ 
				serviceAggregator.addBehaviour(serviceAggregator.new AcceptBehaviour());
			}
			if (e.getSource() == MainWindow.this.getRejectButton()) {
				serviceAggregator.addBehaviour(serviceAggregator.new RejectBehaviour(serviceAggregator));
			}
			if (e.getSource() == MainWindow.this.getClearResButton()){
				toggleStartAutomaticButton(true);
				toggleStartManualButton(true);
				serviceAggregator.addBehaviour(serviceAggregator.new RestartBehaviour());
			}
			if(e.getSource() == MainWindow.this.getStartButton()){
				offer.newGame();
				serviceAggregator.setMinimumProfit(profitSlider.getValue());
				serviceAggregator.unautomate();
				toggleStartAutomaticButton(false);
				toggleStartManualButton(false);
				serviceAggregator.addBehaviour(serviceAggregator.new StartBehaviour(serviceAggregator));
			}
			if(e.getSource() == MainWindow.this.getAutomatedStartButton()){
				offer.newGame();
				toggleStartAutomaticButton(false);
				toggleStartManualButton(false);
				serviceAggregator.setMinimumProfit(profitSlider.getValue());
				serviceAggregator.automate();
				serviceAggregator.addBehaviour(serviceAggregator.new StartBehaviour(serviceAggregator));		
			}
			
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == MainWindow.this.getLimitCheckBox()) 
				setLimitToOffer(e);
		};
		public void windowActivated(java.awt.event.WindowEvent e) {};
		public void windowClosed(java.awt.event.WindowEvent e) {
			if (e.getSource() == MainWindow.this) 
				closeApplication(e);
		};
		public void windowClosing(java.awt.event.WindowEvent e) {};
		public void windowDeactivated(java.awt.event.WindowEvent e) {};
		public void windowDeiconified(java.awt.event.WindowEvent e) {};
		public void windowIconified(java.awt.event.WindowEvent e) {};
		public void windowOpened(java.awt.event.WindowEvent e) {};
	};
	/**
	 * MyDemonstrator - Konstruktorkommentar.
	 */
	public MainWindow() {
		super();
		initialize();
	}

	/**
	 * Main Window Constructor; Also initializes the window and sets serviceAggregator agent variable -> (1:1 MainWindow : ServiceAggregator)
	 * @param agent Service Aggregator agent invoking the creation of MainWindow
	 */
	public MainWindow(Agent a) {
		super();
		this.serviceAggregator= (ServiceAggregatorAgent) a;
		initialize();
	}

	

	/**
	 * connEtoM10:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> JTextField2.text)
	 */
	public void updatePriceOrderTextField() {
		try {
			getPriceOrderTextField().setText(String.valueOf(getOffer().getActiveObjectIncome()));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void updatePriceOrderTextField(String txt) {
		try {
			getPriceOrderTextField().setText(txt);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * connEtoM11:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> JTextField3.text)
	 */
	public void updateIncomeTotalTextField() {
		try {
			getIncomeTotalTextField().setText(String.valueOf(getOffer().getIncomeTotal()));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * connEtoM11:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> JTextField3.text)
	 */
	public void updateTurnOverTextField() {
		try {
			getTurnOverTextField().setText(String.valueOf(getOffer().getTurnOver()));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void updateIncomeTextField(double currentOfferProfit){
		try {
			getIncomeTextField().setText(String.valueOf(currentOfferProfit));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	private JTextComponent getIncomeTextField() {
		if (incomeTxtField == null) {
			try {
				incomeTxtField= new javax.swing.JTextField();
				incomeTxtField.setName("JTextField3");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return incomeTxtField;
	}
	private JLabel getIncomeLabel() {
		if (incomeLbl == null) {
			try {
				incomeLbl= new javax.swing.JLabel();
				incomeLbl.setName("incomeLabel");
				incomeLbl.setText("Income");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return incomeLbl;
	}
	
	private JTextComponent getIncomeTotalTextField() {
		if (incomeTotalTxtField == null) {
			try {
				incomeTotalTxtField= new javax.swing.JTextField();
				incomeTotalTxtField.setName("JTextField3");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return incomeTotalTxtField;
	}
	private JLabel getIncomeTotalLabel() {
		if (incomeTotalLbl == null) {
			try {
				incomeTotalLbl= new javax.swing.JLabel();
				incomeTotalLbl.setName("incomeLabel");
				incomeTotalLbl.setText("Income(Total)");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return incomeTotalLbl;
	}
	
	public void updateTurnOverTextField(String txt) {
		try {
			getTurnOverTextField().setText(txt);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	
	/**
	 * connEtoM12:  (JButton4.action.actionPerformed(java.awt.event.ActionEvent) --> JTextArea1.append(Ljava.lang.String;)V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	public void updateLogTextArea(String text) {
		try {
			initLogTextArea().append( text+ "\n");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * Update Log Text Area with the information required with a accepted offer
	 * @param arg1 java.awt.event.ActionEvent
	 */
	public void updateLogTextAreaAccept() {
			initLogTextArea().append("--------------------- ACCEPTED OFFER ---------------------\n\n\n");
		
	}
	
	private void initLogTextArea(java.awt.event.ActionEvent arg1) {
		try {
			initLogTextArea().setText("nr;income;priceTag; id;accepted\n");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	
	public void toggleStartManualButton(boolean result) {
		try {
			this.getStartButton().setEnabled(result);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void toggleStartAutomaticButton(boolean result) {
		try {
			this.getAutomatedStartButton().setEnabled(result);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	
	public void toggleAcceptButton(boolean result) {
		try {
			this.getAcceptButton().setEnabled(result);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}public void toggleRejectButton(boolean result) {
		try {
			this.getRejectButton().setEnabled(result);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	public void greenAcceptButton(){
		acceptButton.setBackground(Color.GREEN);
	}
	public void redAcceptButton(){
		acceptButton.setBackground(Color.RED);
	}
	
	private void closeApplication(java.awt.event.WindowEvent arg1) {
		try {
			getOffer().close();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}


	
	public void updateStepTextField() {
		try {
			getStepTextField().setText(Integer.toString(getOffer().getStep()+1));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	private void setLimitToOffer(java.awt.event.ItemEvent arg1) {
		try {
			getOffer().setLimitSteps(getLimitCheckBox().isSelected());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	public void repaintCapacityCanvas() {
		try {
			getCapacityCanvas().repaint();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	
	public void resetAgentCanvas(){
		try {
			getAgentCanvas().reset();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void repaintAgentCanvas() {
		try {
			getAgentCanvas().repaint();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	
	public void printAgentCanvas(){
		getAgentCanvas().print();
	}
	public void addAgentToCanvas(String agent, Resource r, int totalCapacity){
		getAgentCanvas().addAgent(agent, r, totalCapacity);
	}
	public void updateAgentCanvas(String agent, Resource r, int newUsedCapacity, byte color){
		getAgentCanvas().addAgentResource(agent, r, newUsedCapacity, color);
	}
	
	public void repaintOfferCanvas() {
			getOfferCanvas().repaint();	
	}
	/**
	 * Updating the log text area with information on a reject object
	 * @param 
	 */
	public void updateLogTextAreaReject() {
		initLogTextArea().append("--------------------- REJECTED OFFER ---------------------\n\n\n");
	}

	
	/**
	 * connEtoM9:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> JTextField1.text)
	 */
	public void updatePricePerResTextField() {
		try {
			getPricePerResTextField().setText(getOffer().getFormatedActiveObjectPriceTag());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * Set value of Aggregated Cost
	 * @param arg1
	 */
	public void updateAggCost(double aggCost) {
		try {
			this.getAggCostField().setText(Double.toString(aggCost));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}/**
	 * Set value of Aggregated Cost
	 * @param arg1
	 */
	public void updateAggCost() {
		try {
			this.getAggCostField().setText(Double.toString(offer.getAggCost()));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * Set value of Aggregated Cost
	 * @param arg1
	 */
	public void updateAggCost(String text) {
		try {
			this.getAggCostField().setText(text);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * connPtoP1SetTarget:  (mySpace.this <--> myObjectCanvas.mySpace)
	 */
	public void setOfferInOfferCanvas(OfferFactory offer) {
		try {
			getOfferCanvas().setOffer(offer);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * connPtoP2SetTarget:  (mySpace.this <--> mySpaceCanvas.mySpace)
	 * this.getOffer -> setOffer in CapacityCanvas also repaints the canvas
	 */
	public void registerOfferInCapacityCanvas(OfferFactory offer) {
		try {
			getCapacityCanvas().setOffer(offer);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	private javax.swing.JButton getRejectButton() {
		if (rejectButton == null) {
			try {
				rejectButton = new javax.swing.JButton();
				rejectButton.setName("JButton3");
				rejectButton.setText("Reject");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return rejectButton;
	}
	
	
	private javax.swing.JSlider getMinimumProfitSlider() {
		if (profitSlider== null) {
			try {
				profitSlider= new javax.swing.JSlider(JSlider.HORIZONTAL);
				profitSlider.setMinimum(1);
				profitSlider.setMaximum(30);
				Hashtable labelTable= new Hashtable();
				labelTable.put(1, new JLabel("1"));
				labelTable.put(10, new JLabel("10"));
				labelTable.put(30, new JLabel("30"));
				profitSlider.setValue(10);
				profitSlider.setLabelTable(labelTable);
				profitSlider.setPaintLabels(true);
				profitSlider.setName("profitSlider");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return profitSlider;
	}
	
	private javax.swing.JButton getAcceptButton() {
		if (acceptButton == null) {
			try {
				acceptButton = new javax.swing.JButton();
				acceptButton.setName("JButton4");
				acceptButton.setText("Accept");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return acceptButton;
	}
	private javax.swing.JButton getClearResButton() {
		if (clearButton == null) {
			try {
				clearButton = new javax.swing.JButton();
				clearButton.setName("JButton5");
				clearButton.setText("Clear Resource");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return clearButton;
	}
	private javax.swing.JButton getStartButton() {
		if (startButton == null) {
			try {
				startButton = new javax.swing.JButton();
				startButton.setName("JButton5");
				startButton.setText("Start (manual)");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return startButton;
	}
	private javax.swing.JButton getAutomatedStartButton() {
		if (startAutomatedButton == null) {
			try {
				startAutomatedButton = new javax.swing.JButton();
				startAutomatedButton.setName("JButton5");
				startAutomatedButton.setText("Start (automatic)");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return startAutomatedButton;
	}
	private javax.swing.JCheckBox getLimitCheckBox() {
		if (limitStepsCheckBox == null) {
			try {
				limitStepsCheckBox = new javax.swing.JCheckBox();
				limitStepsCheckBox.setName("JCheckBox1");
				limitStepsCheckBox.setText("Limit Steps");
				limitStepsCheckBox.setForeground(new java.awt.Color(102,102,153));
				limitStepsCheckBox.setSelected(true);
				limitStepsCheckBox.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
				limitStepsCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return limitStepsCheckBox;
	}
	/**
	 * Den Eigenschaftswert JFrameContentPane zurückgeben.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJFrameContentPane() {
		if (mainPanel == null) {
			try {
				mainPanel = new javax.swing.JPanel();
				mainPanel.setName("JFrameContentPane");
				mainPanel.setLayout(new java.awt.GridBagLayout());
				mainPanel.setDoubleBuffered(true);
				mainPanel.setVisible(true);

				java.awt.GridBagConstraints constraintsOfferCanvas = new java.awt.GridBagConstraints();
				constraintsOfferCanvas.gridx = 0; constraintsOfferCanvas.gridy = 0;
				constraintsOfferCanvas.fill = java.awt.GridBagConstraints.BOTH;
				constraintsOfferCanvas.weightx = 1.0;
				constraintsOfferCanvas.weighty = 1.0;
				constraintsOfferCanvas.ipadx = 237;
				constraintsOfferCanvas.ipady = 187;
				constraintsOfferCanvas.insets = new java.awt.Insets(0, 0, 2, 2);
				getJFrameContentPane().add(getOfferCanvas(), constraintsOfferCanvas);

				java.awt.GridBagConstraints constraintsCapacityCanvas = new java.awt.GridBagConstraints();
				constraintsCapacityCanvas.gridx = 0; constraintsCapacityCanvas.gridy = 1;
				constraintsCapacityCanvas.fill = java.awt.GridBagConstraints.BOTH;
				constraintsCapacityCanvas.weightx = 1.0;
				constraintsCapacityCanvas.weighty = 2.0;
				constraintsCapacityCanvas.ipadx = 237;
				constraintsCapacityCanvas.ipady = 187;
				constraintsCapacityCanvas.insets = new java.awt.Insets(3, 0, 1, 2);
				getJFrameContentPane().add(getAgentCanvas(), constraintsCapacityCanvas);

				java.awt.GridBagConstraints constraintsJTabbedPane1 = new java.awt.GridBagConstraints();
				constraintsJTabbedPane1.gridx = 1; constraintsJTabbedPane1.gridy = 0;
				constraintsJTabbedPane1.gridheight = 2;
				constraintsJTabbedPane1.fill = java.awt.GridBagConstraints.BOTH;
				constraintsJTabbedPane1.weightx = 1.0;
				constraintsJTabbedPane1.weighty = 1.0;
				constraintsJTabbedPane1.insets = new java.awt.Insets(4, 4, 4, 4);
				getJFrameContentPane().add(getJTabbedPane1(), constraintsJTabbedPane1);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return mainPanel;
	}
	
	public void showReportWindow(){
		((ReportWindow) reportWindow).show();
	}
	
	
	private javax.swing.JLabel getPricePerResLabel() {
		if (pricePerResLabel == null) {
			try {
				pricePerResLabel = new javax.swing.JLabel();
				pricePerResLabel.setName("JLabel1");
				pricePerResLabel.setText("Price per ResUnit");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return pricePerResLabel;
	}
	private javax.swing.JLabel getPriceOrderLabel() {
		if (priceOrderLabel == null) {
			try {
				priceOrderLabel = new javax.swing.JLabel();
				priceOrderLabel.setName("JLabel2");
				priceOrderLabel.setText("Price for this Order");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return priceOrderLabel;
	}
	private javax.swing.JLabel getTurnOverLabel() {
		if (turnoverLabel == null) {
			try {
				turnoverLabel = new javax.swing.JLabel();
				turnoverLabel.setName("JLabel3");
				turnoverLabel.setText("Turnover");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return turnoverLabel;
	}
	private javax.swing.JLabel getStepLabel() {
		if (stepLabel == null) {
			try {
				stepLabel = new javax.swing.JLabel();
				stepLabel.setName("JLabel4");
				stepLabel.setText("Step");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return stepLabel;
	}
	private javax.swing.JLabel getAggCostLabel() {
		if (this.aggCostLabel == null) {
			try {
				this.aggCostLabel = new javax.swing.JLabel();
				this.aggCostLabel.setName("aggCostLabel");
				this.aggCostLabel.setText("Aggregated Costs");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return this.aggCostLabel;
	}

	private JTextField getAggCostField() {
		if (this.aggCostField == null) {
			try {
				this.aggCostField = new javax.swing.JTextField();
				this.aggCostField.setName("aggCostField");
				this.aggCostField.setText("0");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return aggCostField;
	}
	/**
	 * Den Eigenschaftswert JPanel1 zurückgeben.
	 * 
	 * JPanel1 Right Side
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanel1() {
		if (ivjJPanel1 == null) {
			try {
				ivjJPanel1 = new javax.swing.JPanel();
				ivjJPanel1.setName("JPanel1");
				ivjJPanel1.setLayout(new java.awt.GridBagLayout());
				
				
				/**
				 * Price Per Res Unit
				 */
				java.awt.GridBagConstraints constraintsJTextField1 = new java.awt.GridBagConstraints();
				constraintsJTextField1.gridx = 1; constraintsJTextField1.gridy = 0;
				constraintsJTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJTextField1.weightx = 1.0;
				constraintsJTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getPricePerResTextField(), constraintsJTextField1);

				java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
				constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
				constraintsJLabel1.weightx = 1.0;
				constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getPricePerResLabel(), constraintsJLabel1);
				
				/**
				 * Price for this order
				 */

				java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
				constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 1;
				constraintsJLabel2.weightx = 1.0;
				constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getPriceOrderLabel(), constraintsJLabel2);


				java.awt.GridBagConstraints constraintsJTextField2 = new java.awt.GridBagConstraints();
				constraintsJTextField2.gridx = 1; constraintsJTextField2.gridy = 1;
				constraintsJTextField2.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJTextField2.weightx = 1.0;
				constraintsJTextField2.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getPriceOrderTextField(), constraintsJTextField2);
				
				/**
				 *  Minimum Income for an order
				 */
				java.awt.GridBagConstraints constraintsSlider= new java.awt.GridBagConstraints();
				constraintsSlider.gridx = 0; constraintsSlider.gridy = 2;
				constraintsSlider.gridwidth = 2;
				constraintsSlider.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsSlider.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(this.getMinimumProfitSlider(), constraintsSlider);
				
				
				this.getMinimumProfitSlider().setBorder(
						BorderFactory.createTitledBorder("Minimum Income per Order"));
			
				
				/**
				 * Aggregated Costs for this order
				 */
				java.awt.GridBagConstraints constraintsAggCost = new java.awt.GridBagConstraints();
				constraintsAggCost.gridx = 0; constraintsAggCost.gridy = 3;
				constraintsAggCost.weightx = 1.0;
				constraintsAggCost.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(this.getAggCostLabel(), constraintsAggCost);

				java.awt.GridBagConstraints constraintsAggField= new java.awt.GridBagConstraints();
				constraintsAggField.gridx = 1; constraintsAggField.gridy = 3;
				constraintsAggField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsAggField.weightx = 1.0;
				constraintsAggField.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(this.getAggCostField(), constraintsAggField);

				
				
				
				/**
				 * Income for this order
				 */
				java.awt.GridBagConstraints constraintsIncomeOrderLbl = new java.awt.GridBagConstraints();
				constraintsIncomeOrderLbl.gridx = 0; constraintsIncomeOrderLbl.gridy =4;
				constraintsIncomeOrderLbl.weightx = 1.0;
				constraintsIncomeOrderLbl.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getIncomeLabel(), constraintsIncomeOrderLbl);

				java.awt.GridBagConstraints constraintsIncomeOrderTxtField = new java.awt.GridBagConstraints();
				constraintsIncomeOrderTxtField.gridx = 1; constraintsIncomeOrderTxtField.gridy = 4;
				constraintsIncomeOrderTxtField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsIncomeOrderTxtField.weightx = 1.0;
				constraintsIncomeOrderTxtField.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getIncomeTextField(), constraintsIncomeOrderTxtField);
				

				/**
				 * Steps/Limited
				 */

				java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
				constraintsJLabel4.gridx = 0; constraintsJLabel4.gridy = 5;
				constraintsJLabel4.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getStepLabel(), constraintsJLabel4);
				
				java.awt.GridBagConstraints constraintsJTextField4 = new java.awt.GridBagConstraints();
				constraintsJTextField4.gridx = 1; constraintsJTextField4.gridy = 5;
				constraintsJTextField4.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJTextField4.weightx = 1.0;
				constraintsJTextField4.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getStepTextField(), constraintsJTextField4);

				java.awt.GridBagConstraints constraintsJCheckBox1 = new java.awt.GridBagConstraints();
				constraintsJCheckBox1.gridx = 0; constraintsJCheckBox1.gridy = 6;
				constraintsJCheckBox1.gridwidth = 2;
				constraintsJCheckBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJCheckBox1.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getLimitCheckBox(), constraintsJCheckBox1);
				
				/**
				 * Start Buttons
				 */
				java.awt.GridBagConstraints constraintsStartButton = new java.awt.GridBagConstraints();
				constraintsStartButton.gridx = 0; constraintsStartButton.gridy = 7;
				constraintsStartButton.weightx = 2;
				constraintsStartButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsStartButton.insets = new java.awt.Insets(4, 8, 4, 8);
				getJPanel1().add(getStartButton(), constraintsStartButton );

				java.awt.GridBagConstraints constraintsAutomatedStartButton = new java.awt.GridBagConstraints();
				constraintsAutomatedStartButton .gridx = 1; constraintsAutomatedStartButton .gridy = 7;
				constraintsAutomatedStartButton .weightx= 2;
				constraintsAutomatedStartButton .fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsAutomatedStartButton .insets = new java.awt.Insets(4, 8, 4, 8);
				getJPanel1().add(getAutomatedStartButton(), constraintsAutomatedStartButton );			
			
				
				/**
				 * Accept/Reject Buttons
				 */
				java.awt.GridBagConstraints constraintsJButton3 = new java.awt.GridBagConstraints();
				constraintsJButton3.gridx = 0; constraintsJButton3.gridy = 8;
				constraintsJButton3.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJButton3.weightx = 1.0;
				constraintsJButton3.insets = new java.awt.Insets(4, 8, 4, 8);
				getJPanel1().add(getRejectButton(), constraintsJButton3);

				java.awt.GridBagConstraints constraintsJButton4 = new java.awt.GridBagConstraints();
				constraintsJButton4.gridx = 1; constraintsJButton4.gridy = 8;
				constraintsJButton4.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJButton4.weightx = 1.0;
				constraintsJButton4.insets = new java.awt.Insets(4, 8, 4, 8);
				getJPanel1().add(getAcceptButton(), constraintsJButton4);

				
				/**
				 * Turnover
				 */
				java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
				constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 9;
				constraintsJLabel3.weightx = 1.0;
				constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getTurnOverLabel(), constraintsJLabel3);

				java.awt.GridBagConstraints constraintsJTextField3 = new java.awt.GridBagConstraints();
				constraintsJTextField3.gridx = 1; constraintsJTextField3.gridy = 9;
				constraintsJTextField3.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJTextField3.weightx = 1.0;
				constraintsJTextField3.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getTurnOverTextField(), constraintsJTextField3);

				
				/**
				 * Income
				 */
				java.awt.GridBagConstraints constraintsIncomeLbl = new java.awt.GridBagConstraints();
				constraintsIncomeLbl.gridx = 0; constraintsIncomeLbl.gridy =10;
				constraintsIncomeLbl.weightx = 1.0;
				constraintsIncomeLbl.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getIncomeTotalLabel(), constraintsIncomeLbl);

				java.awt.GridBagConstraints constraintsIncomeTxtField = new java.awt.GridBagConstraints();
				constraintsIncomeTxtField.gridx = 1; constraintsIncomeTxtField.gridy = 10;
				constraintsIncomeTxtField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsIncomeTxtField.weightx = 1.0;
				constraintsIncomeTxtField.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getIncomeTotalTextField(), constraintsIncomeTxtField);
				

				/**
				 * Reset
				 */
				java.awt.GridBagConstraints constraintsJButton5 = new java.awt.GridBagConstraints();
				constraintsJButton5.gridx = 0; constraintsJButton5.gridy = 11;
				constraintsJButton5.gridwidth = 2;
				constraintsJButton5.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJButton5.insets = new java.awt.Insets(4, 8, 4, 8);
				getJPanel1().add(getClearResButton(), constraintsJButton5);

				
				
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		
		return ivjJPanel1;
	}
	
	/**
	 * Den Eigenschaftswert JScrollPane1 zurückgeben.
	 * @return javax.swing.JScrollPane
	 */
	/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
	private javax.swing.JScrollPane getJScrollPane1() {
		if (ivjJScrollPane1 == null) {
			try {
				ivjJScrollPane1 = new javax.swing.JScrollPane();
				ivjJScrollPane1.setName("JScrollPane1");
				getJScrollPane1().setViewportView(initLogTextArea());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJScrollPane1;
	}
	/**
	 * Den Eigenschaftswert JTabbedPane1 zurückgeben.
	 * @return javax.swing.JTabbedPane
	 */
	private javax.swing.JTabbedPane getJTabbedPane1() {
		if (controlLogTabbedPane == null) {
			try {
				controlLogTabbedPane = new javax.swing.JTabbedPane();
				controlLogTabbedPane.setName("JTabbedPane1");
				controlLogTabbedPane.insertTab("Control", null, getJPanel1(), null, 0);
				controlLogTabbedPane.insertTab("Log", null, getJScrollPane1(), null, 1);

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return controlLogTabbedPane;
	}
	/**
	 * Den Eigenschaftswert JTextArea1 zurückgeben.
	 * @return javax.swing.JTextArea
	 */
	public javax.swing.JTextArea initLogTextArea() {
		if (logTextArea == null) {
			try {
				logTextArea = new javax.swing.JTextArea();
				logTextArea.setName("JTextArea1");
				logTextArea.setBounds(0, 0, 163, 197);
				logTextArea.setEditable(false);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return logTextArea;
	}
	private javax.swing.JTextField getPricePerResTextField() {
		if (pricePerResTextField == null) {
			try {
				pricePerResTextField = new javax.swing.JTextField();
				pricePerResTextField.setName("JTextField1");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return pricePerResTextField;
	}
	private javax.swing.JTextField getPriceOrderTextField() {
		if (priceOrderTextField == null) {
			try {
				priceOrderTextField = new javax.swing.JTextField();
				priceOrderTextField.setName("JTextField2");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return priceOrderTextField;
	}
	private javax.swing.JTextField getTurnOverTextField() {
		if (turnOverTextField == null) {
			try {
				turnOverTextField = new javax.swing.JTextField();
				turnOverTextField.setName("JTextField3");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return turnOverTextField;
	}
	private javax.swing.JTextField getStepTextField() {
		if (stepTextField == null) {
			try {
				stepTextField = new javax.swing.JTextField();
				stepTextField.setName("JTextField4");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return stepTextField;
	}
	private OfferCanvas getOfferCanvas() {
		if (offerCanvas == null) {
			try {
				offerCanvas = new gui.OfferCanvas();
				offerCanvas.setName("myObjectCanvas");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return offerCanvas;
	}
	private OfferFactory getOffer() {
		if (offer == null) {
			try {
				offer = new offer.OfferFactory();
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return offer;
	}
	private CapacityCanvas getCapacityCanvas() {
		if (capacityCanvas == null) {
			try {
				capacityCanvas = new gui.CapacityCanvas();
				capacityCanvas.setName("mySpaceCanvas");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return capacityCanvas;
	}
	private AgentCapacityCanvas getAgentCanvas() {
		if (agentCanvas == null) {
			try {
				agentCanvas = new AgentCapacityCanvas();
				agentCanvas.setName("myAgentCanvas");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return agentCanvas;
	}
	private void handleException(java.lang.Throwable exception) {
		exception.printStackTrace(System.out);
	}
	/**
	 * Initialisiert Verbindungen
	 * @exception java.lang.Exception Die Beschreibung der Ausnahmebedingung.
	 */
	private void initListeners() throws java.lang.Exception {
		this.addKeyListener(this);
		this.getMinimumProfitSlider().addChangeListener(new ChangeListener() {
		      public void stateChanged(ChangeEvent e) {
		    	  serviceAggregator.setMinimumProfit(getMinimumProfitSlider().getValue());
		      }
		    });
		getAcceptButton().addActionListener(ivjEventHandler);
		getRejectButton().addActionListener(ivjEventHandler);
		getClearResButton().addActionListener(ivjEventHandler);
		getStartButton().addActionListener(ivjEventHandler);
		this.getAutomatedStartButton().addActionListener(ivjEventHandler);
		this.addWindowListener(ivjEventHandler);
		getLimitCheckBox().addItemListener(ivjEventHandler);
	}
	
	@SuppressWarnings("deprecation")
	private void initialize() {
		try {
			setMinimumSize(new Dimension(100,100));
			getAcceptButton().setEnabled(false);
			getRejectButton().setEnabled(false);
			setName("MyDemonstrator");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setVisible(true);
			setFont(new java.awt.Font("dialog", 0, 10));
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
			setTitle("Resource Allocator");
			setContentPane(getJFrameContentPane());
			initListeners();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		show();
	}
	public void keyReleased(java.awt.event.KeyEvent e) {}
	public void keyTyped(java.awt.event.KeyEvent e) {
	}
	@Override
	public void keyPressed(KeyEvent e) {	
	}

	public void addStats(String agent, String content) {
		if(reportWindow == null)
		{
			reportWindow= new ReportWindow();
			reportWindow.setVisible(true);
		}
		((ReportWindow) reportWindow).addAgent(agent, content);
		
	}
	public void addEmpty() {
		if(reportWindow == null)
		{
			return;
//			reportWindow= new ReportWindow();
//			reportWindow.setVisible(true);
		}
		((ReportWindow) reportWindow).addEmpty();
		
	}
	
}
