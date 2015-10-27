package offer;

import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import jade.core.AID;
import offer.Resource;


public class Offer implements Serializable {

	private boolean state;
	private byte shape[];
	private int income;
	private byte shapeID;
	private int unitPrice;
	public int getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(int unitPrice) {
		this.unitPrice = unitPrice;
	}
	private int ID;

	private HashMap<Resource, Byte> activeObjectMap;
	//Current lowest costs
	private HashMap<Resource, Double> bestCost;
	//Agents contacted for a set of resources
	private HashMap<AID, HashSet<Resource>> contactedAgents;
	//Proposal by all agents
	private HashMap<AID,HashMap<Resource, Double>> agentCostsMap;
	
	//Flag marking an offer as rejected
	private boolean rejected = false;
	
	//Current aggregated costs
	private double aggCost;

	public Offer(int offer_id, int unitPrice, byte shadeID, byte[] shape) {
		this.state = false;
		  this.shape = shape;
		  this.shapeID = shadeID;
		  this.unitPrice = unitPrice;
		  for(int i=0; i< shape.length; i++) this.income += unitPrice;
		  
		  this.rejected=false;
		  this.bestCost = new HashMap<Resource, Double>();
		  this.aggCost = 0;
		  this.setContactedAgents(new HashMap<AID, HashSet<Resource>>());
		  ID = offer_id;
	}
	
	public Offer(Offer of){
		  state = of.getActiveObjectState();
		  shape = of.getActiveObject();
		  income = of.getActiveObjectIncome();
		  shapeID = of.getActiveObjectID();
		  unitPrice = of.getUnitPrice();
		  this.rejected = of.isRejected();
		  this.bestCost = (HashMap<Resource, Double>) of.getBestCost().clone();
		  this.contactedAgents = (HashMap<AID, HashSet<Resource>>) of.getContactedAgents().clone();
		  this.ID = of.getID();
	}
	
	public HashMap<Resource, Byte> getActiveObjectMap() {
		return activeObjectMap;
	}
	public void setActiveObjectMap(HashMap<Resource, Byte> activeObjectMap) {
		this.activeObjectMap = activeObjectMap;
	}
	public byte[] getActiveObject() { return shape; }
	public byte getActiveObjectID() { return shapeID; }
	public int getActiveObjectIncome() { return income; }
	public int getActiveObjectPriceTag() { return unitPrice;}
	public boolean getActiveObjectState() { return state; }
	
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
	public boolean isRejected() {
		return rejected;
	}
	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}
}
