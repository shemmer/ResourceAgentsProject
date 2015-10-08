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
import resourceAgent.ServiceAggregatorAgent;

public class ServiceAggStartBehaviour extends SimpleBehaviour {
	private boolean finished = false;
	private Offer offer;
	public ServiceAggStartBehaviour(Agent a, Offer o){
		super(a);
		ServiceAggregatorAgent serviceAgg = (ServiceAggregatorAgent) a;
		serviceAgg.setOffer(o);
		this.offer = o;
	}
	@Override
	public void action() {;
		System.out.println("Received an offer");
		byte[] activeObj = offer.getActiveObject();
		HashMap<Resource, Byte> activeObjectMap = (HashMap<Resource, Byte>) offer.getActiveObjectMap();
		HashMap<Resource, Double> map = new HashMap<Resource, Double>();
		Iterator<Resource> it = activeObjectMap.keySet().iterator();
		while(it.hasNext())
		{
			
			Resource currRes = it.next();
			//Create a entry with 0 in the currBestCost map
			map.put(currRes, Double.MAX_VALUE);
			//Search the DF for agents responsible for the current resource
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(currRes.toString());
			sd.setName("Agent_"+currRes.toString());
			dfd.addServices(sd);
			try {
				DFAgentDescription[] resAgents = DFService.search(this.myAgent, dfd);
				System.out.println("Found " + resAgents.length + " agent(s) for resource " + currRes );
				//Contact agent to inform him of a new offer and submit it to him
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setSender(this.myAgent.getAID());
				msg.setLanguage("Java");
				msg.setContentObject(activeObjectMap);
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
		((ServiceAggregatorAgent) this.myAgent).setCurrBestCost(map);
		finished = true;
		System.out.println("Resource Agents contacted");
	}
	@Override
	public boolean done() {
		return finished;
	}

}
