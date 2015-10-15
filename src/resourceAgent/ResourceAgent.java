package resourceAgent;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;

import org.w3c.dom.Element;

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
						if(!offer.containsKey(resType))break;
						//ResourceType handled by ResourceAgent and capacity sufficient
						if(offer.containsKey(resType) && capacityMap.get(resType) - offer.get(resType) >=0){
							ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
							reply.addReceiver(msg.getSender());
							reply.setContent(resType.toString());
							reply.setLanguage("Java");
							reply.setOntology("");
							reply.setSender(this.myAgent.getAID());
							this.myAgent.send(reply);
							this.myAgent.addBehaviour(new CalculatingBehaviour(this.myAgent, msg.getSender(),resType));
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
							this.myAgent.addBehaviour(new AbortBehaviour());
						}
					}
				}
				if(ACLMessage.ACCEPT_PROPOSAL == msg.getPerformative()){
					//XML Stuff
					Element accept = doc.createElement("ACCEPTED");
					requestElement.appendChild(accept);
					writeHistoryToXML();
					
					//Reserving capacity for the current
					try {
//						System.err.println(this.myAgent.getLocalName()  + " received accepted proposal " + (Resource) msg.getContentObject());
						this.myAgent.addBehaviour(new ReservingBehaviour(this.myAgent,
								(SimpleEntry<Resource, Double>) msg.getContentObject()));
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
				if(ACLMessage.REJECT_PROPOSAL==msg.getPerformative()){
//										System.out.println("Agent " + this.myAgent.getLocalName() +
//												" COST_REJECT");
					Element reject = doc.createElement("REJECTED");
					requestElement.appendChild(reject);
					writeHistoryToXML();
				}
				if(ACLMessage.CANCEL== msg.getPerformative()){
					//					System.out.println("Agent " + this.myAgent.getLocalName() +
					//							" : CANCEL " );
					this.myAgent.addBehaviour(new AbortBehaviour());
				}
				if(ACLMessage.PROPAGATE == msg.getPerformative()){
					for(Resource r : capacityMap.keySet()){
						capacityMap.put(r, 6);
					}
					offer = null;
					System.out.println(capacityMap);
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
		private Resource resType;
		public CalculatingBehaviour(Agent me, AID serviceAgg, Resource resType)
		{
			this.myAgent=me;
			this.serviceAgg= serviceAgg;
			this.resType = resType;
		}
		@Override
		public void action() {
			byte quantity = offer.get(resType);
			cost.put(resType, quantity * cost.get(resType));
			AbstractMap.SimpleEntry<Resource, Double> pair = new AbstractMap.SimpleEntry<>(resType,cost.get(resType));
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			msg.setSender(this.myAgent.getAID());
			msg.setLanguage("Java");
			try {
				msg.setContentObject(pair);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			msg.setOntology("");
			msg.addReceiver(serviceAgg);
			this.myAgent.send(msg);
			
			//XML Stuff
			Element resElement = doc.createElement(resType.toString());
			resElement.setTextContent(offer.get(resType).toString());
			resElement.setAttribute("capacity", capacityMap.get(resType).toString());
			resElement.setAttribute("cost", Double.toString(cost.get(resType)));
			requestElement.appendChild(resElement);
			
			this.finished=true;
			cost.put(resType, (double) 1);
		}
		@Override
		public boolean done() {
			return finished;
		}
	}

	/**
	 * Abort Behavior
	 */
	private class AbortBehaviour extends SimpleBehaviour {
		private boolean finished = false;
		@Override
		public void action() {
			System.err.println(this.myAgent.getLocalName() + ": Aborting");
			offer = null;
			this.finished = true;
		}
		@Override
		public boolean done() {
			return finished;
		}
	}
	/**
	 * Abort Behavior
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
			System.err.println(this.myAgent.getLocalName() + ": Reserving "  + currCost.getKey());
			System.err.println(offer);
			capacityMap.put(currCost.getKey(), capacityMap.get(currCost.getKey()) - offer.get(currCost.getKey()));
			profit = profit + currCost.getValue();
			
			this.finished = true;
		}
		@Override
		public boolean done() {
			return finished;
		}
	}
}
