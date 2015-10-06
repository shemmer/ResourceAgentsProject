package wendtris;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import resourceAgent.Resource;

public class ServiceAggStartBehaviour extends SimpleBehaviour {
	private boolean finished = false;
	private Offer offer;
	public ServiceAggStartBehaviour(Agent a, Offer o){
		super(a);
		this.offer = o;
	}
	@Override
	public void action() {;
		System.out.println("Received an offer");
		byte[] activeObj = offer.getActiveObject();
		Map<Resource, Byte> activeObjectMap = offer.getActiveObjectMap();
		Iterator<Resource> it = activeObjectMap.keySet().iterator();
		while(it.hasNext())
		{
			DFAgentDescription dfd = new DFAgentDescription();
			Resource res = it.next();
			//In case we just want to submit the amount of a resource that is needed
			byte val = activeObjectMap.get(res);
			ServiceDescription sd = new ServiceDescription();
			sd.setType(res.toString());
			sd.setName("Agent_"+res.toString());
			dfd.addServices(sd);
			try {
				DFAgentDescription[] resAgents = DFService.search(this.myAgent, dfd);
				System.out.println("Found " + resAgents.length + " agent(s) for resource " + res );
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setSender(this.myAgent.getAID());
				msg.setLanguage("Java");
				msg.setContentObject(offer.getActiveObject());
				msg.setOntology("");
				for(DFAgentDescription a : resAgents)
				{
					a.getName();
					msg.addReceiver(a.getName());
				}
				this.myAgent.send(msg);
			} catch (FIPAException e) {
				System.err.println("Error contacting DF service");
				e.printStackTrace();
			} 
			catch (IOException e) {
				System.err.println("Error converting Offer object");
				e.printStackTrace();
			}
		}
		finished = true;
		System.out.println("Resource Agents contacted");
	}
	@Override
	public boolean done() {
		return finished;
	}

}
