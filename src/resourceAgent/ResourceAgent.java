package resourceAgent;

import jade.core.Agent;

public class ResourceAgent extends Agent {
	/**
	 * Setup Method
	 */
	protected void setup(){
		//Printout a welcome message
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
