package offer;


import java.util.HashMap;
import java.util.HashSet;
import jade.core.AID;
import offer.Resource;


public class OfferFactory {

	//"Memory" of the past objects -> each cell contains a activeObjectID or 0
	public byte[][] history = new byte[maxCols][maxRows];

	private boolean state = false;
	private byte shape[] = null;
	private int income = 1;
	private byte shapeId = 0;
	private int unitPrice = 1;
	private int ID;
	java.util.Random random = new java.util.Random();

	private HashMap<Resource, Byte> activeObjectMap;
	
	private int objectNumber = 0;	
	public static int maxRows = 18, maxCols = 12;
	
	//Current lowest costs
	private HashMap<Resource, Double> bestCost;
	//Agents contacted for a set of resources
	private HashMap<AID, HashSet<Resource>> contactedAgents;
	//Proposal by all agents
	private HashMap<AID,HashMap<Resource, Double>> agentCostsMap;
	
	
	
	private HashMap<Resource, HashMap<Integer, HashMap<AID, Double>>> agentCostsDivMap;
	
	//Flag marking an object as rejected
	private boolean rejected = false;
	
	//Current aggregated costs
	private double aggCost;

	private double incomeTotal=0;
	
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
	private int turnOver = 0;

	private java.text.DecimalFormat df = new java.text.DecimalFormat();

	private int step = 0, maxStep = 16;
	
	private int profit=0;
	
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
		for( int x=0; x<maxCols; x++) if( shape[x] > 0) {
			int y = 0;
			while( history[x][y] != 0) y++;
			while( shape[x] > 0) {
				history[x][y] = shapeId;
				y++; shape[x]--;
			}
		}
		turnOver += income;
		state = false;
		objectNumber++;
		return true;
	}
	/**
	 * Ein Objekt mit der Parameterbezeichnung wird in unser Feld eingefuegt.
	 */
	public boolean activateObject() {
		this.rejected=false;
		this.bestCost = new HashMap<Resource, Double>();
		this.aggCost = 0;
		this.setContactedAgents(new HashMap<AID, HashSet<Resource>>());
		ID = random.nextInt(100);
		if( limitSteps && step>=maxStep) return false;
		if( state) System.out.println("Error at activeObject()");
		else {
			this.activeObjectMap = new HashMap<Resource,Byte>();
			while( 0 == (shapeId = (byte)random.nextInt(thisObject.length))) {};
			shape = new byte[ maxCols];
			for( int i=0; i<maxCols; i++) 
			{
				//insert the contents of the two-dimensional arrays second dimension into active object at
				//activeobjectid
				shape[i] = thisObject[ shapeId][i];
				if(thisObject[ shapeId][i]!=0){
					this.activeObjectMap.put(Resource.allValuesAsList().get(i), thisObject[ shapeId][i]);
				}
			}
			unitPrice = minPrice + random.nextInt( priceSpan);
			income = 0;
			for( int i=0; i<maxCols; i++) income += shape[i];
			income *= unitPrice;
			state = true;
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
	public byte[] getActiveObject() { return shape; }
	public String getActiveObjectDescription() {
		return objectNumber+"; "+income+"; "+unitPrice+"; "+shapeId;
	}
	public byte getShapeID() { return shapeId; }
	public int getActiveObjectIncome() { return income; }
	public int getActiveObjectPriceTag() { return unitPrice;}
	public boolean getActiveObjectState() { return state; }
	public String getFormatedActiveObjectPriceTag() { return df.format(((double)unitPrice));}
	public int getMaxPriceTag() { return minPrice+priceSpan+4; }
	public int getTurnOver() { return turnOver; }
	public void setStep(int step) {
		this.step = step;
	}
	public int getMaxStep() {
		return maxStep;
	}
	public void setMaxStep(int maxStep) {
		this.maxStep = maxStep;
	}
	public int getStep() {
		return step;
	}
	public boolean isLimitSteps() {
		return limitSteps;
	}	
	public void newGame() {
		for( int i=0; i<maxRows; i++)
			for( int j=0; j<maxCols; j++) history[j][i] = 0;		
		turnOver 		= 0;
		state = false;
		step		= 0;
		this.aggCost=0;
		this.unitPrice=1;
		this.income=1;
		this.rejected= false;
		this.incomeTotal = 0;
	}

	/**
	 * Setzt das aktive Objekt zurück. Damit kann das Nächste erscheinen.
	 */
	public boolean rejectActiveObject() {
		state = false;
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
		for( int x=0; x<maxCols; x++) if( shape[x] > 0) {
			if( history[x][maxRows-1] > 0) return false;
			int y = 0;
			while( history[x][y] != 0) y++;
			y += shape[x];
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
	public HashMap<AID, HashMap<Resource, Double>> getAgentCostsMap() {
		return agentCostsMap;
	}
	public void setAgentCostsMap(HashMap<AID, HashMap<Resource, Double>> agentCostsMap) {
		this.agentCostsMap = agentCostsMap;
	}
	public void putAgentCostsMap(AID id , HashMap<Resource, Double> agentCostsMap) {
		this.agentCostsMap.put(id, agentCostsMap);
	}
	
	public HashMap<Resource, HashMap<Integer, HashMap<AID, Double>>> getAgentCostsDivMap() {
		return agentCostsDivMap;
	}
	public void setAgentCostsDivMap(HashMap<Resource, HashMap<Integer, HashMap<AID, Double>>> agentCostsDivMap) {
		this.agentCostsDivMap = agentCostsDivMap;
	}
	public void putAgentCostsDivMap(Resource id , HashMap<Integer, HashMap<AID, Double>> agentCostsDivMap) {
		this.agentCostsDivMap.put(id, agentCostsDivMap);
	}
	
	public boolean isRejected() {
		return rejected;
	}
	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}
	public int getProfit() {
		return profit;
	}
	public void setProfit(int profit) {
		this.profit = profit;
	}
	public double getIncomeTotal() {
		return incomeTotal;
	}
	public void addIncomeTotal(double incomeTotal) {
		this.incomeTotal += incomeTotal;
	}
}
