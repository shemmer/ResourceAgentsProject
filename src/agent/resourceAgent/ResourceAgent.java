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
	protected HashMap<Resource ,Double> baselineCostsPerRes;
	//Current remaining capacity 
	protected HashMap<Resource, Integer> capacityMap;
	//Current offer
	protected double profit;
	//Current offer map
	protected HashMap<Resource, Byte> offer;
	protected Element requestElement;
	protected boolean alreadyAnswered = false;

	protected byte step;
	protected byte maxstep;	
	/**
	 * Overloading setup to also execute the Registration of the agent with the DF
	 */
	protected void setup(){
		super.setup();
		root= doc.createElement(this.getLocalName());
		doc.appendChild(root);
		path= new File("./log/" + this.getLocalName()+"_hist.xml").getAbsolutePath();
		Object[] args = getArguments();
		capacityMap=new HashMap<Resource,Integer>();
		this.service = new String[args.length-1];
		this.serviceName ="Resource";
		baselineCostsPerRes = new HashMap<Resource, Double>();
		for(int i =1; i<args.length; i++){
			this.baselineCostsPerRes.put(Resource.valueOf(args[i].toString()), Double.valueOf((String) args[0]));
			capacityMap.put(Resource.valueOf(args[i].toString()), 6);
			this.service[i-1] = args[i].toString();
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
//					System.err.println(this.myAgent.getLocalName()  + " :: REQUEST");
					try {
						offer = (HashMap<Resource, Byte>) msg.getContentObject();
					} catch (UnreadableException e) {
						System.err.println("Error deserializing offer object");
						e.printStackTrace();
					}
					//Get step
					if(offer.containsKey(Resource.DUMMY)){
						step = offer.get(Resource.DUMMY);
						maxstep = offer.get(Resource.DUMMY_MAX);
						offer.remove(Resource.DUMMY);
						offer.remove(Resource.DUMMY_MAX);
					}
					
					
					requestElement= doc.createElement("request");
					root.appendChild(requestElement);
					boolean enough =false;
					HashSet<Resource> res = new HashSet<Resource>();
			
					for(Resource resType : offer.keySet()){
						//IF capacity map does not contain a resource type of the offer -> skip
						if(!capacityMap.containsKey(resType)){
							continue;
						}
						if(capacityMap.get(resType) - offer.get(resType) >=0){
							enough=true;
						}else{
							res.add(resType);
							enough=false;
							break;
						}
					}
//					System.out.println("#########" + this.myAgent.getLocalName() + " "  + count);
//					System.out.println(this.myAgent.getLocalName() + enough +" enough already: " + alreadyAnswered);
//					System.out.println(this.myAgent.getLocalName() + "  " + capacityMap);
					if(enough){
						if(!alreadyAnswered){
							this.myAgent.addBehaviour(new CalculatingBehaviour(this.myAgent, msg.getSender()));
							alreadyAnswered=true;
						}
					}else{
						if(!alreadyAnswered){
//							System.out.println(this.myAgent.getLocalName()+ " REF");
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
					//Reserving capacity for the current
					try {
//						System.err.println(this.myAgent.getLocalName()  + " ACCEPT");
						SimpleEntry<Resource, Double> pair = 
								(SimpleEntry<Resource, Double>) msg.getContentObject();
						this.myAgent.addBehaviour(new ReservingBehaviour(this.myAgent,pair));
						
						//XML Stuff
						NodeList requestNodes = requestElement.getElementsByTagName(pair.getKey().toString());
						Element resElement = (Element) requestNodes.item(0);
						if(resElement!= null){
							resElement.setAttribute("accepted","true");
						}
						writeHistoryToXML();
						alreadyAnswered=false;
		
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
				if(ACLMessage.REJECT_PROPOSAL==msg.getPerformative()){
					System.out.println("Agent " + this.myAgent.getLocalName() +
												" COST_REJECT");
//					Element reject = doc.createElement("REJECTED");
//					requestElement.appendChild(reject);
					alreadyAnswered=false;
					writeHistoryToXML();
					
				}
				//"Cancel" behaviour i.e. the service aggregator aborts the current offer because
				//one of the resource does not have any agents with capacity left
				if(ACLMessage.CANCEL== msg.getPerformative()){
					//					System.out.println("Agent " + this.myAgent.getLocalName() +
					//							" : CANCEL " );
					offer=null;
					alreadyAnswered=false;
				}
				//Reset Behaviour
				if(ACLMessage.PROPAGATE == msg.getPerformative()){
					for(Resource r : capacityMap.keySet()){
						capacityMap.put(r, 6);
					}
					for(Resource r : baselineCostsPerRes.keySet()){
						baselineCostsPerRes.put(r, 6.0);
					}
					alreadyAnswered= false;
					offer = null;
				}
				if(ACLMessage.INFORM== msg.getPerformative()){
					//					System.out.println("Agent " + this.myAgent.getLocalName() +
					//							" : CANCEL " );
					ACLMessage reply = new ACLMessage(ACLMessage.INFORM_REF);
					reply.setSender(this.myAgent.getAID());
					reply.setLanguage("Java");
					SimpleEntry<String, Double> pair = 
							new SimpleEntry<String, Double>(myAgent.getLocalName(), profit);
					try {
						reply.setContentObject(pair);
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

	}
	/**
	 * Calculating Behaviour
	 */
	private class CalculatingBehaviour extends SimpleBehaviour {
		private boolean finished = false;
		private AID serviceAgg;
		double totalCost;
		public CalculatingBehaviour(Agent me, AID serviceAgg)
		{
			this.myAgent=me;
			this.serviceAgg= serviceAgg;
		}
		@Override
		public void action() {
			HashMap<Resource, Double> local_resourceCostMap = new 
					HashMap<Resource, Double>();
			//Is bottleneck?
			boolean bottleneck =false;
			byte tmpValue=0;
			for(Resource r : offer.keySet()){
				if(offer.get(r)>tmpValue){
					tmpValue = offer.get(r);
				} 
			}//tmpValue = highest requested capacity
			int a =0;
			for(Resource r1: offer.keySet()){
				if(capacityMap.containsKey(r1) && offer.get(r1)==tmpValue){
					bottleneck=true;
					a++;
				}
			}
			if(a== offer.size()) bottleneck = false;
			//TODO History Information
			//Iterating through all resources that are in the capacityMap
			for(Resource r : capacityMap.keySet()){
				//Skipping every resource, that is not in the offer
				if(!offer.containsKey(r))continue;
				//Stored costs from the last transaction
				double calculatedCosts = baselineCostsPerRes.get(r);
				//Reading history information
				Element docElement = doc.getDocumentElement();
				NodeList requestNodes = docElement.getElementsByTagName("request");
				//Opportunity costs of past 
				double oppCost =0;
				int won=0, lost=0;
				for(int i=0; i<requestNodes.getLength(); i++){
					Element requestElement = (Element) requestNodes.item(i);
					NodeList resourceNodes = requestElement.getChildNodes();
					for(int j = 0; j < resourceNodes.getLength(); j++){
						Element resElement = (Element) resourceNodes.item(j);
						if(resElement.getTagName().equals(r.toString())){
							double requestCost = Double.parseDouble(resElement.getAttributeNode("cost").getValue());
							double requestSize = Double.parseDouble(resElement.getAttributeNode("size").getValue());
//							(i/resourceNodes.getLength());
							if(resElement.hasAttribute("accepted")){
								won++;
								if(requestCost <= calculatedCosts) {
									//For each accepted offer where the current costs are lower or equal
									//to the cost in the accepted offer
									//i/resourceNodes.getLength -> Bewertung der Kosten je nachdem wie weit sie in der
									//Vergangenheit liegen
//									oppCost += (i/resourceNodes.getLength()) * ;
								}else{
									
								}
							}else{
								lost++;
								// Request was not accepted
								oppCost +=   requestCost * requestSize;
								if(requestCost >= calculatedCosts){ 
									
								}else{
									
								}
							}
						}
					}
				}
				
				System.err.println(this.myAgent.getLocalName() + " : " +oppCost);
				
				if(won != 0 || lost !=0){
					double winLossRatio= won / (won+lost);
					if(winLossRatio > 0.8){
						baselineCostsPerRes.put(r, calculatedCosts+1);
					}else{
						if(winLossRatio < 0.3)
							baselineCostsPerRes.put(r, calculatedCosts-1);
					}
				}
//				baselineCostsPerRes.put(r, currCost);
				byte quantity = offer.get(r);
//				if(bottleneck){
//					calculatedCosts = 2* calculatedCosts;
//				}
				//TODO Remaining capacity information
				capacityMap.get(r);
				
				//TODO Remaining steps
				double remainingStepRatio = 0.0;
				remainingStepRatio = step/maxstep;
				if(remainingStepRatio<0.5){
					calculatedCosts *=0.75;
				}
				
				//TODO compute the total cost
				double totalCost = quantity * calculatedCosts;
				local_resourceCostMap.put(r, totalCost);
				//XML Stuff
				Element resElement = doc.createElement(r.toString());
				resElement.setAttribute("size" ,offer.get(r).toString());
				resElement.setAttribute("capacity", capacityMap.get(r).toString());
				resElement.setAttribute("cost", Double.toString(local_resourceCostMap.get(r)));
				requestElement.appendChild(resElement);
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
//			System.out.println("------------Reserving " + myAgent.getLocalName() + currCost.getKey());
			capacityMap.put(currCost.getKey(), capacityMap.get(currCost.getKey()) - offer.get(currCost.getKey()));
			profit = profit + currCost.getValue();
			System.err.println("#### Profit: " + this.myAgent.getLocalName() +" ; " + profit);
			this.finished = true;
		}
		@Override
		public boolean done() {
			return finished;
		}
	}
}
