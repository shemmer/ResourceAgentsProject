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
import wendtris.OfferFactory;

public class ServiceAggregatorAgent extends AbstractAgent{
	//GUI
	private MainWindow gui;
	//Current offer
	protected OfferFactory offer;
	//Current costs
	protected HashMap<AID,HashMap<Resource, Double>> currCostOffers;

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
	 * Evaluate Behaviour
	 */
	private class EvaluateBehaviour extends SimpleBehaviour {
		private boolean finished = false;

		public EvaluateBehaviour(Agent me){
			super(me);
		}
		@Override
		public void action() {
			//A HashMap containing the "best" offers i.e. the lowest offers for a resource
			HashMap<Resource, AID> bestAgent = new 
					HashMap<Resource, AID>();
			//Preparing the ACLMessages for the Agents; A "positive" reply i.e. a reply with the possibility
			//that the offer will be accepted and a "negative" reply for the agents that are clearly out
			//i.e. there a better offers for this resource
			posReply = new HashSet<ACLMessage>();
			negativeReply = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
			negativeReply.setLanguage("Java");
			negativeReply.setOntology("");
			negativeReply.setSender(this.myAgent.getAID());
			System.err.println("--------------- EVAL --------------- ");
			//Evaluate each offer by iterating through agents
			for(AID agent : currCostOffers.keySet()){
				//XML Stuff
				Element sender = doc.createElement(agent.getLocalName());
				//Get the offers of an agent
				HashMap<Resource, Double> resourceOfferMap = currCostOffers.get(agent);
				System.err.println(agent.getLocalName());
				//For each offer on a resource

				for(Resource r : resourceOfferMap.keySet()){

					System.err.println(r+" :: " +  agent.getLocalName() + " -- " + resourceOfferMap.get(r));
					//If the current best cost is bigger than that of the loop variable
					if(offer.getBestCost().get(r) > resourceOfferMap.get(r))
					{
						if(bestAgent.containsKey(r)){
							negativeReply.addReceiver(bestAgent.get(r));
						}
						//Add the current
						bestAgent.put(r, agent);	
						offer.putBestCost(r, resourceOfferMap.get(r));
					}else{
						negativeReply.addReceiver(agent);
					}
					//More XML Stuff
					Element prop = doc.createElement(r.toString());
					prop.setTextContent(resourceOfferMap.get(r).toString());
					sender.appendChild(prop);
				}
				offerElement.appendChild(sender);
			}
//			System.err.println(bestAgent);
			for(Resource r : bestAgent.keySet()){
				@SuppressWarnings("deprecation")
				ACLMessage positiveReply = new ACLMessage();
				positiveReply.setLanguage("Java");
				positiveReply.setOntology("");
				positiveReply.setSender(this.myAgent.getAID());
				SimpleEntry<Resource, Double> tmp = new SimpleEntry<Resource,Double>(r, offer.getBestCost().get(r));
				try {
					positiveReply.setContentObject(tmp);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				positiveReply.addReceiver(bestAgent.get(r));
				posReply.add(positiveReply);
			}
//			System.err.println(bestAgent);
//			System.err.println(offer.getBestCost());
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
			

			System.err.println("--------------- ENDEVAL --------------- ");
		}
		@Override
		public boolean done() {
			return finished;
		}
	}
	/**
	 * Accepting Behaviour
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
			offer.acceptActiveObject();
			gui.repaintCapacityCanvas();
			
			gui.updateStepTextField();
			gui.updatePricePerResTextField(); 
			gui.updatePriceOrderTextField();
			gui.updateIncomeTextField();
			
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
			if(posReply!=null){
			//Creating the corresponding XML element
			Element reject = doc.createElement("REJECT_PROPOSAL");
			offerElement.appendChild(reject);
			//Informing all resource agents of the outcome
			//TODO Here was a null pointer
			for(ACLMessage e : posReply){
				e.setPerformative(ACLMessage.REJECT_PROPOSAL);
				this.myAgent.send(e);	
			}
			//Calling GUI Methods
			gui.updateLogTextAreaReject();
			offer.rejectActiveObject();
			writeHistoryToXML();
			}
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
						HashMap<Resource, Double> map = (HashMap<Resource, Double>) msg.getContentObject();
						currCostOffers.put(msg.getSender(), map);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					if(offer.getAgentsResMap().size() == currCostOffers.size()){
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
									contacts.addAll(offer.getAgentsResMap().get(r));
								}
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
			
			gui.initNewGame();
			gui.initLogTextArea();
			gui.enableAcceptButton();
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
		public ServiceAggStartBehaviour(Agent a, OfferFactory o){
			super(a);
			offer = o;
		}
		@Override
		public void action() {
			
			gui.repaintOfferCanvas();
			//XML creation of new offer
			offerElement = doc.createElement("Offer");
			//Initializing a new currCosts Maps
			currCostOffers= new HashMap<AID, HashMap<Resource, Double>>();
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
					if(resAgents.length==0){
						this.myAgent.addBehaviour(new RefusalBehaviour(this.myAgent, contacted));
						gui.toggleAcceptButton(false);
						break;
					}
					//Adding relevant agents to the receiver list
					for(DFAgentDescription a : resAgents)
					{
						a.getName();
						if(!contacted.contains(a.getName())){
							msg.addReceiver(a.getName());
							contacted.add(a.getName());
						}
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
