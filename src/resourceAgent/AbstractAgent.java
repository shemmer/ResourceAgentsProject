package resourceAgent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public abstract class AbstractAgent extends Agent{
	//Agent Identifier
	protected AID id;
	//Type of agent
	protected String[] service;
	// Directory-Facilitator Description of the agent
	protected DFAgentDescription dfAgentDescr;
	//Service Name
	protected String serviceName;
	/**
	 * Setup Method
	 */
	protected void setup(){
		//Printout a welcome message
		//TODO Insert common options, read them from the input if Necessary
		id = getAID();
		System.out.println("Hi! I am" + getAID().getName()+ " Ready!");
	}
	/**
	 * Clean-up operations
	 */
	protected void takeDown(){
		try {
			DFService.deregister(this);
		}
		catch(Exception e){
			System.err.println("Could not deregister agent " + getAID().getName());
		}
		//Printout a dismissal message
		System.out.println(getAID().getName() + " says Good Bye!");
	}
	/**
	 * Registers each agent at the df that is can be found via the given type
	 * @param service - the name under which the agent can be searched at the df
	 */
	protected void registerAtDF(){
		String serv = "";
		dfAgentDescr = new DFAgentDescription();
		dfAgentDescr.setName(id);
		for(int i = 0; i<service.length; i++){
			ServiceDescription sd = new ServiceDescription();
			sd.setName(serviceName);
			sd.setType(service[i]);
			dfAgentDescr.addServices(sd);	
			serv = serv + service[i];
		}
		try {
			DFService.register(this, dfAgentDescr);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		System.out.println(getLocalName() + " registered at DF - Type: "+ serv + "; Service: "+ serviceName);
	}
	/**
	 * Send a message to a single agent
	 */
	protected ACLMessage createMessage(int performative, String content, AID agent){
		ACLMessage msg = new ACLMessage(performative);
		msg.setSender(id);
		msg.addReceiver(agent);
		msg.setLanguage("Java");
		msg.setContent(content);
		msg.setOntology("");
		return msg;
	}
	
	
}
