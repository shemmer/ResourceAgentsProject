package resourceAgent;

import jade.core.behaviours.SimpleBehaviour;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import wendtris.Offer;

public class ResourceAgent extends AbstractAgent{
	//ServiceAggregatorAgent ID
	protected AID serviceAgg;
	//Current costs per res unit
	protected double cost = 1.0;
	//Current remaining capacity 
	//TODO turn into MAP
	protected HashMap<Resource, Integer> capacityMap;
	//Current offer
	protected Offer offer;
	/**
	 * Overloading setup to also execute the Registration of the agent with the DF
	 */
	protected void setup(){
		Object[] args = getArguments();
		capacityMap=new HashMap<Resource,Integer>();
		this.service = new String[args.length];
		this.serviceName ="Resource";
		for(int i =0; i<args.length; i++){
			capacityMap.put(Resource.valueOf(args[i].toString()), 6);
			this.service[i] = args[i].toString();
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
					//					System.out.println(this.myAgent.getLocalName() + " : INFORM");
					try {
						offer = (Offer) msg.getContentObject();
					} catch (UnreadableException e) {
						System.err.println("Error deserializing offer object");
						e.printStackTrace();
					}
					for(Resource resType : capacityMap.keySet()){
						if(!offer.getActiveObjectMap().containsKey(resType))break;
						//ResourceType handled by ResourceAgent and capacity sufficient
						if(offer.getActiveObjectMap().containsKey(resType) && capacityMap.get(resType) - offer.getActiveObjectMap().get(resType) >=0){
							//						System.out.println(this.myAgent.getLocalName()+  " CONFIRM");
							ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
							reply.addReceiver(msg.getSender());
							reply.setContent(resType.toString());
							reply.setLanguage("Java");
							reply.setOntology("");
							reply.setSender(this.myAgent.getAID());
							this.myAgent.send(reply);
							this.myAgent.addBehaviour(new CalculatingBehaviour(this.myAgent, msg.getSender(),resType));
						}else{
							//						System.out.println(this.myAgent.getLocalName() + " REFUSE");
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
							offer = null;
						}
					}
				}
				if(ACLMessage.ACCEPT_PROPOSAL == msg.getPerformative()){
					//					System.out.println("Agent " + this.myAgent.getLocalName() +": COST_ACCEPT");
					Resource resType;
					try {
						resType = (Resource) msg.getContentObject();
						capacityMap.put(resType, capacityMap.get(resType) - offer.getActiveObjectMap().get(resType)) ;
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
				if(ACLMessage.REJECT_PROPOSAL==msg.getPerformative()){
					//					System.out.println("Agent " + this.myAgent.getLocalName() +
					//							" COST_REJECT");
					//TODO Nothing?
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
			System.out.println(this.myAgent.getLocalName() +  ": Starting calculation of costs for"
					+ " resource " + resType );

			byte quantity = offer.getActiveObjectMap().get(resType);
			cost = quantity * cost;
			AbstractMap.SimpleEntry<Resource, Double> pair = new AbstractMap.SimpleEntry<>(resType,cost);
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
			this.finished=true;
			cost =1;
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
			System.err.println("Aborting");
			this.finished = true;
		}
		@Override
		public boolean done() {
			return finished;
		}
	}
}
