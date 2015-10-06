package wendtris;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import resourceAgent.Resource;


public class Offer implements Serializable {
	public static final int left = 1, right = 2, accept = 3, reject = 4;

	public byte[][] space = new byte[maxCols][maxRows];

	private boolean activeObjectState = false;
	private byte activeObject[] = null;
	private int activeObjectIncome = 1;
	private byte activeObjectID = 0;
	private int activeObjectPriceTag = 1;
	java.util.Random random = new java.util.Random();

	private Map<Resource, Byte> activeObjectMap = new HashMap<Resource,Byte>();
	public Map<Resource, Byte> getActiveObjectMap() {
		return activeObjectMap;
	}
	public void setActiveObjectMap(Map<Resource, Byte> activeObjectMap) {
		this.activeObjectMap = activeObjectMap;
	}
	private int objectNumber = 0;	
	public static final int maxRows = 6, maxCols = 12;

// Die Breite der Objectsspalten sollte maxCols entsprechen, sonst funktioniert hier nix!
	private static final byte[][] thisObject = 
						{ { 0,0,0,0,0,0,0,0,0,0,0,0 }, // dummy, da wir nicht mit objectID = 0 arbeiten können
						  { 0,0,0,0,1,2,3,2,1,0,0,0 },
						  { 1,0,0,0,1,1,1,1,0,0,0,0 },
						  { 0,0,0,0,0,2,2,0,0,0,0,0 }, 
						  { 0,0,0,0,2,2,1,1,0,0,0,0 },
						  { 0,0,0,0,1,3,2,2,1,0,0,0 },
						  { 0,0,0,0,2,1,1,1,3,3,0,0 } };

	private int minPrice = 1, priceSpan = 9; // thus maxPrice = 10;
	private int profit = 0;
	
	private java.util.Vector actionListeners = new java.util.Vector();

	private java.text.DecimalFormat df = new java.text.DecimalFormat();

	private int step = 0, maxStep = 20;
	private boolean limitSteps = true;
public Offer() {
	super();
	df.setMaximumFractionDigits(2);
	newGame();
}
/**
 * Setzt das aktive Objekt zurück. Damit kann das Nächste erscheinen.
 */
public boolean acceptActiveObject() {
	for( int x=0; x<maxCols; x++) if( activeObject[x] > 0) {
		int y = 0;
		while( space[x][y] != 0) y++;
		while( activeObject[x] > 0) {
			space[x][y] = activeObjectID;
			y++; activeObject[x]--;
		}
	}
	profit += activeObjectIncome;
	activeObjectState = false;
	boolean r = activateObject();
	objectNumber++;
	notifyActionEvent();
	return r;
}
/**
 * Ein Objekt mit der Parameterbezeichnung wird in unser Feld eingefuegt.
 */
private boolean activateObject() {
	if( limitSteps && step>=maxStep) return false;
	if( activeObjectState) System.out.println("Error at activeObject()");
	else {
		while( 0 == (activeObjectID = (byte)random.nextInt(thisObject.length))) {};
		activeObject = new byte[ maxCols];
		for( int i=0; i<maxCols; i++) 
		{
			//insert the contents of the two-dimensional arrays second dimension into active object at
			//activeobjectid
			activeObject[i] = thisObject[ activeObjectID][i];
			if(thisObject[ activeObjectID][i]!=0){
				this.activeObjectMap.put(Resource.allValuesAsList().get(i), thisObject[ activeObjectID][i]);
			}
		}
		activeObjectPriceTag = minPrice + random.nextInt( priceSpan);
		activeObjectIncome = 0;
		for( int i=0; i<maxCols; i++) activeObjectIncome += activeObject[i];
		activeObjectIncome *= activeObjectPriceTag;
		activeObjectState = true;
		++step;
	}
	
	return testResouces();
}
public void addActionListener( ActionListener a) {
	if ( !actionListeners.contains(a) ) actionListeners.addElement(a);	
}
/**
 * Die Beschreibung der Methode hier eingeben.
 * Erstellungsdatum: (10.12.2002 11:58:10)
 */
public void close() {
	System.out.println("Closing Application");
	System.exit(0);
}
	public byte[] getActiveObject() { return activeObject; }
	public String getActiveObjectDescription() {
		return objectNumber+"; "+activeObjectIncome+"; "+activeObjectPriceTag+"; "+activeObjectID;
	}
public byte getActiveObjectID() { return activeObjectID; }
	public int getActiveObjectIncome() { return activeObjectIncome; }
	public int getActiveObjectPriceTag() { return activeObjectPriceTag;}
	public boolean getActiveObjectState() { return activeObjectState; }
	public String getFormatedActiveObjectPriceTag() { return df.format(((double)activeObjectPriceTag));}
	public int getMaxPriceTag() { return minPrice+priceSpan+4; }
	public int getProfit() { return profit; }
	public String getStep() {
		return step+(limitSteps?" / "+maxStep:"");
	}
/**
 * Die Beschreibung der Methode hier eingeben.
 * Erstellungsdatum: (10.12.2002 21:32:41)
 * @return boolean
 */
public boolean isLimitSteps() {
	return limitSteps;
}
/**
 * Alle Bewegungen der aktiven Objekte sind in dieser Methode implementiert.
 */
public boolean moveObject(int direction) {
	if (activeObjectState) {
		switch (direction) {
			case left :	if( activeObject[0] > 0) return false;
						for( int x = 1; x<maxRows-1; x++) activeObject[x] = activeObject[x+1]; 
						activeObject[maxRows-1] = 0;
						notifyActionEvent();
						return true;
			case right: // alles analog nur mit veraenderter Richtung
						if( activeObject[maxRows-1] > 0) return false;
						for( int x = maxRows-1; x>0; x--) activeObject[x] = activeObject[x-1]; 
						activeObject[0] = 0;
						notifyActionEvent();
						return true;
			case accept: 
						acceptActiveObject();
						notifyActionEvent();
						return true;
						
			case reject: 
						rejectActiveObject();
						notifyActionEvent();
						return true;
						
			}
	}
	return false;
}
/**
 * Alle Bewegungen der aktiven Objekte sind in dieser Methode implementiert.
 */
public boolean moveObjectLeft() {
	if (activeObjectState) {
		if( activeObject[0] > 0) return testResouces();
		for( int x = 0; x<maxCols-1; x++) activeObject[x] = activeObject[x+1]; 
		activeObject[maxCols-1] = 0;
		notifyActionEvent();
	}
	return testResouces();
}
/**
 * Alle Bewegungen der aktiven Objekte sind in dieser Methode implementiert.
 */
public boolean moveObjectRight() {
	if (activeObjectState) {
		if( activeObject[maxCols-1] > 0) return testResouces();
		for( int x = maxCols-1; x>0; x--) activeObject[x] = activeObject[x-1]; 
		activeObject[0] = 0;
		notifyActionEvent();
	}
	return testResouces();
}
public void newGame() {
	for( int i=0; i<maxRows; i++)
		for( int j=0; j<maxCols; j++) space[j][i] = 0;		
	profit 		= 0;
	activeObjectState = false;
	step		= 0;
	activateObject();
	notifyActionEvent();
}
public void notifyActionEvent () {

	java.util.Vector v;
	synchronized ( this ) {
		v = (java.util.Vector) actionListeners.clone();
	}
	int cnt = v.size();
	for ( int i=0; i<cnt; i++) ((ActionListener) v.elementAt(i)).actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "Hello"));
}
/**
 * Setzt das aktive Objekt zurück. Damit kann das Nächste erscheinen.
 */
public boolean rejectActiveObject() {
	activeObjectState = false;
	boolean r = activateObject();
	objectNumber++;
	notifyActionEvent();
	return r;
}
	public void removeActionListener( ActionListener a) {
		if ( actionListeners.contains(a) ) actionListeners.removeElement(a);
	}
/**
 * Die Beschreibung der Methode hier eingeben.
 * Erstellungsdatum: (10.12.2002 21:32:41)
 * @param newLimitSteps boolean
 */
public void setLimitSteps(boolean newLimitSteps) {
	limitSteps = newLimitSteps;
	if( step == maxStep && !activeObjectState) activateObject();
	else notifyActionEvent();
}
private boolean testResouces() {
	for( int x=0; x<maxCols; x++) if( activeObject[x] > 0) {
		if( space[x][maxRows-1] > 0) return false;
		int y = 0;
		while( space[x][y] != 0) y++;
		y += activeObject[x];
		if( y > maxRows) return false;
	}
	return true;
}
}
