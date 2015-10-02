package resourceAgent;

import jade.core.behaviours.SimpleBehaviour;

public class ServiceAggregatorAgent extends AbstractAgent{
	
	/**
	 * Sample Behavior
	 */
	private class SampleBehaviour extends SimpleBehaviour {

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
