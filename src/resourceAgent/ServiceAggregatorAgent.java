package resourceAgent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import wendtris.MainWindow;

public class ServiceAggregatorAgent extends AbstractAgent{
	private MainWindow gui;
	/**
	 * Overloading setup
	 */
	protected void setup(){
		this.service = "ServiceAggregation";
		this.registerAtDF();
		gui = new MainWindow(this);
	}
	/**
	 * Compare Behavior
	 */
	private class compareBehaviour extends SimpleBehaviour {

		@Override
		public void action() {
			System.out.println("Sample Action");
		}
		@Override
		public boolean done() {
			return true;
		}

	}
	/**
	 * Propose Behavior
	 */
	private class ProposeBehaviour extends SimpleBehaviour {
		
		@Override
		public void action() {
			System.out.println("Sample Action");
		}
		@Override
		public boolean done() {
			return true;
		}
	}
}
