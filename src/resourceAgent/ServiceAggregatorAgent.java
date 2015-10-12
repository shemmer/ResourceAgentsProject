package resourceAgent;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.AMSService;
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
	//Current offer
	protected Offer offer;
	/**
	 * Overloading setup -> Adding ServiceAggregator to the DF; Creating GUI; Adding Message ReceiverBehaviour
	 * 
	 */
	protected void setup(){
		this.service = new String[1];
		this.service[0] = "ServiceAggregation";
		this.serviceName="Aggregator";
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
						AbstractMap.SimpleEntry<Resource, Double> offerPair = (AbstractMap.SimpleEntry<Resource, Double>) msg.getContentObject();
						
						//						System.out.println(this.myAgent.getLocalName() + " received costs of " + pair.getValue()+ " for " + pair.getKey());
						if(offer.getBestCost().get(offerPair.getKey())<= offerPair.getValue())
						{
							//Intermediate
							send(createMessage(ACLMessage.REJECT_PROPOSAL, "REJECT", msg.getSender()));
						}
						else{
							//Intermediate
							offer.setBestCost(offerPair.getKey(), offerPair.getValue());
							ACLMessage reply = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
							reply.addReceiver(msg.getSender());
							reply.setLanguage("Java");
							reply.setOntology("");
							reply.setContentObject(offerPair.getKey());
							this.myAgent.send(reply);
//							gui.updateLogTextArea(offer.getBestCost().toString() + " ; " + offerPair.getKey() + " : " + offerPair.getValue());
						}
						offer.setAggCost(0);
						for (Resource key : offer.getBestCost().keySet()){
							offer.setAggCost(offer.getAggCost() + offer.getBestCost().get(key));
						}
						gui.updateAggCost(offer.getAggCost());

					} catch (UnreadableException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				//Actions performed when a resource agents confirms he is responsible for a resource and has
				//received the offer
				if(ACLMessage.CONFIRM == msg.getPerformative()){
					Resource r = Resource.valueOf(msg.getContent());
					Set<AID> tmp = offer.getAgentsResMap().get(r);
					if(tmp!=null){
						tmp.add(msg.getSender());
					}else{
						tmp = new HashSet<AID>();
						tmp.add(msg.getSender());
					}
					offer.putAgentsResMap(r, tmp);
				}
				if(ACLMessage.REFUSE == msg.getPerformative()){
					gui.updateAggCost("INFINITY");
					try{
						Resource r = (Resource) msg.getContentObject();
						gui.updateLogTextArea(msg.getSender().getLocalName() +" refused the offer ");
						Set<AID> tmp = offer.getAgentsResMap().get(r);	
						if(tmp!= null)
						{
							tmp.remove(msg.getSender());
							if(tmp.isEmpty()){
								ACLMessage reply = new ACLMessage(ACLMessage.CANCEL);
								reply.setSender(this.myAgent.getAID());
								reply.addReceiver(msg.getSender());
								//all others
								Iterator<Resource> it = offer.getAgentsResMap().keySet().iterator();
								while(it.hasNext()){
									tmp = offer.getAgentsResMap().get(it.next());
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
							Iterator<Resource> it = offer.getAgentsResMap().keySet().iterator();
							while(it.hasNext()){
								tmp = offer.getAgentsResMap().get(it.next());
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
	public class RestartBehaviour extends SimpleBehaviour{
		boolean finished = false;
		@Override
		public void action() {
			gui.updateLogTextArea("-------------- Restarting --------------");
			ACLMessage msg = new ACLMessage(ACLMessage.PROPAGATE);
			msg.setSender(this.myAgent.getAID());
			msg.setLanguage("Java");
			msg.setOntology("");
			List<Resource> list = Resource.allValuesAsList();
			for(Resource r : list){
				DFAgentDescription dfd = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType(r.toString());
				sd.setName("Agent_"+r.toString());
				dfd.addServices(sd);
				DFAgentDescription[] resAgents;
				
				try {
					resAgents = DFService.search(this.myAgent, dfd);
					gui.updateLogTextArea("Resetting " + resAgents.length);
					//Adding relevant agents to the receiver list
					for(DFAgentDescription a : resAgents)
					{
						a.getName();
						msg.addReceiver(a.getName());
					}
				} catch (FIPAException e) {
					e.printStackTrace();
				}
			}
		
			
			this.myAgent.send(msg);
			finished=true;
		}
		@Override
		public boolean done() {
			return finished;
		}

	}
	public class ServiceAggStartBehaviour extends SimpleBehaviour {
		private boolean finished = false;
		public ServiceAggStartBehaviour(Agent a, Offer o){
			super(a);
			ServiceAggregatorAgent serviceAgg = (ServiceAggregatorAgent) a;
			offer = o;
		}
		@Override
		public void action() {;
		gui.updateLogTextArea("--------------------- Received an offer ---------------------");
		offer.setAggCost(0);
		HashMap<Resource, Byte> activeObjectMap = (HashMap<Resource, Byte>) offer.getActiveObjectMap();
		HashMap<Resource, Double> map = new HashMap<Resource, Double>();
		Iterator<Resource> it = activeObjectMap.keySet().iterator();
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		msg.setLanguage("Java");
		msg.setOntology("");
		while(it.hasNext())
		{
			Resource currRes = it.next();
			//Initialize the current responsible agents with empty set
			offer.setAgentsResMap(new HashMap<Resource, Set<AID>>());
			Set<AID> tmp = new HashSet<AID>();
			offer.putAgentsResMap(currRes, tmp);
			//Create a entry with max_value in the offer's currBestCostMap
			map.put(currRes, Double.MAX_VALUE);
			System.err.println(currRes);
			//Search the DF for agents responsible for the current resource
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(currRes.toString());
			sd.setName("Resource");
			dfd.addServices(sd);
			try {
				DFAgentDescription[] resAgents = DFService.search(this.myAgent, dfd);
				gui.updateLogTextArea("Found " + resAgents.length + " agent(s) for resource " + currRes );
				//Adding relevant agents to the receiver list
				for(DFAgentDescription a : resAgents)
				{
					a.getName();
					msg.addReceiver(a.getName());
				}
			} catch (FIPAException e) {
				System.err.println("Error contacting DF service");
				e.printStackTrace();
			} 
		}
		try {
			msg.setContentObject(offer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		offer.setBestCost(map);
		this.myAgent.send(msg);
		finished = true;
		}

		@Override
		public boolean done() {
			return finished;
		}
	}
}
