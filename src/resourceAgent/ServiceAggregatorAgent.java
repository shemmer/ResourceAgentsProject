package resourceAgent;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

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
	//Current number of agents involved
	private int numberOfAgents;
	//Current aggregated costs
	private double aggCost;
	/**
	 * Overloading setup
	 */
	protected void setup(){
		numberOfAgents = 0;
		this.service = "ServiceAggregation";
		this.registerAtDF();
		gui = new MainWindow(this);
		addBehaviour(new ServiceReceiverBehaviour(this));
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
				if(ACLMessage.PROPOSE == msg.getPerformative()){
					try {
						AbstractMap.SimpleEntry<Resource, Double> pair = (SimpleEntry<Resource, Double>) msg.getContentObject();
						System.out.println(this.myAgent.getLocalName() + " received costs of " + pair.getValue()+ " for " + pair.getKey());
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
				if(ACLMessage.CONFIRM == msg.getPerformative()){
					numberOfAgents++;
				}
			}
		}
	}
	public void setOffer(Offer o){
		this.offer = o;
	}
	public void setCurrBestCost(HashMap<Resource, Double> map){
		this.bestCost = map;
	}
}
