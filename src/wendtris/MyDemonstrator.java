package wendtris;

/**
 * Die Beschreibung des Typs hier eingeben.
 * Erstellungsdatum: (08.12.2002 14:29:39)
 * @author: 
 */
public class MyDemonstrator extends javax.swing.JFrame implements java.awt.event.KeyListener {
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private MyObjectCanvas ivjmyObjectCanvas = null;
	private MySpace ivjmySpace = null;
	private MySpaceCanvas ivjmySpaceCanvas = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JButton ivjJButton1 = null;
	private javax.swing.JButton ivjJButton2 = null;
	private javax.swing.JButton ivjJButton3 = null;
	private javax.swing.JButton ivjJButton4 = null;
	private javax.swing.JButton ivjJButton5 = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabel3 = null;
	private javax.swing.JTextField ivjJTextField1 = null;
	private javax.swing.JTextField ivjJTextField2 = null;
	private javax.swing.JTextField ivjJTextField3 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTextArea ivjJTextArea1 = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JLabel ivjJLabel4 = null;
	private javax.swing.JTextField ivjJTextField4 = null;
	private javax.swing.JTabbedPane ivjJTabbedPane1 = null;
	private javax.swing.JCheckBox ivjJCheckBox1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.awt.event.WindowListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == MyDemonstrator.this.getJButton1()) 
				connEtoM1(e);
			if (e.getSource() == MyDemonstrator.this.getJButton2()) 
				connEtoM2(e);
			if (e.getSource() == MyDemonstrator.this.getJButton4()) 
				connEtoM12(e);
			if (e.getSource() == MyDemonstrator.this.getJButton4()) 
				connEtoM6(e);
			if (e.getSource() == MyDemonstrator.this.getJButton3()) 
				connEtoM5(e);
			if (e.getSource() == MyDemonstrator.this.getJButton3()) 
				connEtoM7(e);
			if (e.getSource() == MyDemonstrator.this.getJButton5()) 
				connEtoM8(e);
			if (e.getSource() == MyDemonstrator.this.getmySpace()) 
				connEtoM9(e);
			if (e.getSource() == MyDemonstrator.this.getmySpace()) 
				connEtoM10(e);
			if (e.getSource() == MyDemonstrator.this.getmySpace()) 
				connEtoM11(e);
			if (e.getSource() == MyDemonstrator.this.getmySpace()) 
				connEtoM4(e);
			if (e.getSource() == MyDemonstrator.this.getmySpace()) 
				connEtoM3(e);
			if (e.getSource() == MyDemonstrator.this.getJButton5()) 
				connEtoM13(e);
			if (e.getSource() == MyDemonstrator.this.getJButton5()) 
				connEtoM18(e);
			if (e.getSource() == MyDemonstrator.this.getmySpace()) 
				connEtoM20(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == MyDemonstrator.this.getJCheckBox1()) 
				connEtoM22(e);
		};
		public void windowActivated(java.awt.event.WindowEvent e) {};
		public void windowClosed(java.awt.event.WindowEvent e) {
			if (e.getSource() == MyDemonstrator.this) 
				connEtoM19(e);
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
public MyDemonstrator() {
	super();
	initialize();
}
/**
 * MyDemonstrator - Konstruktorkommentar.
 * @param title java.lang.String
 */
public MyDemonstrator(String title) {
	super(title);
}
/**
 * connEtoM1:  (JButton1.action.actionPerformed(java.awt.event.ActionEvent) --> mySpace.moveObjectLeft()Z)
 * @return boolean
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private boolean connEtoM1(java.awt.event.ActionEvent arg1) {
	boolean connEtoM1Result = false;
	try {
		// user code begin {1}
		// user code end
		connEtoM1Result = getmySpace().moveObjectLeft();
		connEtoM14(connEtoM1Result);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	return connEtoM1Result;
}
/**
 * connEtoM10:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> JTextField2.text)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJTextField2().setText(String.valueOf(getmySpace().getActiveObjectIncome()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM11:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> JTextField3.text)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM11(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJTextField3().setText(String.valueOf(getmySpace().getProfit()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM12:  (JButton4.action.actionPerformed(java.awt.event.ActionEvent) --> JTextArea1.append(Ljava.lang.String;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM12(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJTextArea1().append(getmySpace().getActiveObjectDescription());
		// user code begin {2}
		getJTextArea1().append( "; true\n");
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM13:  (JButton5.action.actionPerformed(java.awt.event.ActionEvent) --> JTextArea1.setText(Ljava.lang.String;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM13(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJTextArea1().setText("nr;income;priceTag; id;accepted\n");
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM14:  ( (JButton1,action.actionPerformed(java.awt.event.ActionEvent) --> mySpace,moveObjectLeft()Z).normalResult --> JButton4.setEnabled(Z)V)
 * @param result boolean
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM14(boolean result) {
	try {
		// user code begin {1}
		// user code end
		getJButton4().setEnabled(result);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM15:  ( (JButton3,action.actionPerformed(java.awt.event.ActionEvent) --> mySpace,rejectActiveObject()Z).normalResult --> JButton4.setEnabled(Z)V)
 * @param result boolean
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM15(boolean result) {
	try {
		// user code begin {1}
		// user code end
		getJButton4().setEnabled(result);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM16:  ( (JButton4,action.actionPerformed(java.awt.event.ActionEvent) --> mySpace,acceptActiveObject()Z).normalResult --> JButton4.setEnabled(Z)V)
 * @param result boolean
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM16(boolean result) {
	try {
		// user code begin {1}
		// user code end
		getJButton4().setEnabled(result);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM17:  ( (JButton2,action.actionPerformed(java.awt.event.ActionEvent) --> mySpace,moveObjectRight()Z).normalResult --> JButton4.setEnabled(Z)V)
 * @param result boolean
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM17(boolean result) {
	try {
		// user code begin {1}
		// user code end
		getJButton4().setEnabled(result);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM18:  (JButton5.action.actionPerformed(java.awt.event.ActionEvent) --> JButton4.enabled)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM18(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJButton4().setEnabled(true);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM19:  (MyDemonstrator.window.windowClosed(java.awt.event.WindowEvent) --> mySpace.close()V)
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM19(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getmySpace().close();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (JButton2.action.actionPerformed(java.awt.event.ActionEvent) --> mySpace.moveObjectRight()Z)
 * @return boolean
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private boolean connEtoM2(java.awt.event.ActionEvent arg1) {
	boolean connEtoM2Result = false;
	try {
		// user code begin {1}
		// user code end
		connEtoM2Result = getmySpace().moveObjectRight();
		connEtoM17(connEtoM2Result);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	return connEtoM2Result;
}
/**
 * connEtoM20:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> JTextField4.text)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM20(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJTextField4().setText(getmySpace().getStep());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM21:  (MyDemonstrator.initialize() --> mySpace.notifyActionEvent()V)
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM21() {
	try {
		// user code begin {1}
		// user code end
		getmySpace().notifyActionEvent();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM22:  (JCheckBox1.item.itemStateChanged(java.awt.event.ItemEvent) --> mySpace.setLimitSteps(Z)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM22(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getmySpace().setLimitSteps(getJCheckBox1().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> mySpaceCanvas.repaint()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getmySpaceCanvas().repaint();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM4:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> myObjectCanvas.repaint()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getmyObjectCanvas().repaint();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM5:  (JButton3.action.actionPerformed(java.awt.event.ActionEvent) --> JTextArea1.append(Ljava.lang.String;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJTextArea1().append(getmySpace().getActiveObjectDescription());
		// user code begin {2}
		getJTextArea1().append( "; false\n");
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM6:  (JButton4.action.actionPerformed(java.awt.event.ActionEvent) --> mySpace.acceptActiveObject()Z)
 * @return boolean
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private boolean connEtoM6(java.awt.event.ActionEvent arg1) {
	boolean connEtoM6Result = false;
	try {
		// user code begin {1}
		// user code end
		connEtoM6Result = getmySpace().acceptActiveObject();
		connEtoM16(connEtoM6Result);
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
private boolean connEtoM7(java.awt.event.ActionEvent arg1) {
	boolean connEtoM7Result = false;
	try {
		// user code begin {1}
		// user code end
		connEtoM7Result = getmySpace().rejectActiveObject();
		connEtoM15(connEtoM7Result);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	return connEtoM7Result;
}
/**
 * connEtoM8:  (JButton5.action.actionPerformed(java.awt.event.ActionEvent) --> mySpace.newGame()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getmySpace().newGame();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM9:  (mySpace.action.actionPerformed(java.awt.event.ActionEvent) --> JTextField1.text)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connEtoM9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJTextField1().setText(getmySpace().getFormatedActiveObjectPriceTag());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetTarget:  (mySpace.this <--> myObjectCanvas.mySpace)
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connPtoP1SetTarget() {
	/* Das Ziel des Quellenobjekts definieren */
	try {
		getmyObjectCanvas().setMySpace(getmySpace());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP2SetTarget:  (mySpace.this <--> mySpaceCanvas.mySpace)
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void connPtoP2SetTarget() {
	/* Das Ziel des Quellenobjekts definieren */
	try {
		getmySpaceCanvas().setMySpace(getmySpace());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Den Eigenschaftswert JButton1 zurückgeben.
 * @return javax.swing.JButton
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JButton getJButton1() {
	if (ivjJButton1 == null) {
		try {
			ivjJButton1 = new javax.swing.JButton();
			ivjJButton1.setName("JButton1");
			ivjJButton1.setText("Move Left");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton1;
}
/**
 * Den Eigenschaftswert JButton2 zurückgeben.
 * @return javax.swing.JButton
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JButton getJButton2() {
	if (ivjJButton2 == null) {
		try {
			ivjJButton2 = new javax.swing.JButton();
			ivjJButton2.setName("JButton2");
			ivjJButton2.setText("Move Right");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton2;
}
/**
 * Den Eigenschaftswert JButton3 zurückgeben.
 * @return javax.swing.JButton
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JButton getJButton3() {
	if (ivjJButton3 == null) {
		try {
			ivjJButton3 = new javax.swing.JButton();
			ivjJButton3.setName("JButton3");
			ivjJButton3.setText("Reject");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton3;
}
/**
 * Den Eigenschaftswert JButton4 zurückgeben.
 * @return javax.swing.JButton
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JButton getJButton4() {
	if (ivjJButton4 == null) {
		try {
			ivjJButton4 = new javax.swing.JButton();
			ivjJButton4.setName("JButton4");
			ivjJButton4.setText("Accept");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton4;
}
/**
 * Den Eigenschaftswert JButton5 zurückgeben.
 * @return javax.swing.JButton
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JButton getJButton5() {
	if (ivjJButton5 == null) {
		try {
			ivjJButton5 = new javax.swing.JButton();
			ivjJButton5.setName("JButton5");
			ivjJButton5.setText("Clear Resource");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton5;
}
/**
 * Den Eigenschaftswert JCheckBox1 zurückgeben.
 * @return javax.swing.JCheckBox
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JCheckBox getJCheckBox1() {
	if (ivjJCheckBox1 == null) {
		try {
			ivjJCheckBox1 = new javax.swing.JCheckBox();
			ivjJCheckBox1.setName("JCheckBox1");
			ivjJCheckBox1.setText("Limit Steps");
			ivjJCheckBox1.setForeground(new java.awt.Color(102,102,153));
			ivjJCheckBox1.setSelected(true);
			ivjJCheckBox1.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJCheckBox1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBox1;
}
/**
 * Den Eigenschaftswert JFrameContentPane zurückgeben.
 * @return javax.swing.JPanel
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new java.awt.GridBagLayout());
			ivjJFrameContentPane.setDoubleBuffered(true);
			ivjJFrameContentPane.setVisible(true);

			java.awt.GridBagConstraints constraintsmyObjectCanvas = new java.awt.GridBagConstraints();
			constraintsmyObjectCanvas.gridx = 0; constraintsmyObjectCanvas.gridy = 0;
			constraintsmyObjectCanvas.fill = java.awt.GridBagConstraints.BOTH;
			constraintsmyObjectCanvas.weightx = 1.0;
			constraintsmyObjectCanvas.weighty = 1.0;
			constraintsmyObjectCanvas.ipadx = 237;
			constraintsmyObjectCanvas.ipady = 187;
			constraintsmyObjectCanvas.insets = new java.awt.Insets(0, 0, 2, 2);
			getJFrameContentPane().add(getmyObjectCanvas(), constraintsmyObjectCanvas);

			java.awt.GridBagConstraints constraintsmySpaceCanvas = new java.awt.GridBagConstraints();
			constraintsmySpaceCanvas.gridx = 0; constraintsmySpaceCanvas.gridy = 1;
			constraintsmySpaceCanvas.fill = java.awt.GridBagConstraints.BOTH;
			constraintsmySpaceCanvas.weightx = 1.0;
			constraintsmySpaceCanvas.weighty = 2.0;
			constraintsmySpaceCanvas.ipadx = 237;
			constraintsmySpaceCanvas.ipady = 187;
			constraintsmySpaceCanvas.insets = new java.awt.Insets(3, 0, 1, 2);
			getJFrameContentPane().add(getmySpaceCanvas(), constraintsmySpaceCanvas);

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
	return ivjJFrameContentPane;
}
/**
 * Den Eigenschaftswert JLabel1 zurückgeben.
 * JLabel1 Price per ResUnit
 * @return javax.swing.JLabel
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Price per ResUnit");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Den Eigenschaftswert JLabel2 zurückgeben.
 * JLabel2 Price for this Order
 * @return javax.swing.JLabel
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("Price for this Order");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}
/**
 * Den Eigenschaftswert JLabel3 zurückgeben.
 * @return javax.swing.JLabel
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setText("Income");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}
/**
 * Den Eigenschaftswert JLabel4 zurückgeben.
 * @return javax.swing.JLabel
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JLabel getJLabel4() {
	if (ivjJLabel4 == null) {
		try {
			ivjJLabel4 = new javax.swing.JLabel();
			ivjJLabel4.setName("JLabel4");
			ivjJLabel4.setText("Step");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel4;
}
/**
 * Den Eigenschaftswert JPanel1 zurückgeben.
 * 
 * JPanel1 Linke Seite
 * @return javax.swing.JPanel
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
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
			getJPanel1().add(getJButton1(), constraintsJButton1);

			java.awt.GridBagConstraints constraintsJButton2 = new java.awt.GridBagConstraints();
			constraintsJButton2.gridx = 1; constraintsJButton2.gridy = 5;
			constraintsJButton2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButton2.weightx = 1.0;
			constraintsJButton2.insets = new java.awt.Insets(4, 8, 4, 8);
			getJPanel1().add(getJButton2(), constraintsJButton2);

			java.awt.GridBagConstraints constraintsJButton3 = new java.awt.GridBagConstraints();
			constraintsJButton3.gridx = 0; constraintsJButton3.gridy = 6;
			constraintsJButton3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButton3.weightx = 1.0;
			constraintsJButton3.insets = new java.awt.Insets(4, 8, 4, 8);
			getJPanel1().add(getJButton3(), constraintsJButton3);

			java.awt.GridBagConstraints constraintsJButton4 = new java.awt.GridBagConstraints();
			constraintsJButton4.gridx = 1; constraintsJButton4.gridy = 6;
			constraintsJButton4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButton4.weightx = 1.0;
			constraintsJButton4.insets = new java.awt.Insets(4, 8, 4, 8);
			getJPanel1().add(getJButton4(), constraintsJButton4);

			java.awt.GridBagConstraints constraintsJButton5 = new java.awt.GridBagConstraints();
			constraintsJButton5.gridx = 0; constraintsJButton5.gridy = 7;
			constraintsJButton5.gridwidth = 2;
			constraintsJButton5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButton5.insets = new java.awt.Insets(4, 8, 4, 8);
			getJPanel1().add(getJButton5(), constraintsJButton5);

			java.awt.GridBagConstraints constraintsJTextField1 = new java.awt.GridBagConstraints();
			constraintsJTextField1.gridx = 1; constraintsJTextField1.gridy = 0;
			constraintsJTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextField1.weightx = 1.0;
			constraintsJTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJTextField1(), constraintsJTextField1);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.weightx = 1.0;
			constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 1;
			constraintsJLabel2.weightx = 1.0;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 2;
			constraintsJLabel3.weightx = 1.0;
			constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel3(), constraintsJLabel3);

			java.awt.GridBagConstraints constraintsJTextField2 = new java.awt.GridBagConstraints();
			constraintsJTextField2.gridx = 1; constraintsJTextField2.gridy = 1;
			constraintsJTextField2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextField2.weightx = 1.0;
			constraintsJTextField2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJTextField2(), constraintsJTextField2);

			java.awt.GridBagConstraints constraintsJTextField3 = new java.awt.GridBagConstraints();
			constraintsJTextField3.gridx = 1; constraintsJTextField3.gridy = 2;
			constraintsJTextField3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextField3.weightx = 1.0;
			constraintsJTextField3.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJTextField3(), constraintsJTextField3);

			java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
			constraintsJLabel4.gridx = 0; constraintsJLabel4.gridy = 3;
			constraintsJLabel4.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel4(), constraintsJLabel4);

			java.awt.GridBagConstraints constraintsJTextField4 = new java.awt.GridBagConstraints();
			constraintsJTextField4.gridx = 1; constraintsJTextField4.gridy = 3;
			constraintsJTextField4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextField4.weightx = 1.0;
			constraintsJTextField4.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJTextField4(), constraintsJTextField4);

			java.awt.GridBagConstraints constraintsJCheckBox1 = new java.awt.GridBagConstraints();
			constraintsJCheckBox1.gridx = 0; constraintsJCheckBox1.gridy = 4;
			constraintsJCheckBox1.gridwidth = 2;
			constraintsJCheckBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJCheckBox1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJCheckBox1(), constraintsJCheckBox1);
			// user code begin {1}
			// user code end
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
			getJScrollPane1().setViewportView(getJTextArea1());
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
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JTabbedPane getJTabbedPane1() {
	if (ivjJTabbedPane1 == null) {
		try {
			ivjJTabbedPane1 = new javax.swing.JTabbedPane();
			ivjJTabbedPane1.setName("JTabbedPane1");
			ivjJTabbedPane1.insertTab("Control", null, getJPanel1(), null, 0);
			ivjJTabbedPane1.insertTab("Log", null, getJScrollPane1(), null, 1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane1;
}
/**
 * Den Eigenschaftswert JTextArea1 zurückgeben.
 * @return javax.swing.JTextArea
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JTextArea getJTextArea1() {
	if (ivjJTextArea1 == null) {
		try {
			ivjJTextArea1 = new javax.swing.JTextArea();
			ivjJTextArea1.setName("JTextArea1");
			ivjJTextArea1.setText("nr; income; priceTag; id; accepted\n");
			ivjJTextArea1.setBounds(0, 0, 163, 197);
			ivjJTextArea1.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextArea1;
}
/**
 * Den Eigenschaftswert JTextField1 zurückgeben.
 * @return javax.swing.JTextField
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JTextField getJTextField1() {
	if (ivjJTextField1 == null) {
		try {
			ivjJTextField1 = new javax.swing.JTextField();
			ivjJTextField1.setName("JTextField1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField1;
}
/**
 * Den Eigenschaftswert JTextField2 zurückgeben.
 * @return javax.swing.JTextField
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JTextField getJTextField2() {
	if (ivjJTextField2 == null) {
		try {
			ivjJTextField2 = new javax.swing.JTextField();
			ivjJTextField2.setName("JTextField2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField2;
}
/**
 * Den Eigenschaftswert JTextField3 zurückgeben.
 * @return javax.swing.JTextField
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JTextField getJTextField3() {
	if (ivjJTextField3 == null) {
		try {
			ivjJTextField3 = new javax.swing.JTextField();
			ivjJTextField3.setName("JTextField3");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField3;
}
/**
 * Den Eigenschaftswert JTextField4 zurückgeben.
 * @return javax.swing.JTextField
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private javax.swing.JTextField getJTextField4() {
	if (ivjJTextField4 == null) {
		try {
			ivjJTextField4 = new javax.swing.JTextField();
			ivjJTextField4.setName("JTextField4");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField4;
}
/**
 * Den Eigenschaftswert myObjectCanvas zurückgeben.
 * @return wendtris.MyObjectCanvas
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private MyObjectCanvas getmyObjectCanvas() {
	if (ivjmyObjectCanvas == null) {
		try {
			ivjmyObjectCanvas = new wendtris.MyObjectCanvas();
			ivjmyObjectCanvas.setName("myObjectCanvas");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmyObjectCanvas;
}
/**
 * Den Eigenschaftswert mySpace zurückgeben.
 * @return wendtris.MySpace
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private MySpace getmySpace() {
	if (ivjmySpace == null) {
		try {
			ivjmySpace = new wendtris.MySpace();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmySpace;
}
/**
 * Den Eigenschaftswert mySpaceCanvas zurückgeben.
 * @return wendtris.MySpaceCanvas
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private MySpaceCanvas getmySpaceCanvas() {
	if (ivjmySpaceCanvas == null) {
		try {
			ivjmySpaceCanvas = new wendtris.MySpaceCanvas();
			ivjmySpaceCanvas.setName("mySpaceCanvas");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmySpaceCanvas;
}
/**
 * Wird aufgerufen, wenn die Komponente eine Ausnahmebedingung übergibt.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Entfernen Sie den Kommentar für die folgenden Zeilen, um nicht abgefangene Ausnahmebedingungen auf der Standardausgabeeinheit (stdout) auszugeben */
	// System.out.println("--------- NICHT ABGEFANGENE AUSNAHMEBEDINGUNG ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initialisiert Verbindungen
 * @exception java.lang.Exception Die Beschreibung der Ausnahmebedingung.
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	this.addKeyListener(this);
	// user code end
	getJButton1().addActionListener(ivjEventHandler);
	getJButton2().addActionListener(ivjEventHandler);
	getJButton4().addActionListener(ivjEventHandler);
	getJButton3().addActionListener(ivjEventHandler);
	getJButton5().addActionListener(ivjEventHandler);
	getmySpace().addActionListener(ivjEventHandler);
	this.addWindowListener(ivjEventHandler);
	getJCheckBox1().addItemListener(ivjEventHandler);
	connPtoP2SetTarget();
	connPtoP1SetTarget();
}
/**
 * Die Klasse initialisieren.
 */
/* WARNUNG: DIESE METHODE WIRD ERNEUT GENERIERT. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("MyDemonstrator");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
		setFont(new java.awt.Font("dialog", 0, 10));
		setSize(500, 400);
		setTitle("Resource Allocator");
		setContentPane(getJFrameContentPane());
		initConnections();
		connEtoM21();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	show();
	// user code end
}
	/**
	 * Invoked when a key has been pressed.
	 */
public void keyPressed(java.awt.event.KeyEvent e) {
	int key = e.getKeyCode();
	java.awt.event.ActionEvent ae = null;
	switch (key) {
		case 37:	connEtoM1( new java.awt.event.ActionEvent(this, 0, null));
					//getmySpace().moveObjectLeft();
					break;
		case 39: 	connEtoM2( new java.awt.event.ActionEvent(this, 0, null));
					//getmySpace().moveObjectRight();
					break;
		case 65:
		case 40: 	if( getJButton4().isEnabled())
						ae = new java.awt.event.ActionEvent( this, 0, null);
						connEtoM6( ae);
						connEtoM12( ae);
					break;
		case 82:
		case 38: 	ae = new java.awt.event.ActionEvent( this, 0, null);
					connEtoM7( ae);
					connEtoM5( ae);
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
