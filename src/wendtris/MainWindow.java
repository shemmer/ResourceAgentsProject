package wendtris;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.Serializable;

import javax.swing.JTextField;

import jade.core.Agent;
import resourceAgent.ServiceAggregatorAgent;

/**
 * Die Beschreibung des Typs hier eingeben.
 * Erstellungsdatum: (08.12.2002 14:29:39)
 * @author: 
 */
public class MainWindow extends javax.swing.JFrame implements java.awt.event.KeyListener {
	
	private javax.swing.JPanel mainPanel = null;
	private OfferCanvas offerCanvas = null;
	private OfferFactory offer = null;
	private CapacityCanvas capacityCanvas = null;
	private javax.swing.JPanel ivjJPanel1 = null;
//	private javax.swing.JButton moveLeftButton = null;
//	private javax.swing.JButton moveRightButton = null;
	private javax.swing.JButton rejectButton = null;
	private javax.swing.JButton acceptButton = null;
	private javax.swing.JButton clearButton = null;
	private javax.swing.JLabel pricePerResLabel = null;
	private javax.swing.JLabel priceOrderLabel = null;
	private javax.swing.JLabel incomeLabel = null;
	private javax.swing.JTextField pricePerResTextField = null;
	private javax.swing.JTextField priceOrderTextField = null;
	private javax.swing.JTextField incomeTextField = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTextArea logTextArea = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JLabel stepLabel = null;
	private javax.swing.JTextField stepTextField = null;
	private javax.swing.JTabbedPane controlLogTabbedPane = null;
	private javax.swing.JCheckBox limitStepsCheckBox = null;

	private javax.swing.JLabel aggCostLabel = null;
	private javax.swing.JTextField aggCostField = null;

	private ServiceAggregatorAgent serviceAggregator;

	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.awt.event.WindowListener, Serializable {
		public void actionPerformed(java.awt.event.ActionEvent e) {
//			if (e.getSource() == MainWindow.this.getMoveLeftButton()) 
//				moveOfferLeft(e);
//			if (e.getSource() == MainWindow.this.getMoveRightButton()) 
//				moveOfferRight(e);
			if (e.getSource() == MainWindow.this.getAcceptButton()){ 
				serviceAggregator.addBehaviour(serviceAggregator.new AcceptBehaviour());
			}
			if (e.getSource() == MainWindow.this.getRejectButton()) {
				serviceAggregator.addBehaviour(serviceAggregator.new RejectBehaviour());
			}
			if (e.getSource() == MainWindow.this.getOffer()) {
				serviceAggregator.addBehaviour(serviceAggregator.new ServiceAggStartBehaviour(serviceAggregator, offer));
			}
			if (e.getSource() == MainWindow.this.getClearResButton()){ 
				serviceAggregator.addBehaviour(serviceAggregator.new RestartBehaviour());
			
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
//
//	/**
//	 * connEtoM1:  (JButton1.action.actionPerformed(java.awt.event.ActionEvent) --> mySpace.moveObjectLeft()Z)
//	 * @return boolean
//	 * @param arg1 java.awt.event.ActionEvent
//	 */
//	private boolean moveOfferLeft(java.awt.event.ActionEvent arg1) {
//		boolean connEtoM1Result = false;
//		try {
//			connEtoM1Result = getOffer().moveObjectLeft();
//			toggleAcceptButton(connEtoM1Result);
//		} catch (java.lang.Throwable ivjExc) {
//			handleException(ivjExc);
//		}
//		return connEtoM1Result;
//	}
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
	/**
	 * connEtoM11:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> JTextField3.text)
	 */
	public void updateIncomeTextField() {
		try {
			getIncomeTextField().setText(String.valueOf(getOffer().getProfit()));
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
			initLogTextArea().append(getOffer().getActiveObjectDescription());
			initLogTextArea().append( "; true\n");
		
	}
	/**
	 * connEtoM13:  (JButton5.action.actionPerformed(java.awt.event.ActionEvent) --> JTextArea1.setText(Ljava.lang.String;)V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	private void initLogTextArea(java.awt.event.ActionEvent arg1) {
		try {
			initLogTextArea().setText("nr;income;priceTag; id;accepted\n");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM17:  ( (JButton2,action.actionPerformed(java.awt.event.ActionEvent) --> mySpace,moveObjectRight()Z).normalResult --> JButton4.setEnabled(Z)V)
	 * @param result boolean
	 */
	public void toggleAcceptButton(boolean result) {
		try {
			getAcceptButton().setEnabled(result);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * connEtoM18:  (JButton5.action.actionPerformed(java.awt.event.ActionEvent) --> JButton4.enabled)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	public void enableAcceptButton() {
			getAcceptButton().setEnabled(true);
	}
	public void greenAcceptButton(){
		acceptButton.setBackground(Color.GREEN);
	}
	public void redAcceptButton(){
		acceptButton.setBackground(Color.RED);
	}
	/**
	 * connEtoM19:  (MyDemonstrator.window.windowClosed(java.awt.event.WindowEvent) --> mySpace.close()V)
	 * @param arg1 java.awt.event.WindowEvent
	 */
	private void closeApplication(java.awt.event.WindowEvent arg1) {
		try {
			getOffer().close();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
//	/**
//	 * connEtoM2:  (JButton2.action.actionPerformed(java.awt.event.ActionEvent) --> mySpace.moveObjectRight()Z)
//	 * @return boolean
//	 * @param arg1 java.awt.event.ActionEvent
//	 */
//	private boolean moveOfferRight(java.awt.event.ActionEvent arg1) {
//		boolean connEtoM2Result = false;
//		try {
//			connEtoM2Result = getOffer().moveObjectRight();
//			toggleAcceptButton(connEtoM2Result);
//		} catch (java.lang.Throwable ivjExc) {
//			handleException(ivjExc);
//		}
//		return connEtoM2Result;
//	}
	/**
	 * connEtoM20:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> JTextField4.text)
	 */
	public void updateStepTextField() {
		try {
			getStepTextField().setText(getOffer().getStep());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * connEtoM21:  (MyDemonstrator.initialize() --> mySpace.notifyActionEvent()V)
	 */
	private void notifyOffer() {
		try {
			getOffer().notifyActionEvent();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * connEtoM22:  (JCheckBox1.item.itemStateChanged(java.awt.event.ItemEvent) --> mySpace.setLimitSteps(Z)V)
	 * @param arg1 java.awt.event.ItemEvent
	 */

	private void setLimitToOffer(java.awt.event.ItemEvent arg1) {
		try {
			getOffer().setLimitSteps(getLimitCheckBox().isSelected());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * connEtoM3:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> mySpaceCanvas.repaint()V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	public void repaintCapacityCanvas() {
		try {
			getCapacityCanvas().repaint();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * connEtoM4:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> myObjectCanvas.repaint()V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	public void repaintOfferCanvas() {
			getOfferCanvas().repaint();	
	}
	/**
	 * Updating the log text area with information on a reject object
	 * @param 
	 */
	public void updateLogTextAreaReject() {
			initLogTextArea().append(getOffer().getActiveObjectDescription());
			initLogTextArea().append( "; false\n");
	}
//	/**
//	 * Accepting a offer
//	 * @return boolean Success of accepting an offer
//	 */
//	public boolean acceptActiveObject() {
//		boolean success = false;
//			success = getOffer().acceptActiveObject();
//			toggleAcceptButton(success);
//		return success;
//	}
//	/**
//	 * Rejecting an offer
//	 * @return boolean Success of rejecting an offer
//	 */
//	public boolean rejectActiveObject() {
//		boolean success = false;
//			success = getOffer().rejectActiveObject();
//			this.toggleAcceptButton(success);
//		return success;
//	}
	/**
	 * connEtoM8:  (JButton5.action.actionPerformed(java.awt.event.ActionEvent) --> mySpace.newGame()V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	public void initNewGame() {
			getOffer().newGame();
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
	private void setOfferInOfferCanvas() {
		try {
			getOfferCanvas().setOffer(getOffer());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * connPtoP2SetTarget:  (mySpace.this <--> mySpaceCanvas.mySpace)
	 * this.getOffer -> setOffer in CapacityCanvas also repaints the canvas
	 */
	private void registerOfferInCapacityCanvas() {
		try {
			getCapacityCanvas().setOffer(getOffer());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
//	private javax.swing.JButton getMoveLeftButton() {
//		if (moveLeftButton == null) {
//			try {
//				moveLeftButton = new javax.swing.JButton();
//				moveLeftButton.setName("JButton1");
//				moveLeftButton.setText("Move Left");
//			} catch (java.lang.Throwable ivjExc) {
//				handleException(ivjExc);
//			}
//		}
//		return moveLeftButton;
//	}
//	private javax.swing.JButton getMoveRightButton() {
//		if (moveRightButton == null) {
//			try {
//				moveRightButton = new javax.swing.JButton();
//				moveRightButton.setName("JButton2");
//				moveRightButton.setText("Move Right");
//			} catch (java.lang.Throwable ivjExc) {
//				handleException(ivjExc);
//			}
//		}
//		return moveRightButton;
//	}
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

				java.awt.GridBagConstraints constraintsmyObjectCanvas = new java.awt.GridBagConstraints();
				constraintsmyObjectCanvas.gridx = 0; constraintsmyObjectCanvas.gridy = 0;
				constraintsmyObjectCanvas.fill = java.awt.GridBagConstraints.BOTH;
				constraintsmyObjectCanvas.weightx = 1.0;
				constraintsmyObjectCanvas.weighty = 1.0;
				constraintsmyObjectCanvas.ipadx = 237;
				constraintsmyObjectCanvas.ipady = 187;
				constraintsmyObjectCanvas.insets = new java.awt.Insets(0, 0, 2, 2);
				getJFrameContentPane().add(getOfferCanvas(), constraintsmyObjectCanvas);

				java.awt.GridBagConstraints constraintsmySpaceCanvas = new java.awt.GridBagConstraints();
				constraintsmySpaceCanvas.gridx = 0; constraintsmySpaceCanvas.gridy = 1;
				constraintsmySpaceCanvas.fill = java.awt.GridBagConstraints.BOTH;
				constraintsmySpaceCanvas.weightx = 1.0;
				constraintsmySpaceCanvas.weighty = 2.0;
				constraintsmySpaceCanvas.ipadx = 237;
				constraintsmySpaceCanvas.ipady = 187;
				constraintsmySpaceCanvas.insets = new java.awt.Insets(3, 0, 1, 2);
				getJFrameContentPane().add(getCapacityCanvas(), constraintsmySpaceCanvas);

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
	private javax.swing.JLabel getIncomeLabel() {
		if (incomeLabel == null) {
			try {
				incomeLabel = new javax.swing.JLabel();
				incomeLabel.setName("JLabel3");
				incomeLabel.setText("Income");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return incomeLabel;
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

//				java.awt.GridBagConstraints constraintsJButton1 = new java.awt.GridBagConstraints();
//				constraintsJButton1.gridx = 0; constraintsJButton1.gridy = 5;
//				constraintsJButton1.fill = java.awt.GridBagConstraints.HORIZONTAL;
//				constraintsJButton1.weightx = 1.0;
//				constraintsJButton1.insets = new java.awt.Insets(4, 8, 4, 8);
//				getJPanel1().add(getMoveLeftButton(), constraintsJButton1);
//
//				java.awt.GridBagConstraints constraintsJButton2 = new java.awt.GridBagConstraints();
//				constraintsJButton2.gridx = 1; constraintsJButton2.gridy = 5;
//				constraintsJButton2.fill = java.awt.GridBagConstraints.HORIZONTAL;
//				constraintsJButton2.weightx = 1.0;
//				constraintsJButton2.insets = new java.awt.Insets(4, 8, 4, 8);
//				getJPanel1().add(getMoveRightButton(), constraintsJButton2);

				java.awt.GridBagConstraints constraintsJButton3 = new java.awt.GridBagConstraints();
				constraintsJButton3.gridx = 0; constraintsJButton3.gridy = 6;
				constraintsJButton3.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJButton3.weightx = 1.0;
				constraintsJButton3.insets = new java.awt.Insets(4, 8, 4, 8);
				getJPanel1().add(getRejectButton(), constraintsJButton3);

				java.awt.GridBagConstraints constraintsJButton4 = new java.awt.GridBagConstraints();
				constraintsJButton4.gridx = 1; constraintsJButton4.gridy = 6;
				constraintsJButton4.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJButton4.weightx = 1.0;
				constraintsJButton4.insets = new java.awt.Insets(4, 8, 4, 8);
				getJPanel1().add(getAcceptButton(), constraintsJButton4);

				java.awt.GridBagConstraints constraintsJButton5 = new java.awt.GridBagConstraints();
				constraintsJButton5.gridx = 0; constraintsJButton5.gridy = 7;
				constraintsJButton5.gridwidth = 2;
				constraintsJButton5.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJButton5.insets = new java.awt.Insets(4, 8, 4, 8);
				getJPanel1().add(getClearResButton(), constraintsJButton5);

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

				java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
				constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 1;
				constraintsJLabel2.weightx = 1.0;
				constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getPriceOrderLabel(), constraintsJLabel2);

				java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
				constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 2;
				constraintsJLabel3.weightx = 1.0;
				constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getIncomeLabel(), constraintsJLabel3);

				java.awt.GridBagConstraints constraintsJTextField2 = new java.awt.GridBagConstraints();
				constraintsJTextField2.gridx = 1; constraintsJTextField2.gridy = 1;
				constraintsJTextField2.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJTextField2.weightx = 1.0;
				constraintsJTextField2.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getPriceOrderTextField(), constraintsJTextField2);

				java.awt.GridBagConstraints constraintsJTextField3 = new java.awt.GridBagConstraints();
				constraintsJTextField3.gridx = 1; constraintsJTextField3.gridy = 2;
				constraintsJTextField3.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJTextField3.weightx = 1.0;
				constraintsJTextField3.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getIncomeTextField(), constraintsJTextField3);

				java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
				constraintsJLabel4.gridx = 0; constraintsJLabel4.gridy = 3;
				constraintsJLabel4.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getStepLabel(), constraintsJLabel4);

				java.awt.GridBagConstraints constraintsJTextField4 = new java.awt.GridBagConstraints();
				constraintsJTextField4.gridx = 1; constraintsJTextField4.gridy = 3;
				constraintsJTextField4.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJTextField4.weightx = 1.0;
				constraintsJTextField4.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getStepTextField(), constraintsJTextField4);

				java.awt.GridBagConstraints constraintsJCheckBox1 = new java.awt.GridBagConstraints();
				constraintsJCheckBox1.gridx = 0; constraintsJCheckBox1.gridy = 4;
				constraintsJCheckBox1.gridwidth = 2;
				constraintsJCheckBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJCheckBox1.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getLimitCheckBox(), constraintsJCheckBox1);
				//Custom Code
				java.awt.GridBagConstraints constraintsAggCost = new java.awt.GridBagConstraints();
				constraintsAggCost.gridx = 0; constraintsAggCost.gridy = 10;
				constraintsAggCost.weightx = 1.0;
				constraintsAggCost.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(this.getAggCostLabel(), constraintsAggCost);

				java.awt.GridBagConstraints constraintsAggField= new java.awt.GridBagConstraints();
				constraintsAggField.gridx = 1; constraintsAggField.gridy = 10;
				constraintsAggField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsAggField.weightx = 1.0;
				constraintsAggField.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(this.getAggCostField(), constraintsAggField);

			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
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
	//TODO Log
	public javax.swing.JTextArea initLogTextArea() {
		if (logTextArea == null) {
			try {
				logTextArea = new javax.swing.JTextArea();
				logTextArea.setName("JTextArea1");
				logTextArea.setText("nr; income; priceTag; id; accepted\n");
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
	private javax.swing.JTextField getIncomeTextField() {
		if (incomeTextField == null) {
			try {
				incomeTextField = new javax.swing.JTextField();
				incomeTextField.setName("JTextField3");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return incomeTextField;
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
				offerCanvas = new wendtris.OfferCanvas();
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
				offer = new wendtris.OfferFactory();
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return offer;
	}
	private CapacityCanvas getCapacityCanvas() {
		if (capacityCanvas == null) {
			try {
				capacityCanvas = new wendtris.CapacityCanvas();
				capacityCanvas.setName("mySpaceCanvas");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return capacityCanvas;
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
//		getMoveLeftButton().addActionListener(ivjEventHandler);
//		getMoveRightButton().addActionListener(ivjEventHandler);
		getAcceptButton().addActionListener(ivjEventHandler);
		getRejectButton().addActionListener(ivjEventHandler);
		getClearResButton().addActionListener(ivjEventHandler);
		getOffer().addActionListener(ivjEventHandler);
		this.addWindowListener(ivjEventHandler);
		getLimitCheckBox().addItemListener(ivjEventHandler);
		registerOfferInCapacityCanvas();
		setOfferInOfferCanvas();
	}
	
	private void initialize() {
		try {
			setName("MyDemonstrator");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setVisible(true);
			setFont(new java.awt.Font("dialog", 0, 10));
			setSize(500, 400);
			setTitle("Resource Allocator");
			setContentPane(getJFrameContentPane());
			initListeners();
			notifyOffer();
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
}
