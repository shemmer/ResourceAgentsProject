package resourceAgent;

import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class ResourceAgent extends AbstractAgent{
	//Type of Resource the Agent is responsible for
	protected Resource type;
	// Directory-Facilitator Description of the agent
	protected DFAgentDescription dfAgentDescr;
		
	/**
	 * Overloading setup to also execute the Registration of the agent with the DF
	 */
	protected void setup(){
		//Registering the Resource agents for a Domain in the Directory Facilitator
		this.registerAtDF();
		//TODO Register tasks in a queue by calling addBehaviour()
		addBehaviour(new SampleBehaviour());
	}
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
	/**
	 * Registers each agent at the df that is can be found via the given type
	 * @param type - the name under which the agent can be searched at the df
	 */
	protected void registerAtDF(){
		dfAgentDescr = new DFAgentDescription();
		dfAgentDescr.setName(id);
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type.toString());
		sd.setName("Agent_" + type);
		dfAgentDescr.addServices(sd);
		try {
			DFService.register(this, dfAgentDescr);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		System.out.println(getLocalName() + " registered at DF");
	}
}
