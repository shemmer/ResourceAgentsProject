package agent.resourceAgent;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import agent.AbstractAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import offer.Resource;

public class ResourceAgent extends AbstractAgent{
	//Current costs per res unit
	protected HashMap<Resource ,Double> currentBaselineCostsPerRes;
	//Current remaining capacity 
	protected HashMap<Resource, Integer> currentCapacityMap;
	
	//Initial baseLineCosts
	protected HashMap<Resource, Double> baselineCostsPerRes;
	//Initial Capacity
	protected HashMap<Resource, Integer> initialCapacityMap;
	//Current offer
	protected double profit;
	//Current offer map
	protected HashMap<Resource, Byte> offer;
	
	protected Element requestElement;
	protected boolean alreadyAnswered = false;
	protected byte step;
	protected byte maxstep;	
	protected byte id;
	/**
	 * Overloading setup to also execute the Registration of the agent with the DF
	 */
	protected void setup(){
		super.setup();
		root= doc.createElement(this.getLocalName());
		doc.appendChild(root);
		logFile = new File("./"+ this.getLocalName()+"_hist.xml");
		path= logFile.getAbsolutePath();
		Object[] args = getArguments();
		currentCapacityMap=new HashMap<Resource,Integer>();
		initialCapacityMap = new HashMap<Resource, Integer>();
		baselineCostsPerRes = new HashMap<Resource, Double>();
		this.service = new String[args.length];
		this.serviceName ="Resource";
		currentBaselineCostsPerRes = new HashMap<Resource, Double>();
		for(int i =0; i<args.length; i++){	
			String[] argument= args[i].toString().split("<<");		
			currentBaselineCostsPerRes.put(Resource.valueOf(argument[0]), Double.valueOf(argument[1]));
			currentCapacityMap.put(Resource.valueOf(argument[0]), Integer.valueOf(argument[2]));
			initialCapacityMap.put(Resource.valueOf(argument[0]), Integer.valueOf(argument[2]));
			baselineCostsPerRes.put(Resource.valueOf(argument[0]), Double.valueOf(argument[2]));
			this.service[i] = argument[0];
		}
		
		this.registerAtDF();
		
		addBehaviour(new ResourceReceiverBehaviour(this));
	}

	
	
	/**
	 * Receiver Behavior
	 */
	private class ResourceReceiverBehaviour extends CyclicBehaviour {
		public ResourceReceiverBehaviour(Agent a){
			this.myAgent = (ResourceAgent) a;
		}

		@Override
		public void action() {
			ACLMessage msg = this.myAgent.receive();
			if(msg==null){
				block(1000);
			}else{
				//New Offer
				if(ACLMessage.REQUEST == msg.getPerformative()){
					try {
						offer = (HashMap<Resource, Byte>) msg.getContentObject();
					} catch (UnreadableException e) {
						System.err.println("Error deserializing offer object");
						e.printStackTrace();
					}
					//Get step
					if(offer.containsKey(Resource.DUMMY)&&offer.containsKey(Resource.DUMMY_MAX)){
						step = offer.get(Resource.DUMMY);
						maxstep = offer.get(Resource.DUMMY_MAX);
						id= offer.get(Resource.ID);
						offer.remove(Resource.DUMMY);
						offer.remove(Resource.DUMMY_MAX);
						offer.remove(Resource.ID);
					}
					
					boolean enough =false;
					HashSet<Resource> res = new HashSet<Resource>();
					for(Resource resType : offer.keySet()){
						//IF capacity map does not contain a resource type of the offer -> skip
						if(!currentCapacityMap.containsKey(resType)){
							continue;
						}
						if(currentCapacityMap.get(resType) - offer.get(resType) >=0){
							enough=true;
							res.add(resType);
						}else{
							enough=false;
							break;
						}
					}
					if(enough){
						if(!alreadyAnswered){
							this.myAgent.addBehaviour(new CalculatingBehaviour(this.myAgent, msg.getSender()));
//							this.myAgent.addBehaviour(new CalcDividableBehaviour(this.myAgent, msg.getSender()));
							alreadyAnswered=true;
						}
					}else{
						if(!alreadyAnswered){
								ACLMessage reply = new ACLMessage(ACLMessage.REFUSE);
								reply.setSender(this.myAgent.getAID());
								reply.setLanguage("Java");
								try {
									reply.setContentObject(res);
								} catch (IOException e) {
								    e.printStackTrace();
								}
								reply.setOntology("");
								reply.addReceiver(msg.getSender());
								this.myAgent.send(reply);
								alreadyAnswered=true;
						}
					}
				} 
				if(ACLMessage.ACCEPT_PROPOSAL == msg.getPerformative()){
					root.appendChild(requestElement);
					//Reserving capacity for the current
					try {
						SimpleEntry<Resource, Double> pair = 
								(SimpleEntry<Resource, Double>) msg.getContentObject();
						this.myAgent.addBehaviour(new ReservingBehaviour(this.myAgent,pair));						
						//XML storing accepting of accepting
						requestElement.setAttribute("accepted", "true");
						writeHistoryToXML();
						alreadyAnswered=false;
		
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
				if(ACLMessage.REJECT_PROPOSAL==msg.getPerformative()){
					root.appendChild(requestElement);
					alreadyAnswered=false;
					writeHistoryToXML();
					
				}
				//"Cancel" behaviour i.e. the service aggregator aborts the current offer because
				//one of the resource does not have any agents with capacity left
				if(ACLMessage.CANCEL== msg.getPerformative()){
					requestElement.setAttribute("canceled","true");
					root.appendChild(requestElement);
					writeHistoryToXML();
					offer=null;
					alreadyAnswered=false;
				}
				//Reset Behaviour
				if(ACLMessage.PROPAGATE == msg.getPerformative()){
					for(Resource r : currentCapacityMap.keySet()){
						currentCapacityMap.put(r, initialCapacityMap.get(r));
					}
					for(Resource r : currentBaselineCostsPerRes.keySet()){
						currentBaselineCostsPerRes.put(r, baselineCostsPerRes.get(r));
					}
					alreadyAnswered= false;
					profit=0;
					offer = null;
					doc= db.newDocument();
					root = doc.createElement(this.myAgent.getLocalName());
					doc.appendChild(root);
				}
				if(ACLMessage.INFORM== msg.getPerformative()){
					Element docElement = doc.getDocumentElement();
					NodeList requestNodes = docElement.getElementsByTagName("request");
					double won=0, lost=0;
					for(int i=0; i<requestNodes.getLength(); i++){
						Element requestElement = (Element) requestNodes.item(i);
						if(requestElement.hasAttribute("canceled")) break;
						if(requestElement.hasAttribute("accepted")) {
							won++;
						}else{
							lost++;
						}
					}
					ACLMessage reply = new ACLMessage(ACLMessage.INFORM_REF);
					reply.setSender(this.myAgent.getAID());
					reply.setLanguage("Java");
					reply.setContent(currentCapacityMap + "<<" + initialCapacityMap +
							" << " + profit + " << " + currentBaselineCostsPerRes +" << " + baselineCostsPerRes+"<<"+  +won+ " << " +lost);
					reply.setOntology("");
					reply.addReceiver(msg.getSender());
					this.myAgent.send(reply);
					alreadyAnswered=true;
				}			
			}
		}

	}
	/**
	 * Calculating Behaviour
	 */
	private class CalculatingBehaviour extends SimpleBehaviour {
		private boolean finished = false;
		private AID serviceAgg;

		public CalculatingBehaviour(Agent me, AID serviceAgg)
		{
			this.myAgent=me;
			this.serviceAgg= serviceAgg;
		}
		@Override
		public void action() {
			requestElement= doc.createElement("request");
			requestElement.setAttribute("id", String.valueOf(id));
			HashMap<Resource, Double> local_resourceCostMap = new 
					HashMap<Resource, Double>();
			//Is there a bottleneck?
			boolean bottleneck =false;
			byte highestRequestedCapacity=0;
			for(Resource r : offer.keySet()){
				if(offer.get(r)>highestRequestedCapacity){
					highestRequestedCapacity = offer.get(r);
				} 
			}//tmpValue = highest requested capacity
			int a =0;
			for(Resource r1: offer.keySet()){
				if(offer.get(r1)==highestRequestedCapacity){
					bottleneck=true;
					a++;
				}
			}
			if(a>1) bottleneck = false;
			//Iterating through all resources that are in the capacityMap
			for(Resource r : currentCapacityMap.keySet()){
				//Skipping every resource, that is not in the offer
				if(!offer.containsKey(r))continue;
				//Stored baseline costs
				double calculatedCosts = currentBaselineCostsPerRes.get(r);
				//Reading history information
				Element docElement = doc.getDocumentElement();
				NodeList requestNodes = docElement.getElementsByTagName("request");
				double won=0, lost=0;
				for(int i=0; i<requestNodes.getLength(); i++){
					Element requestElement = (Element) requestNodes.item(i);
					NodeList resourceNodes = requestElement.getChildNodes();
					for(int j = 0; j < resourceNodes.getLength(); j++){
						Element resElement = (Element) resourceNodes.item(j);
						if(resElement.getTagName().equals(r.toString())){
							if(requestElement.hasAttribute("canceled")) break;
							if(requestElement.hasAttribute("accepted")){
								won+=1;
							}else{
								lost+=1;
							}
						}
					}
				}	
				//Percentage based calculation!
				if(won != 0 || lost !=0){
					double winRatio= won / (won+lost);
					if(winRatio > 0.8){
						currentBaselineCostsPerRes.put(r, currentBaselineCostsPerRes.get(r)*1.2);
					}else{
						if(winRatio < 0.25)
							currentBaselineCostsPerRes.put(r, currentBaselineCostsPerRes.get(r)*0.9);
					}
				}
				calculatedCosts= currentBaselineCostsPerRes.get(r);
				
				if(bottleneck){
					if(offer.get(r)==highestRequestedCapacity) calculatedCosts = 1.25* calculatedCosts;
				}
				
				double remainingStepRatio = 0.0;
				remainingStepRatio = (double)step/(double)maxstep;
				//Gradually increase lowering costs
				if(remainingStepRatio>=0.8){
					calculatedCosts =calculatedCosts * (1- (double) currentCapacityMap.get(r)/initialCapacityMap.get(r));
				}else{
					//Remaining capacity information
					if((double) currentCapacityMap.get(r)/initialCapacityMap.get(r)< 0.5){
						calculatedCosts = calculatedCosts * (2- (double) currentCapacityMap.get(r)/initialCapacityMap.get(r));
					}	
				}
				byte quantity = offer.get(r);
				double totalCost = calculatedCosts>1?quantity * calculatedCosts:quantity*1;
				local_resourceCostMap.put(r, totalCost);
				//XML Storage of request information
				Element resElement = doc.createElement(r.toString());
				resElement.setAttribute("size" ,offer.get(r).toString());
				resElement.setAttribute("capacity", currentCapacityMap.get(r).toString());
				resElement.setAttribute("cost", Double.toString(local_resourceCostMap.get(r)));
				requestElement.appendChild(resElement);
			}
			//Multiple resources to offer
			if(currentCapacityMap.size()>1){
				//TODO include a condition like winRatio <0.5 as this indicates competition
				if(local_resourceCostMap.size()==currentCapacityMap.size() ){
					for(Resource r : local_resourceCostMap.keySet()){
						local_resourceCostMap.put(r, local_resourceCostMap.get(r)*0.9);
					}
				}else{
					for(Resource r : local_resourceCostMap.keySet()){
						local_resourceCostMap.put(r, local_resourceCostMap.get(r)*(1+ (1- (double) local_resourceCostMap.size()/currentCapacityMap.size())));
					}
				}
			}
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			msg.setSender(this.myAgent.getAID());
			msg.setLanguage("Java");
			try {
				msg.setContentObject(local_resourceCostMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
			msg.setOntology("");
			msg.addReceiver(serviceAgg);
			this.myAgent.send(msg);
			this.finished=true;
		}
		@Override
		public boolean done() {
			return finished;
		}
	}

	
	
	/**
	 * Calculating Behaviour for dividable resources
	 */
	private class CalcDividableBehaviour extends SimpleBehaviour {
		private boolean finished = false;
		private AID serviceAgg;

		public CalcDividableBehaviour(Agent me, AID serviceAgg)
		{
			this.myAgent=me;
			this.serviceAgg= serviceAgg;
		}
		@Override
		public void action() {

			//TODO: 175% -> 150% 1 + (1-requested/total# of resources)
			requestElement= doc.createElement("request");
			requestElement.setAttribute("id", String.valueOf(id));
			HashMap<Resource, Double> currentCalcBaseCostMap = new 
					HashMap<Resource, Double>();
			//Is there a bottleneck?
			boolean bottleneck =false;
			byte highestRequestedCapacity=0;
			for(Resource r : offer.keySet()){
				if(offer.get(r)>highestRequestedCapacity){
					highestRequestedCapacity = offer.get(r);
				} 
			}//tmpValue = highest requested capacity
			int a =0;
			for(Resource r1: offer.keySet()){
				if(offer.get(r1)==highestRequestedCapacity){
					bottleneck=true;
					a++;
				}
			}
			if(a>1) bottleneck = false;
			//Iterating through all resources that are in the capacityMap
			for(Resource r : currentCapacityMap.keySet()){
				//Skipping every resource, that is not in the offer
				if(!offer.containsKey(r)) continue;
				//Stored baseline costs
				double calculatedCosts = currentBaselineCostsPerRes.get(r);
				//Reading history information
				Element docElement = doc.getDocumentElement();
				NodeList requestNodes = docElement.getElementsByTagName("request");
				double won=0, lost=0;
				for(int i=0; i<requestNodes.getLength(); i++){
					Element requestElement = (Element) requestNodes.item(i);
					NodeList resourceNodes = requestElement.getChildNodes();
					for(int j = 0; j < resourceNodes.getLength(); j++){
						Element resElement = (Element) resourceNodes.item(j);
						if(resElement.getTagName().equals(r.toString())){
							if(requestElement.hasAttribute("canceled")) break;
							if(requestElement.hasAttribute("accepted")){
								won+=1;
							}else{
								lost+=1;
							}
						}
					}
				}	
				//Percentage based calculation!
				if(won != 0 || lost !=0){
					double winRatio= won / (won+lost);
					if(winRatio > 0.8){
						currentBaselineCostsPerRes.put(r, currentBaselineCostsPerRes.get(r)*1.2);
					}else{
						if(winRatio < 0.1)
							currentBaselineCostsPerRes.put(r, currentBaselineCostsPerRes.get(r)*0.9);
					}
				}
				calculatedCosts= currentBaselineCostsPerRes.get(r);
				if(bottleneck){
					if(offer.get(r)==highestRequestedCapacity) calculatedCosts *= 1.25;
				}
				
				double remainingStepRatio = 0.0;
				remainingStepRatio = (double)step/(double)maxstep;
				//Gradually increase lowering costs
				if(remainingStepRatio>=0.8 ){
					calculatedCosts *=0.75;
				}else{
					//Remaining capacity information
					if(currentCapacityMap.get(r)/6< 0.5){
						calculatedCosts = calculatedCosts * (2- currentCapacityMap.get(r)/6);
					}
				}
					
//				byte quantity = offer.get(r);
//				double totalCost = quantity * calculatedCosts;
				currentCalcBaseCostMap.put(r, calculatedCosts);
				//XML Stuff
				Element resElement = doc.createElement(r.toString());
				resElement.setAttribute("size" ,offer.get(r).toString());
				resElement.setAttribute("capacity", currentCapacityMap.get(r).toString());
				resElement.setAttribute("cost", Double.toString(currentCalcBaseCostMap.get(r)));
				requestElement.appendChild(resElement);
			}
			HashMap<Resource,HashMap<Integer,Double>> reqReply= new HashMap<Resource, HashMap<Integer,Double>>();
			for(Resource r : currentCalcBaseCostMap.keySet()){
				int quantity = offer.get(r);
				HashMap<Integer, Double> map = new HashMap<Integer, Double>();
				for(int i = quantity; i> 0; i--){
					map.put(i, currentCalcBaseCostMap.get(r)*(1.0+ (1.0- ((double) i/quantity))));
				}
				reqReply.put(r, map);
			}
			//Multiple resources to offer
			if(currentCapacityMap.size()>1){
				if(currentCalcBaseCostMap.size()==currentCapacityMap.size()){
					for(Resource r : currentCalcBaseCostMap.keySet()){
						currentCalcBaseCostMap.put(r, currentCalcBaseCostMap.get(r)*0.9);
					}
				}else{
					for(Resource r : currentCalcBaseCostMap.keySet()){
						currentCalcBaseCostMap.put(r, currentCalcBaseCostMap.get(r)*(1+ (1-(double) currentCalcBaseCostMap.size()/currentCapacityMap.size())));
					}
				}
			}
//			System.out.println(this.myAgent.getLocalName()+":"+reqReply);
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			msg.setSender(this.myAgent.getAID());
			msg.setLanguage("Java");
			try {
				msg.setContentObject(reqReply);
			} catch (IOException e) {
				e.printStackTrace();
			}	
			msg.setOntology("");
			msg.addReceiver(serviceAgg);
//			this.myAgent.send(msg);
			this.finished=true;
		}
		@Override
		public boolean done() {
			return finished;
		}
	}
	/**
	 * Reserving Behavior
	 */
	private class ReservingBehaviour extends SimpleBehaviour {
		private boolean finished = false;
		private SimpleEntry<Resource, Double> currCost;
		public ReservingBehaviour(Agent me, SimpleEntry<Resource, Double> simpleEntry){
			super(me);
			this.currCost = simpleEntry;
		}
		@Override
		public void action() {
			currentCapacityMap.put(currCost.getKey(),
					currentCapacityMap.get(currCost.getKey()) - offer.get(currCost.getKey()));
			profit = profit + currCost.getValue();
			this.finished = true;
		}
		@Override
		public boolean done() {
			return finished;
		}
	}
}
