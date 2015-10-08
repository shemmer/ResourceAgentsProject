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
	//Type of Resource the Agent is responsible for
	protected Resource resType;
	//ServiceAggregatorAgent ID
	protected AID serviceAgg;
	//Current offer
	protected HashMap<Resource, Byte> offer;
	//Current costs per res unit
	protected double cost = 1.2;
	/**
	 * Overloading setup to also execute the Registration of the agent with the DF
	 */
	protected void setup(){

		Object[] args = getArguments();
		for(int i =0; i<args.length; i++){
			resType = Resource.valueOf(args[i].toString());
		}
		this.service = resType.toString();
		this.registerAtDF();
		addBehaviour(new ResourceReceiverBehaviour());
	}
	/**
	 * Receiver Behavior
	 */
	private class ResourceReceiverBehaviour extends CyclicBehaviour {
		private boolean finished = false;
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
					send(createMessage(ACLMessage.CONFIRM, "ACK", msg.getSender()));
					System.out.println("Agent " + this.myAgent.getLocalName() +
							" received and acknowledged message from "+ msg.getSender().getLocalName() + " regarding resource " 
							+resType+ " with a quantity of " + offer.get(resType));
					this.myAgent.addBehaviour(new CalculatingBehaviour(this.myAgent, msg.getSender()));
				}
				if(ACLMessage.ACCEPT_PROPOSAL == msg.getPerformative()){
					System.out.println("Agent " + this.myAgent.getLocalName() +
							" received "+ msg.getSender().getLocalName() + " regarding resource " +resType+
							" accepting the proposed costs");
					this.myAgent.addBehaviour(new ReservingBehaviour());
				}
				if(ACLMessage.REJECT_PROPOSAL==msg.getPerformative()){
					System.out.println("Agent " + this.myAgent.getLocalName() +
							" received "+ msg.getSender().getLocalName() + " regarding resource " +resType+
							" rejecting the proposed costs");
					//TODO Nothing?
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
			System.out.println(this.myAgent.getLocalName() +  ": Starting calculation of costs for resource " + resType );
			byte quantity = offer.get(resType);
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
		@Override
		public void action() {
			System.out.println("Reserving resource " + resType);
		}
		@Override
		public boolean done() {
			return finished;
		}
	}
	
	
}
