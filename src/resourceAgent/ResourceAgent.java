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
	//Current costs per res unit
	protected double cost = 1.2;
	//Current remaining capacity
	protected int capacity=6;
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
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
		//Current offer
		protected HashMap<Resource, Byte> offer;
		@Override
		public void action() {
			ACLMessage msg = this.myAgent.receive();
			if(msg==null){
				block(1000);
			}else{
				//New Offer
				if(ACLMessage.INFORM == msg.getPerformative()){
					System.out.println(this.myAgent.getLocalName() + " : INFORM");
					try {
						offer = (HashMap<Resource, Byte>) msg.getContentObject();
					} catch (UnreadableException e) {
						System.err.println("Error deserializing offer object");
						e.printStackTrace();
					}
					//ResourceType handled by ResourceAgent and capacity sufficient
					if(offer.containsKey(resType) && capacity - offer.get(resType) >=0){
						System.out.println(this.myAgent.getLocalName()+  " CONFIRM");
						ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
						reply.addReceiver(msg.getSender());
						try {
							reply.setContentObject(resType);
						} catch (IOException e) {
							e.printStackTrace();
						}
						reply.setLanguage("Java");
						reply.setOntology("");
						reply.setSender(this.myAgent.getAID());
						this.myAgent.send(reply);
						this.myAgent.addBehaviour(new CalculatingBehaviour(this.myAgent, msg.getSender(),offer));
					}else{
						System.out.println(this.myAgent.getLocalName() + " REFUSE");
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
				if(ACLMessage.ACCEPT_PROPOSAL == msg.getPerformative()){
					System.out.println("Agent " + this.myAgent.getLocalName() +": COST_ACCEPT");
					this.myAgent.addBehaviour(new ReservingBehaviour());
				}
				if(ACLMessage.REJECT_PROPOSAL==msg.getPerformative()){
					System.out.println("Agent " + this.myAgent.getLocalName() +
							" COST_REJECT");
					//TODO Nothing?
				}
				if(ACLMessage.CANCEL== msg.getPerformative()){
					System.out.println("Agent " + this.myAgent.getLocalName() +
							" : CANCEL " );
					this.myAgent.addBehaviour(new AbortBehaviour());
				}
			}
		}

	}
	/**
	 * Calculating Behavior
	 */
	private class CalculatingBehaviour extends SimpleBehaviour {
		//Current offer
		protected HashMap<Resource, Byte> offer;
		private boolean finished = false;
		private AID serviceAgg;
		public CalculatingBehaviour(Agent me, AID serviceAgg, 
				HashMap<Resource, Byte> or)
		{
			this.offer= or;
			this.myAgent=me;
			this.serviceAgg= serviceAgg;
		}
		@Override
		public void action() {
			System.out.println(this.myAgent.getLocalName() +  ": Starting calculation of costs for"
					+ " resource " + resType );
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
				capacity = capacity - offer.get(resType);
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
//			System.out.println("Reserving resource " + resType);
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
