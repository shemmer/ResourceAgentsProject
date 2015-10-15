package resourceAgent;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

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
	//Current costs
	protected HashMap<AbstractMap.SimpleEntry<Resource, Double>, AID> currCosts;

	//Current negative reply
	protected ACLMessage negativeReply;

	//Set of positive/negative replies depending on the user input/service agg decision
	protected HashSet<ACLMessage> posReply;


	//Current offer in xml
	protected Element offerElement;
	/**
	 * Overloading setup -> Adding ServiceAggregator to the DF; Creating GUI; Adding Message ReceiverBehaviour
	 * 
	 */
	protected void setup(){
		super.setup();
		root= doc.createElement(this.getLocalName());
		path= new File("./log/"+ this.getLocalName()+"_hist.xml").getAbsolutePath();
		doc.appendChild(root);
		this.service = new String[1];
		this.service[0] = "ServiceAggregation";
		this.serviceName="Aggregator";
		this.registerAtDF();
		gui = new MainWindow(this);
		addBehaviour(new ServiceReceiverBehaviour(this));
//		this.writeHistoryToXML();
	}	
	/**
	 * Proposal Behaviour
	 */
	private class EvaluateBehaviour extends SimpleBehaviour {
		private boolean finished = false;

		public EvaluateBehaviour(Agent me){
			super(me);
		}
		@Override
		public void action() {
			HashMap<SimpleEntry<Resource,Double>, AID> bestAgent = new HashMap<SimpleEntry<Resource,Double>,AID>();
			posReply = new HashSet<ACLMessage>();
			negativeReply = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
			negativeReply.setLanguage("Java");
			negativeReply.setOntology("");
			negativeReply.setSender(this.myAgent.getAID());
			for(AbstractMap.SimpleEntry<Resource, Double> offerPair : currCosts.keySet()){
				if(offer.getBestCost().get(offerPair.getKey()) >= offerPair.getValue())
				{
					if(bestAgent.containsKey(offerPair.getKey())){
						//Here there is a better agent than the one already in the best map -> 
						//Will be overwritten therefore we add it to the negative replys
						negativeReply.addReceiver(currCosts.get(offerPair));
					}
					bestAgent.put(offerPair, currCosts.get(offerPair));	
					offer.putBestCost(offerPair.getKey(), offerPair.getValue());
				}else{
					negativeReply.addReceiver(currCosts.get(offerPair));
				}
			}
			for(SimpleEntry<Resource, Double> r : bestAgent.keySet()){
				@SuppressWarnings("deprecation")
				ACLMessage positiveReply = new ACLMessage();
				positiveReply.setLanguage("Java");
				positiveReply.setOntology("");
				positiveReply.setSender(this.myAgent.getAID());
				try {
					positiveReply.setContentObject(r);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				positiveReply.addReceiver(bestAgent.get(r));
				posReply.add(positiveReply);
			}

			this.myAgent.send(negativeReply);
			for (Resource key : offer.getBestCost().keySet()){
				offer.setAggCost(offer.getAggCost() + offer.getBestCost().get(key));
			}
			gui.updateAggCost(offer.getAggCost());
			//Visualize a suggestions for the user if he should accept the offer or not
			if(offer.getAggCost()< offer.getActiveObjectIncome()){
				gui.greenAcceptButton();
			}else{
				gui.redAcceptButton();
			}
			finished =true;
		}
		@Override
		public boolean done() {
			return finished;
		}
	}
	/**
	 * Accept Behaviour
	 */
	public class AcceptBehaviour extends SimpleBehaviour {
		boolean finished = false;

		@Override
		public void action() {
			//Creating the corresponding XML document
			Element accept = doc.createElement("ACCEPT_PROPOSAL");
			offerElement.appendChild(accept);
			
			//Informing all agents of the outcome
			for(ACLMessage e : posReply){
				Element agent = doc.createElement("Agent");
				agent.setAttribute("Name", ((AID) e.getAllReceiver().next()).getLocalName());
				accept.appendChild(agent);
				
				e.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				this.myAgent.send(e);	
			}
			finished = true;


			//Calling GUI Methods 
			gui.updateLogTextAreaAccept();
			gui.acceptActiveObject();

			root.appendChild(offerElement);
			writeHistoryToXML();
		}
		@Override
		public boolean done() {
			return finished;
		}
	}
	/**
	 * Reject Behaviour
	 */
	public class RejectBehaviour extends SimpleBehaviour {
		boolean finished = false;
		@Override
		public void action() {

			//Creating the corresponding XML element
			Element reject = doc.createElement("REJECT_PROPOSAL");
			offerElement.appendChild(reject);
			System.err.println(offerElement.toString());
			//Informing all resource agents of the outcome
			for(ACLMessage e : posReply){
				e.setPerformative(ACLMessage.REJECT_PROPOSAL);
				this.myAgent.send(e);	
			}


			//Calling GUI Methods
			gui.updateLogTextAreaReject();
			gui.rejectActiveObject();

			writeHistoryToXML();
			
			finished = true;	
		}
		@Override
		public boolean done() {
			return finished;
		}
	}
	/**
	 * 
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
				block();
			}else{
				//Actions performed when a cost proposal arrives from the resource agent
				if(ACLMessage.PROPOSE == msg.getPerformative()){
					try {
						SimpleEntry<Resource, Double>offerPair = (SimpleEntry<Resource, Double>) msg.getContentObject();
						currCosts.put(offerPair, msg.getSender());

						//XML Creation of Proposal history
						Element sender = doc.createElement(msg.getSender().getLocalName());
						Element prop = doc.createElement(offerPair.getKey().toString());
						prop.setTextContent(offerPair.getValue().toString());
						sender.appendChild(prop);
						offerElement.appendChild(sender);

					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					if(offer.getAgentsResMap().size() == currCosts.size()){
						this.myAgent.addBehaviour(new EvaluateBehaviour(this.myAgent));
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
					try {
						gui.updateLogTextArea(msg.getSender().getLocalName() +" refused the offer ");
						Set<AID> tmp = offer.getAgentsResMap().get((Resource) msg.getContentObject());
						gui.updateLogTextArea(offer.getAgentsResMap().toString());
						if(tmp!= null)
						{
							tmp.remove(msg.getSender());
							if(tmp.isEmpty()){
								Set<AID> contacts = new HashSet<AID>();
								for(Resource r : offer.getAgentsResMap().keySet()){
									System.err.println(r);
									contacts.addAll(offer.getAgentsResMap().get(r));
								}
								System.err.println(contacts);
								this.myAgent.addBehaviour(new RefusalBehaviour(this.myAgent, contacts));
							}
						}else{
							Set<AID> contacts = new HashSet<AID>();
							for(Resource r : offer.getAgentsResMap().keySet()){
								contacts.addAll(offer.getAgentsResMap().get(r));
							}
							this.myAgent.addBehaviour(new RefusalBehaviour(this.myAgent, contacts));
						}

					} catch (UnreadableException e) {
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
	public class RefusalBehaviour extends SimpleBehaviour{
		private boolean finished = false;
		private Set<AID> set;
		public RefusalBehaviour(Agent me,  Set<AID> set){
			super(me);
			this.set =set;

		}
		@Override
		public void action() {
			ACLMessage reply = new ACLMessage(ACLMessage.CANCEL);
			reply.setSender(this.myAgent.getAID());
			//all others
			for(AID a : set){
				reply.addReceiver(a);
			}
			reply.setLanguage("Java");
			reply.setOntology("");
			this.myAgent.send(reply);
			finished=true;
		}
		@Override
		public boolean done() {
			return finished;
		}

	}	

	/**
	 * Restart Behaviour
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
			block(1000);
			finished=true;
		}
		@Override
		public boolean done() {
			return finished;
		}

	}
	/**
	 * Start Behaviour
	 * @author stefan
	 *
	 */
	public class ServiceAggStartBehaviour extends SimpleBehaviour {
		private boolean finished = false;
		public ServiceAggStartBehaviour(Agent a, Offer o){
			super(a);
			ServiceAggregatorAgent serviceAgg = (ServiceAggregatorAgent) a;
			offer = o;
		}
		@Override
		public void action() {
			//XML creation of new offer
			offerElement = doc.createElement("Offer");
			//Initializing a new currCosts Maps
			currCosts= new HashMap<SimpleEntry<Resource, Double>, AID>();
			gui.updateLogTextArea("--------------------- Received an offer ---------------------");
			offer.setAggCost(0);
			HashMap<Resource, Byte> activeObjectMap = (HashMap<Resource, Byte>) offer.getActiveObjectMap();
			HashMap<Resource, Double> map = new HashMap<Resource, Double>();
			Iterator<Resource> it = activeObjectMap.keySet().iterator();
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setSender(this.myAgent.getAID());
			msg.setLanguage("Java");
			msg.setOntology("");
			//Create a new (empty) mapping between resources and a set of responsible agents
			offer.setAgentsResMap(new HashMap<Resource, Set<AID>>());
			//Temporary Set of already contacted agents
			Set<AID> contacted = new HashSet<AID>();
			while(it.hasNext())
			{
				Resource currRes = it.next();

				//XML creation of a new resource
				Element res = doc.createElement(currRes.toString());
				res.setTextContent(offer.getActiveObjectMap().get(currRes).toString());
				offerElement.appendChild(res);

				//Initialize the current responsible agents with empty set
				Set<AID> tmp = new HashSet<AID>();
				//Add a new empty set to the mapping between resources and responsible agents
				offer.putAgentsResMap(currRes, tmp);
				//Create a entry with max_value in the offer's currBestCostMap
				map.put(currRes, Double.MAX_VALUE);

				//Search the DF for agents responsible for the current resource
				DFAgentDescription dfd = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType(currRes.toString());
				sd.setName("Resource");
				dfd.addServices(sd);
				try {
					DFAgentDescription[] resAgents = DFService.search(this.myAgent, dfd);
					gui.updateLogTextArea("Found " + resAgents.length + " agent(s) for resource " + currRes );
					//TODO Abort when no agents for a resource are found
					if(resAgents.length==0){
						this.myAgent.addBehaviour(new RefusalBehaviour(this.myAgent, contacted));
						gui.toggleAcceptButton(false);
						break;
					}
					//Adding relevant agents to the receiver list
					for(DFAgentDescription a : resAgents)
					{
						a.getName();
						msg.addReceiver(a.getName());
						contacted.add(a.getName());
					}
				} catch (FIPAException e) {
					System.err.println("Error contacting DF service");
					e.printStackTrace();
				}
			}
			try {
				msg.setContentObject(offer.getActiveObjectMap());
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
