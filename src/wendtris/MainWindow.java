package wendtris;

import javax.swing.JTextField;

import jade.core.Agent;

/**
 * Die Beschreibung des Typs hier eingeben.
 * Erstellungsdatum: (08.12.2002 14:29:39)
 * @author: 
 */
public class MainWindow extends javax.swing.JFrame implements java.awt.event.KeyListener {
	private javax.swing.JPanel mainPanel = null;
	private OfferCanvas offerCanvas = null;
	private Offer offer = null;
	private CapacityCanvas capacityCanvas = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JButton moveLeftButton = null;
	private javax.swing.JButton moveRightButton = null;
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

	private Agent serviceAggregator;

	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.awt.event.WindowListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == MainWindow.this.getMoveLeftButton()) 
				moveOfferLeft(e);
			if (e.getSource() == MainWindow.this.getMoveRightButton()) 
				moveOfferRight(e);
			if (e.getSource() == MainWindow.this.getAcceptButton()) 
				updateLogTextAreaAccept(e);
			if (e.getSource() == MainWindow.this.getAcceptButton()) 
				acceptActiveObject(e);
			if (e.getSource() == MainWindow.this.getRejectButton()) 
				updateLogTextAreaReject(e);
			if (e.getSource() == MainWindow.this.getRejectButton()) 
				rejectActiveObject(e);
			if (e.getSource() == MainWindow.this.getClearResButton()) 
				initNewGame(e);
			if (e.getSource() == MainWindow.this.getOffer()) 
				updatePricePerResTextField(e);
			if (e.getSource() == MainWindow.this.getOffer()) 
				updatePriceOrderTextField(e);
			if (e.getSource() == MainWindow.this.getOffer()) 
				updateIncomeTextField(e);
			if (e.getSource() == MainWindow.this.getOffer()) 
				repaintOfferCanvas(e);
			if (e.getSource() == MainWindow.this.getOffer()) 
				repaintCapacityCanvas(e);
			if (e.getSource() == MainWindow.this.getClearResButton()) 
				initLogTextArea(e);
			if (e.getSource() == MainWindow.this.getClearResButton()) 
				enableAcceptButton(e);
			if (e.getSource() == MainWindow.this.getOffer()){
				//TODO Changes in my space -> activate Service Aggregator
				serviceAggregator.addBehaviour(new ServiceAggStartBehaviour(serviceAggregator, offer));
				updateStepTextField(e);
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
	 * MyDemonstrator - Konstruktorkommentar.
	 * @param agent Service Aggregator agent invoking the creation of MainWindow
	 */
	public MainWindow(Agent a) {
		super();
		this.serviceAggregator= a;
		initialize();
	}

	/**
	 * connEtoM1:  (JButton1.action.actionPerformed(java.awt.event.ActionEvent) --> mySpace.moveObjectLeft()Z)
	 * @return boolean
	 * @param arg1 java.awt.event.ActionEvent
	 */
	private boolean moveOfferLeft(java.awt.event.ActionEvent arg1) {
		boolean connEtoM1Result = false;
		try {
			connEtoM1Result = getOffer().moveObjectLeft();
			toggleAcceptButton(connEtoM1Result);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		return connEtoM1Result;
	}
	/**
	 * connEtoM10:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> JTextField2.text)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	private void updatePriceOrderTextField(java.awt.event.ActionEvent arg1) {
		try {
			getPriceOrderTextField().setText(String.valueOf(getOffer().getActiveObjectIncome()));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * connEtoM11:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> JTextField3.text)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	private void updateIncomeTextField(java.awt.event.ActionEvent arg1) {
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
	private void updateLogTextAreaAccept(java.awt.event.ActionEvent arg1) {
		try {
			initLogTextArea().append(getOffer().getActiveObjectDescription());
			initLogTextArea().append( "; true\n");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
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
	private void toggleAcceptButton(boolean result) {
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
	private void enableAcceptButton(java.awt.event.ActionEvent arg1) {
		try {
			getAcceptButton().setEnabled(true);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
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
	/**
	 * connEtoM2:  (JButton2.action.actionPerformed(java.awt.event.ActionEvent) --> mySpace.moveObjectRight()Z)
	 * @return boolean
	 * @param arg1 java.awt.event.ActionEvent
	 */
	private boolean moveOfferRight(java.awt.event.ActionEvent arg1) {
		boolean connEtoM2Result = false;
		try {
			connEtoM2Result = getOffer().moveObjectRight();
			toggleAcceptButton(connEtoM2Result);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		return connEtoM2Result;
	}
	/**
	 * connEtoM20:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> JTextField4.text)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	private void updateStepTextField(java.awt.event.ActionEvent arg1) {
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
	private void repaintCapacityCanvas(java.awt.event.ActionEvent arg1) {
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
	private void repaintOfferCanvas(java.awt.event.ActionEvent arg1) {
		try {
			getOfferCanvas().repaint();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * connEtoM5:  (JButton3.action.actionPerformed(java.awt.event.ActionEvent) --> JTextArea1.append(Ljava.lang.String;)V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	private void updateLogTextAreaReject(java.awt.event.ActionEvent arg1) {
		try {
			initLogTextArea().append(getOffer().getActiveObjectDescription());

			initLogTextArea().append( "; false\n");

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * connEtoM6:  (JButton4.action.actionPerformed(java.awt.event.ActionEvent) --> mySpace.acceptActiveObject()Z)
	 * @return boolean
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
	private boolean acceptActiveObject(java.awt.event.ActionEvent arg1) {
		boolean connEtoM6Result = false;
		try {
			// user code begin {1}
			// user code end
			connEtoM6Result = getOffer().acceptActiveObject();
			toggleAcceptButton(connEtoM6Result);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
		return connEtoM6Result;
	}
	/**
	 * connEtoM7:  (JButton3.action.actionPerformed(java.awt.event.ActionEvent) --> mySpace.rejectActiveObject()Z)
	 * @return boolean
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
	private boolean rejectActiveObject(java.awt.event.ActionEvent arg1) {
		boolean connEtoM7Result = false;
		try {
			connEtoM7Result = getOffer().rejectActiveObject();
			this.toggleAcceptButton(connEtoM7Result);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		return connEtoM7Result;
	}
	/**
	 * connEtoM8:  (JButton5.action.actionPerformed(java.awt.event.ActionEvent) --> mySpace.newGame()V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	private void initNewGame(java.awt.event.ActionEvent arg1) {
		try {
			getOffer().newGame();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	/**
	 * connEtoM9:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> JTextField1.text)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	private void updatePricePerResTextField(java.awt.event.ActionEvent arg1) {
		try {
			getPricePerResTextField().setText(getOffer().getFormatedActiveObjectPriceTag());
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
	private javax.swing.JButton getMoveLeftButton() {
		if (moveLeftButton == null) {
			try {
				moveLeftButton = new javax.swing.JButton();
				moveLeftButton.setName("JButton1");
				moveLeftButton.setText("Move Left");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return moveLeftButton;
	}
	private javax.swing.JButton getMoveRightButton() {
		if (moveRightButton == null) {
			try {
				moveRightButton = new javax.swing.JButton();
				moveRightButton.setName("JButton2");
				moveRightButton.setText("Move Right");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return moveRightButton;
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
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
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
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
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

				java.awt.GridBagConstraints constraintsJButton1 = new java.awt.GridBagConstraints();
				constraintsJButton1.gridx = 0; constraintsJButton1.gridy = 5;
				constraintsJButton1.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJButton1.weightx = 1.0;
				constraintsJButton1.insets = new java.awt.Insets(4, 8, 4, 8);
				getJPanel1().add(getMoveLeftButton(), constraintsJButton1);

				java.awt.GridBagConstraints constraintsJButton2 = new java.awt.GridBagConstraints();
				constraintsJButton2.gridx = 1; constraintsJButton2.gridy = 5;
				constraintsJButton2.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJButton2.weightx = 1.0;
				constraintsJButton2.insets = new java.awt.Insets(4, 8, 4, 8);
				getJPanel1().add(getMoveRightButton(), constraintsJButton2);

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
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
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
	private javax.swing.JTextArea initLogTextArea() {
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
	private Offer getOffer() {
		if (offer == null) {
			try {
				offer = new wendtris.Offer();
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
	/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
	private void initListeners() throws java.lang.Exception {
		this.addKeyListener(this);
		getMoveLeftButton().addActionListener(ivjEventHandler);
		getMoveRightButton().addActionListener(ivjEventHandler);
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
	/**
	 * Invoked when a key has been pressed.
	 */
	public void keyPressed(java.awt.event.KeyEvent e) {
		int key = e.getKeyCode();
		java.awt.event.ActionEvent ae = null;
		switch (key) {
		case 37:	moveOfferLeft( new java.awt.event.ActionEvent(this, 0, null));
		//getmySpace().moveObjectLeft();
		break;
		case 39: 	moveOfferRight( new java.awt.event.ActionEvent(this, 0, null));
		//getmySpace().moveObjectRight();
		break;
		case 65:
		case 40: 	if( getAcceptButton().isEnabled())
			ae = new java.awt.event.ActionEvent( this, 0, null);
		acceptActiveObject( ae);
		updateLogTextAreaAccept( ae);
		break;
		case 82:
		case 38: 	ae = new java.awt.event.ActionEvent( this, 0, null);
		rejectActiveObject( ae);
		updateLogTextAreaReject( ae);
		break;

		default :	System.out.println(e.toString());

		}


	}
	/**
	 * Invoked when a key has been released.
	 */
	public void keyReleased(java.awt.event.KeyEvent e) {}
	/**
	 * Invoked when a key has been typed.
	 * This event occurs when a key press is followed by a key release.
	 */
	public void keyTyped(java.awt.event.KeyEvent e) {

	}
}