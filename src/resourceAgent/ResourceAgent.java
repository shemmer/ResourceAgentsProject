package resourceAgent;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class ResourceAgent extends AbstractAgent{
	//ServiceAggregatorAgent ID
	protected AID serviceAgg;
	//Current costs per res unit
	protected HashMap<Resource ,Double> cost;
	//Current remaining capacity 
	protected HashMap<Resource, Integer> capacityMap;
	//Current offer
	protected double profit;
	//Current offer map
	protected HashMap<Resource, Byte> offer;
	protected Element requestElement;
	
	
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
		cost = new HashMap<Resource, Double>();
		for(int i =1; i<args.length; i++){
			this.cost.put(Resource.valueOf(args[i].toString()), Double.valueOf((String) args[0]));
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
				if(ACLMessage.INFORM == msg.getPerformative()){
					try {
						offer = (HashMap<Resource, Byte>) msg.getContentObject();
					} catch (UnreadableException e) {
						System.err.println("Error deserializing offer object");
						e.printStackTrace();
					}
					requestElement= doc.createElement("request");
					root.appendChild(requestElement);
					for(Resource resType : capacityMap.keySet()){
						if(!offer.containsKey(resType))continue;
						//ResourceType handled by ResourceAgent and capacity sufficient
						if(offer.containsKey(resType) && capacityMap.get(resType) - offer.get(resType) >=0){						
							ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
							reply.addReceiver(msg.getSender());
							reply.setContent(resType.toString());
							reply.setLanguage("Java");
							reply.setOntology("");
							reply.setSender(this.myAgent.getAID());
							this.myAgent.send(reply);
							this.myAgent.addBehaviour(new CalculatingBehaviour(this.myAgent, msg.getSender()));
							break;
						}else{
							ACLMessage reply = new ACLMessage(ACLMessage.REFUSE);
							reply.setSender(this.myAgent.getAID());
							reply.setLanguage("Java");
							try {
								reply.setContentObject(resType);
							} catch (IOException e) {
								e.printStackTrace();
							}
							reply.setOntology("");
							reply.addReceiver(msg.getSender());
							this.myAgent.send(reply);
						}
					}
					
				}
				if(ACLMessage.ACCEPT_PROPOSAL == msg.getPerformative()){
					//Reserving capacity for the current
					try {
						System.err.println(this.myAgent.getLocalName()  + " ACCEPT");
						SimpleEntry<Resource, Double> pair = 
								(SimpleEntry<Resource, Double>) msg.getContentObject();
						this.myAgent.addBehaviour(new ReservingBehaviour(this.myAgent,pair));
						
						//XML Stuff
						NodeList requestNodes = requestElement.getElementsByTagName(pair.getKey().toString());
						Element resElement = (Element) requestNodes.item(0);
						resElement.setAttribute("accepted","true");
						writeHistoryToXML();
		
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
				if(ACLMessage.REJECT_PROPOSAL==msg.getPerformative()){
//					System.out.println("Agent " + this.myAgent.getLocalName() +
//												" COST_REJECT");
//					Element reject = doc.createElement("REJECTED");
//					requestElement.appendChild(reject);
					writeHistoryToXML();
					
					//TODO Offer should be set to null
				}
				//"Cancel" behaviour i.e. the service aggregator aborts the current offer because
				//one of the resource does not have any agents with capacity left
				if(ACLMessage.CANCEL== msg.getPerformative()){
					//					System.out.println("Agent " + this.myAgent.getLocalName() +
					//							" : CANCEL " );
					offer=null;
				}
				//Reset Behaviour
				if(ACLMessage.PROPAGATE == msg.getPerformative()){
					for(Resource r : capacityMap.keySet()){
						capacityMap.put(r, 6);
					}
					offer = null;
				}
			}
		}

	}
	/**
	 * Calculating Behavior
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
			//TODO Bottleneck -> Use requested capacity in relation to 
			//requested capacity of other resources to determine a cost
			boolean bottleneck =false;
			byte tmpValue=0;
			HashMap<Resource, Double> costMap = new 
					HashMap<Resource, Double>();
			for(Resource r : offer.keySet()){
				if(offer.get(r)>tmpValue){
					tmpValue = offer.get(r);
				} 
			}//tmpValue = highest requested capacity
			for(Resource r1: offer.keySet()){
				if(capacityMap.containsKey(r1) && offer.get(r1)==tmpValue) bottleneck=true;
			}
			for(Resource r : capacityMap.keySet()){
				byte quantity = offer.get(r);
				double currCost = 0.0;
				if(bottleneck){
					currCost = 2* quantity * cost.get(r);
				}else{
					currCost = quantity * cost.get(r);
				}
				costMap.put(r, currCost);

				//Reading history information
				Element docElement = doc.getDocumentElement();
				NodeList requestNodes = docElement.getElementsByTagName("request");
				for(int i=0; i<requestNodes.getLength(); i++){
					Element requestElement = (Element) requestNodes.item(i);
					NodeList resourceNodes = requestElement.getChildNodes();
					for(int j = 0; j < resourceNodes.getLength(); j++){
						Element resElement = (Element) resourceNodes.item(j);
						if(resElement.getTagName().equals(r.toString())){
							if(resElement.hasAttribute("accepted")){
								System.out.println(r + " accepted "+ resElement.getAttribute("cost"));
							}else{
								System.out.println(r + " not accepted " + resElement.getAttribute("cost"));
							}
						}
					}
				}
				
				//XML Stuff
				Element resElement = doc.createElement(r.toString());
				resElement.setTextContent(offer.get(r).toString());
				resElement.setAttribute("capacity", capacityMap.get(r).toString());
				resElement.setAttribute("cost", Double.toString(cost.get(r)));
				requestElement.appendChild(resElement);
			}
			
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			msg.setSender(this.myAgent.getAID());
			msg.setLanguage("Java");
			try {
				msg.setContentObject(costMap);
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
			capacityMap.put(currCost.getKey(), capacityMap.get(currCost.getKey()) - offer.get(currCost.getKey()));
			profit = profit + currCost.getValue();
//			System.err.println(this.myAgent.getLocalName() + ": Reserving "  + currCost.getKey() +" with an aggregated profit of" + profit);
			this.finished = true;
		}
		@Override
		public boolean done() {
			return finished;
		}
	}
}
