package wendtris;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jade.core.AID;
import resourceAgent.Resource;


public class OfferFactory implements Serializable {
	public static final int left = 1, right = 2, accept = 3, reject = 4;

	//"Memory" of the past objects -> each cell contains a activeObjectID or 0
	public byte[][] space = new byte[maxCols][maxRows];

	private boolean activeObjectState = false;
	private byte activeObject[] = null;
	private int activeObjectIncome = 1;
	private byte activeObjectID = 0;
	private int activeObjectPriceTag = 1;
	private int ID;
	java.util.Random random = new java.util.Random();

	private HashMap<Resource, Byte> activeObjectMap;
	
	private int objectNumber = 0;	
	public static int maxRows = 12, maxCols = 12;
	
	//Current best costs
	private HashMap<Resource, Double> bestCost;
	//Current available agents for one resource
	private HashMap<Resource, Set<AID>> responsibleAgentsForRes;
	private HashMap<AID, HashSet<Resource>> contactedAgents;
	
	
	
	//Current aggregated costs
	private double aggCost;

	// Die Breite der Objectsspalten sollte maxCols entsprechen, sonst funktioniert hier nix!
	private static final byte[][] thisObject = 
		{ { 0,0,0,0,0,0,0,0,0,0,0,0 }, // dummy, da wir nicht mit objectID = 0 arbeiten können
				{ 0,0,0,0,1,2,3,2,1,0,0,0 },
				{ 0,0,0,0,1,1,1,1,0,0,0,0 },
				{ 0,0,0,0,0,2,2,0,0,0,0,0 }, 
				{ 0,0,0,0,2,2,1,1,0,0,0,0 },
				{ 0,0,0,0,1,3,2,2,1,0,0,0 },
				{ 0,0,0,0,2,1,1,1,3,3,0,0 } };

	private int minPrice = 1, priceSpan = 9; // thus maxPrice = 10;
	private int profit = 0;

	private java.text.DecimalFormat df = new java.text.DecimalFormat();

	private int step = 0, maxStep = 50;
	public void setStep(int step) {
		this.step = step;
	}
	public int getMaxStep() {
		return maxStep;
	}
	public void setMaxStep(int maxStep) {
		this.maxStep = maxStep;
	}
	private boolean limitSteps = true;
	public OfferFactory() {
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
		objectNumber++;
		return true;
	}
	/**
	 * Ein Objekt mit der Parameterbezeichnung wird in unser Feld eingefuegt.
	 */
	public boolean activateObject() {
		this.bestCost = new HashMap<Resource, Double>();
		this.aggCost = 0;
		this.setContactedAgents(new HashMap<AID, HashSet<Resource>>());
		ID = random.nextInt(100);
		if( limitSteps && step>=maxStep) return false;
		if( activeObjectState) System.out.println("Error at activeObject()");
		else {
			this.activeObjectMap = new HashMap<Resource,Byte>();
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
	/**
	 * 
	 * @return
	 */
	public HashMap<Resource, Byte> getActiveObjectMap() {
		return activeObjectMap;
	}
	/**
	 * 
	 * @param activeObjectMap
	 */
	public void setActiveObjectMap(HashMap<Resource, Byte> activeObjectMap) {
		this.activeObjectMap = activeObjectMap;
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

	public int getStep() {
		return step;
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
			case accept: 
				acceptActiveObject();
				return true;

			case reject: 
				rejectActiveObject();
				return true;

			}
		}
		return false;
	}
	
	public void newGame() {
		for( int i=0; i<maxRows; i++)
			for( int j=0; j<maxCols; j++) space[j][i] = 0;		
		profit 		= 0;
		activeObjectState = false;
		step		= 0;
	}

	/**
	 * Setzt das aktive Objekt zurück. Damit kann das Nächste erscheinen.
	 */
	public boolean rejectActiveObject() {
		activeObjectState = false;
		objectNumber++;
		return true;
	}
	/**
	 * Die Beschreibung der Methode hier eingeben.
	 * Erstellungsdatum: (10.12.2002 21:32:41)
	 * @param newLimitSteps boolean
	 */
	public void setLimitSteps(boolean newLimitSteps) {
		limitSteps = newLimitSteps;
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
	public HashMap<Resource, Double> getBestCost() {
		if(this.bestCost==null) bestCost = new HashMap<Resource, Double>();
		return bestCost;
	}
	public void setBestCost(HashMap<Resource, Double> bestCost) {
		this.bestCost = bestCost;
	}
	public void putBestCost(Resource key, double value){
		this.bestCost.put(key, value);
	}
	//TODO Rethink this! This is kinda bad design
	//Also the ServiceAggregator consistently fire the calculating behaviour to early
	//We do need a structure like that -> Informing agents of an abort? Do we really need to do that?
	//Disabling the button would suffice actually
	//Then we can just keep track of unique agents involved in offer proposal
//	public HashMap<Resource, Set<AID>> getResponsibleAgentsForRes() {
//		return responsibleAgentsForRes;
//	}
//	public void setResponsibleAgentsForRes(HashMap<Resource, Set<AID>> agentsResMap) {
//		this.responsibleAgentsForRes = agentsResMap;
//	}
//	public void addResponsibleAgentsForRes(Resource key, AID value){
//		if(responsibleAgentsForRes.containsKey(key)){
//			Set<AID> agents = this.responsibleAgentsForRes.get(key);
//			agents.add(value);
//		}else{
//			HashSet<AID> agents = new HashSet<AID>();
//			agents.add(value);
//			responsibleAgentsForRes.put(key, agents);
//		}
//	}
	
	public void setContactedAgents(HashMap<AID, HashSet<Resource>> map) {
		this.contactedAgents = map;
	}
	public HashMap<AID, HashSet<Resource>> getContactedAgents() {
		return contactedAgents;
	}
	public void addContactedAgents(AID contactedAgent, Resource res) {
		if(contactedAgents.containsKey(contactedAgent)){
			this.contactedAgents.get(contactedAgent).add(res);
		}else{
			HashSet<Resource> tmp = new HashSet<Resource>();
			tmp.add(res);
			this.contactedAgents.put(contactedAgent, tmp);
		}
	}
	
//	
	public double getAggCost() {
		return aggCost;
	}
	public void setAggCost(double aggCost) {
		this.aggCost = aggCost;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
}
