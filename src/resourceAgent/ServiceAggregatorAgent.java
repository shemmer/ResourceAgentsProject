package resourceAgent;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import wendtris.MainWindow;
import wendtris.Offer;

public class ServiceAggregatorAgent extends AbstractAgent{
	//GUI
	private MainWindow gui;
	//Current Offer
	private Offer offer;
	//Current best costs
	private HashMap<Resource, Double> bestCost;
	//Current available agents for one resource
	private HashMap<Resource, Set<AID>> agentsResMap;
	//Current aggregated costs
	private double aggCost;
	/**
	 * Overloading setup
	 */
	protected void setup(){
		this.service = "ServiceAggregation";
		this.registerAtDF();
		gui = new MainWindow(this);
		addBehaviour(new ServiceReceiverBehaviour(this));
	}
	/**
	 * Compare Behavior
	 */
	private class CompareBehaviour extends SimpleBehaviour {

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
	 * Receiver Behaviour
	 */
	private class ServiceReceiverBehaviour extends CyclicBehaviour {
		private boolean finished = false;
		public ServiceReceiverBehaviour(Agent me){
			this.myAgent = (ServiceAggregatorAgent) me;
		}
		@Override
		public void action() {
			ACLMessage msg = this.myAgent.receive();
			if(msg==null){
				block(1000);
			}else{
				//Actions performed when a cost proposal arrives from the resource agent
				if(ACLMessage.PROPOSE == msg.getPerformative()){
					try {
						AbstractMap.SimpleEntry<Resource, Double> pair = (SimpleEntry<Resource, Double>) msg.getContentObject();
						//						System.out.println(this.myAgent.getLocalName() + " received costs of " + pair.getValue()+ " for " + pair.getKey());
						if(bestCost.get(pair.getKey())<= pair.getValue())
						{
							//Intermediate
							send(createMessage(ACLMessage.REJECT_PROPOSAL, "REJECT", msg.getSender()));
						}
						else{
							//Intermediate
							bestCost.put(pair.getKey(), pair.getValue());
							send(createMessage(ACLMessage.ACCEPT_PROPOSAL, "ACCEPT", msg.getSender()));	
						}						
						aggCost=0;
						for (Resource key : bestCost.keySet()){
							aggCost = aggCost + bestCost.get(key);
						}
						gui.updateAggCost(aggCost);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
				//Actions performed when a resource agents confirms he is responsible for a resource and has
				//received the offer
				if(ACLMessage.CONFIRM == msg.getPerformative()){
					try {
						Resource r = (Resource) msg.getContentObject();
						Set<AID> tmp = agentsResMap.get(r);
						if(tmp!=null){
							tmp.add(msg.getSender());
						}else{
							tmp = new HashSet<AID>();
							tmp.add(msg.getSender());
						}
						agentsResMap.put(r, tmp);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
				if(ACLMessage.REFUSE == msg.getPerformative()){
					try{
						Resource r = (Resource) msg.getContentObject();
						System.err.println(msg.getSender().getLocalName() +" refused the offer ");
						Set<AID> tmp = agentsResMap.get(r);	
						if(tmp!= null)
						{
							tmp.remove(msg.getSender());
							if(tmp.isEmpty()){
								ACLMessage reply = new ACLMessage(ACLMessage.CANCEL);
								reply.setSender(this.myAgent.getAID());
								reply.addReceiver(msg.getSender());
								//all others
								Iterator<Resource> it = agentsResMap.keySet().iterator();
								while(it.hasNext()){
									tmp = agentsResMap.get(it.next());
									for(AID a : tmp){
										reply.addReceiver(a);
									}
								}
								reply.setLanguage("Java");
								reply.setOntology("");
								this.myAgent.send(reply);
							}
						}else{
							ACLMessage reply = new ACLMessage(ACLMessage.CANCEL);
							reply.setSender(this.myAgent.getAID());
							reply.addReceiver(msg.getSender());
							//all others
							Iterator<Resource> it = agentsResMap.keySet().iterator();
							while(it.hasNext()){
								tmp = agentsResMap.get(it.next());
								for(AID a : tmp){
									reply.addReceiver(a);
								}
							}
							reply.setLanguage("Java");
							reply.setOntology("");
							this.myAgent.send(reply);
						}
					}catch(UnreadableException e){
						e.printStackTrace();
					}
				}
			}
		}
	}
	/**
	 * 
	 * @author stefan
	 *
	 */
	private class RestartBehaviour extends SimpleBehaviour{
		boolean finished = false;
		@Override
		public void action() {

		}
		@Override
		public boolean done() {
			return finished;
		}

	}
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
		System.out.println("--------------------- Received an offer ---------------------");
		HashMap<Resource, Byte> activeObjectMap = (HashMap<Resource, Byte>) offer.getActiveObjectMap();
		HashMap<Resource, Double> map = new HashMap<Resource, Double>();
		Iterator<Resource> it = activeObjectMap.keySet().iterator();
		activeObjectMap.size();
		while(it.hasNext())
		{
			Resource currRes = it.next();
			//Initialize the current responsible agents with empty set
			agentsResMap = new HashMap<Resource, Set<AID>>();
			Set<AID> tmp = new HashSet<AID>();
			agentsResMap.put(currRes, tmp);;
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
		}
		@Override
		public boolean done() {
			return finished;
		}
	}
	/**
	 * Setting an current offer object in Service Aggregator
	 * @param o
	 */
	public void setOffer(Offer o){
		this.offer = o;
	}
	public void setCurrBestCost(HashMap<Resource, Double> map){
		this.bestCost = map;
	}
}
