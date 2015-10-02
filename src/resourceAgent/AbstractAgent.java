package resourceAgent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public abstract class AbstractAgent extends Agent{
	//Agent Identifier
	protected AID id;
	/**
	 * Setup Method
	 */
	protected void setup(){
		//Printout a welcome message
		Object[] args = getArguments();
		//TODO Insert common options, read them from the input if Necessary
		id = getAID();
		System.out.println("Hi! I am" + getAID().getName()+ " Ready!");
	}
	/**
	 * Clean-up operations
	 */
	protected void takeDown(){
		//Printout a dismissal message
		System.out.println(getAID().getName() + " says Good Bye!");
	}
}
