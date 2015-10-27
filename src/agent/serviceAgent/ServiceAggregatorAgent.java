package agent.serviceAgent;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;

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
import offer.OfferFactory;
import agent.AbstractAgent;
import gui.MainWindow;
import offer.Resource;

public class ServiceAggregatorAgent extends AbstractAgent{
	//GUI
	private MainWindow gui;
	//Current offer
	protected OfferFactory offer;
	//Current costs
//	protected HashMap<AID,HashMap<Resource, Double>> agentCostsMap;

	//Current negative reply
	protected ACLMessage negativeReply;

	//Set of positive/negative replies depending on the user input/service agg decision
	protected HashSet<ACLMessage> posReply;


	//Current offer in xml
	protected Element offerElement;

	//Automated i.e. no user input required
	protected boolean automated =false;
	
	protected volatile boolean processing = false;
	

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

		offer = new offer.OfferFactory();

		gui.registerOfferInCapacityCanvas(offer);
		gui.setOfferInOfferCanvas(offer);
		gui.setOffer(offer);

		negativeReply = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
		negativeReply.setLanguage("Java");
		negativeReply.setOntology("");
		negativeReply.setSender(this.getAID());
	}	


	public void automate(){
		this.automated=true;
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
			gui.updateLogTextArea("--------------------- Agent proposals ---------------------");
			//A HashMap containing the "best" offers i.e. the lowest offers for a resource
			HashMap<Resource, AID> bestAgent = new HashMap<Resource, AID>();
			//Preparing the ACLMessages for the Agents; A "positive" reply i.e. a reply with the possibility
			//that the offer will be accepted and a "negative" reply for the agents that are clearly out
			//i.e. there a better offers for this resource
			posReply = new HashSet<ACLMessage>();
//			negativeReply = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
//			negativeReply.setLanguage("Java");
//			negativeReply.setOntology("");
//			negativeReply.setSender(this.myAgent.getAID());
			//Evaluate each offer by iterating through agents
			for(AID agent : offer.getAgentCostsMap().keySet()){
				//XML Stuff
				Element sender = doc.createElement(agent.getLocalName());
				//Get the offers of an agent
				HashMap<Resource, Double> resourceOfferMap = offer.getAgentCostsMap().get(agent);
				//For each offer on a resource
				for(Resource r : resourceOfferMap.keySet()){
					//If the current best cost is bigger than that of the loop variable
					gui.updateLogTextArea("Agent "  + agent.getLocalName() + " proposed " + resourceOfferMap.get(r) + " for "+ r);
					
					if(!offer.getBestCost().containsKey(r)){
						offer.putBestCost(r, resourceOfferMap.get(r));
						bestAgent.put(r, agent);
					}else{
						
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
					}
					//More XML Stuff
					Element prop = doc.createElement(r.toString());
					prop.setTextContent(resourceOfferMap.get(r).toString());
					sender.appendChild(prop);
				}
				offerElement.appendChild(sender);
			}
			gui.updateLogTextArea("--------------------- Evaluation for No." +offer.getID()+ ": ---------------------");
			for(Resource r : bestAgent.keySet()){
				gui.updateLogTextArea("Resource " + r + " goes to " + bestAgent.get(r).getLocalName() + " with a total cost of " + offer.getBestCost().get(r));
				@SuppressWarnings("deprecation")
				ACLMessage positiveReply = new ACLMessage();
				positiveReply.setLanguage("Java");
				positiveReply.setOntology("");
				positiveReply.setSender(this.myAgent.getAID());
				SimpleEntry<Resource, Double> tmp = new SimpleEntry
						<Resource,Double>(r, offer.getBestCost().get(r));
				try {
					positiveReply.setContentObject(tmp);
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
			//			gui.updateProfit(Double.toString(offer.getAggCost()- offer.getActiveObjectIncome()));
			double a = offer.getAggCost()- offer.getActiveObjectIncome();
			//Visualize a suggestions for the user if he should accept the offer or not
			if(!automated){
				if(a >10){
					gui.greenAcceptButton();
				}else{
					gui.redAcceptButton();
				}
			}else{
				if(a>1){
					this.myAgent.addBehaviour(new AcceptBehaviour());
				}else{
					this.myAgent.addBehaviour(new RejectBehaviour(myAgent));
				}
			}
			finished =true;
			//			System.err.println("--------------- ENDEVAL --------------- ");
			block();

		}
		@Override
		public boolean done() {
			this.myAgent.removeBehaviour(this);
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

			processing = false;
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

			block();
		}
		@Override
		public boolean done() {
//			System.out.println("AC");
			this.myAgent.addBehaviour(new ServiceAggStartBehaviour(myAgent));
			this.myAgent.removeBehaviour(this);
			return finished;
		}
	}
	/**
	 * Reject Behaviour
	 */
	public class RejectBehaviour extends SimpleBehaviour {
		boolean finished = false;
		public RejectBehaviour(Agent me){
			super(me);
		}
		@Override
		public void action() {
//			System.out.println("REJ");
			//Creating the corresponding XML element
			Element reject = doc.createElement("REJECT_PROPOSAL");
			offerElement.appendChild(reject);
			if(posReply!=null){
				//Informing all resource agents of the outcome
				for(ACLMessage e : posReply){
					e.setPerformative(ACLMessage.REJECT_PROPOSAL);
					this.myAgent.send(e);	
				}
			}
			//Calling GUI Methods
			gui.updateLogTextAreaReject();
			offer.rejectActiveObject();
			gui.updateStepTextField();
			gui.updatePricePerResTextField(); 
			gui.updatePriceOrderTextField();
			gui.updateIncomeTextField();
			writeHistoryToXML();
			
			this.myAgent.send(negativeReply);
			finished = true;
			block();
		}
		@Override
		public boolean done() {
			this.myAgent.addBehaviour(new ServiceAggStartBehaviour(myAgent));
			this.myAgent.removeBehaviour(this);
			return finished;
		}
	}
	/**
	 * 
	 * Receiver Behaviour
	 */
	private class ServiceReceiverBehaviour extends CyclicBehaviour {
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
//					System.err.println("PROP" + msg.getSender().getLocalName());
					try {
						HashMap<Resource, Double> map = (HashMap<Resource, Double>) msg.getContentObject();
//												System.err.println("-------" + msg.getSender().getLocalName() + ":: " + map);				
						offer.putAgentCostsMap(msg.getSender(), map);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					this.checkAndStart();
				}
				//Actions performed when a resource agents confirms he is responsible for a resource and has
				//received the offer
				if(ACLMessage.CONFIRM == msg.getPerformative()){
					Resource r = Resource.valueOf(msg.getContent());
					Set<Resource> tmp = offer.getContactedAgents().get(msg.getSender());
					if(tmp!=null){
						tmp.add(r);
					}else{
						tmp = new HashSet<Resource>();
						tmp.add(r);
					}

				}
				if(ACLMessage.REFUSE == msg.getPerformative()){
//					System.err.println("REF" + msg.getSender().getLocalName());
					negativeReply.addReceiver(msg.getSender());
					try {
						gui.updateLogTextArea(msg.getSender().getLocalName() +" refused the offer ");
						HashSet<Resource> tmp = (HashSet<Resource>) msg.getContentObject();
						offer.getContactedAgents().get(msg.getSender()).remove(tmp);
						for(Resource r : tmp){
							offer.getContactedAgents().get(msg.getSender()).remove(r);
						}
						//Bundle Agents -> I.e. they are removed once they can not fulfill a request
//						if(offer.getContactedAgents().get(msg.getSender()).isEmpty())
//						{
							offer.getContactedAgents().remove(msg.getSender());
//						}
						boolean remainingCap = false;
						//Iterate through the remaining agents
						for(AID id : offer.getContactedAgents().keySet()){
							//Search the others agents for remaining capacity
							for(Resource r : tmp){
								if(offer.getContactedAgents().get(id).contains(r)) remainingCap=true;
							}
						}
						if(!remainingCap){
							gui.updateLogTextArea("Not enough remaining capacity for resource " +
									((HashSet<Resource>) msg.getContentObject()).toString());
							offer.setRejected(true);
						}				
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					this.checkAndStart();
				}
				if(ACLMessage.INFORM_REF == msg.getPerformative()){
					try {
						SimpleEntry<String, Double> pair = (SimpleEntry<String, Double>)
								msg.getContentObject();
						gui.addStats(pair);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}			
			}
		}
		
		private void checkAndStart(){
//			System.err.println("#### " + offer.getContactedAgents().size() + offer.getAgentCostsMap().size());

			if(offer.getContactedAgents().size() == offer.getAgentCostsMap().size()){
				if(!offer.isRejected()){
					this.myAgent.addBehaviour(new EvaluateBehaviour(this.myAgent));
//					System.err.println("-------------------");
				}else {
					for(AID a : offer.getContactedAgents().keySet()) negativeReply.addReceiver(a);
					gui.toggleAcceptButton(false);
					if(automated) this.myAgent.addBehaviour(new RejectBehaviour(myAgent));
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
			block();
			processing = false;
		}
		@Override
		public boolean done() {
			this.myAgent.removeBehaviour(this);
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
			//Informing all resource agents of the restart
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

			gui.initLogTextArea();

			offer.newGame();
			gui.updatePriceOrderTextField();
			gui.updatePricePerResTextField();
			gui.updateIncomeTextField();
			gui.updateStepTextField();
			gui.repaintCapacityCanvas();
			gui.repaintOfferCanvas();
			unautomate();
			this.finished=true;
		}
		@Override
		public boolean done() {
			this.myAgent.removeBehaviour(this);
			return finished;
		}
	}
	
	public class StatsCollectBehaviour extends SimpleBehaviour{
		private boolean finished = false;
		@Override
		public void action() {
			//Contact all agents to get stats
			if(offer.getStep() == offer.getMaxStep()){
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);	
				msg.setSender(this.myAgent.getAID());
				msg.setLanguage("Java");
				msg.setOntology("");
				DFAgentDescription dfd = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setName("Resource");
				dfd.addServices(sd);
				try {
					DFAgentDescription[] resAgents = DFService.search(this.myAgent, dfd);					
					//Adding relevant agents to the receiver list
					for(DFAgentDescription a : resAgents)
					{
						msg.addReceiver(a.getName());	
					}
				} catch (FIPAException e) {
					System.err.println("Error contacting DF service");
					e.printStackTrace();
				}
				myAgent.send(msg);
			}
			finished = true;
			
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
		public ServiceAggStartBehaviour(Agent a){
			super(a);
		}
		@Override
		public void action() {
			System.out.println("--------------------------------------------------------");
			if(offer.isLimitSteps() && offer.getStep() <= offer.getMaxStep()){			
				negativeReply = new ACLMessage(ACLMessage.REJECT_PROPOSAL);			
				//New offer, Repaint Offer Canvas, Update Price Per Res and Price for this order
				offer.activateObject();
				gui.repaintOfferCanvas();
				gui.updatePricePerResTextField(); 
				gui.updatePriceOrderTextField();
				//XML creation of new offer
				offerElement = doc.createElement("Offer");
				//Initializing a new currCosts Maps
				offer.setAgentCostsMap(new HashMap<AID, HashMap<Resource, Double>>());
				gui.updateLogTextArea("--------------------- Received an offer No."+offer.getID()+ " ---------------------");
				//The actual offer
				HashMap<Resource, Byte> activeObjectMap = (HashMap<Resource, Byte>) offer.getActiveObjectMap();
				Iterator<Resource> it = activeObjectMap.keySet().iterator();
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setSender(this.myAgent.getAID());
				msg.setLanguage("Java");
				msg.setOntology("");
				while(it.hasNext())
				{
					Resource currRes = it.next();
					//XML creation of a new resource
					Element res = doc.createElement(currRes.toString());
					res.setTextContent(offer.getActiveObjectMap().get(currRes).toString());
					offerElement.appendChild(res);
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
							this.myAgent.addBehaviour(new RefusalBehaviour(this.myAgent, offer.getContactedAgents().keySet()));
							gui.toggleAcceptButton(false);
							break;
						}
						//Adding relevant agents to the receiver list
						for(DFAgentDescription a : resAgents)
						{
							offer.addContactedAgents(a.getName(), currRes);
							msg.addReceiver(a.getName());	
						}
					} catch (FIPAException e) {
						System.err.println("Error contacting DF service");
						e.printStackTrace();
					}
				}
				if(offer.isLimitSteps()){
					activeObjectMap.put(Resource.DUMMY, (byte) offer.getStep());
					activeObjectMap.put(Resource.DUMMY_MAX, (byte) offer.getMaxStep());
				}
				try {
					msg.setContentObject(activeObjectMap);
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.myAgent.send(msg);
				finished = true;
				block();
			}
			if(offer.getStep() == offer.getMaxStep()){
				this.myAgent.addBehaviour(new StatsCollectBehaviour());
			}
		}
		@Override
		public boolean done() {
			this.myAgent.removeBehaviour(this);
			return finished;
		}
	}
	public void unautomate() {
		this.automated=false;		
	}
}
