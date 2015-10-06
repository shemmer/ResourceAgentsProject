package resourceAgent;

import jade.core.behaviours.SimpleBehaviour;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import wendtris.Offer;

public class ResourceAgent extends AbstractAgent{
	//Type of Resource the Agent is responsible for
	protected Resource resType;
	//ServiceAggregatorAgent ID
	protected AID serviceAgg;
	//Current offer
	protected Offer offer;
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
		addBehaviour(new WaitBehaviour());
	}
	/**
	 * Wait Behavior
	 */
	private class WaitBehaviour extends SimpleBehaviour {
		private boolean finished = false;
		@Override
		public void action() {
			ACLMessage msg = this.myAgent.receive();
			if(msg==null){
				block(1000);
			}else{
				send(createMessage(ACLMessage.CONFIRM, "ACK", msg.getSender()));
				removeBehaviour(new WaitBehaviour());
				System.out.println("Agent " + getAID().getName()+
						" received and acknowledged message from "+ msg.getSender());
				finished = true;
			}
		}
		@Override
		public boolean done() {
			return finished;
		}

	}
	/**
	 * Calculating Behavior
	 */
	private class CalculatingBehaviour extends SimpleBehaviour {
		private boolean finished = false;
		@Override
		public void action() {
			System.out.println("Sample Action");
			block(1000);
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
			System.out.println("Sample Action");
		}
		@Override
		public boolean done() {
			return finished;
		}

	}
	
	
}
