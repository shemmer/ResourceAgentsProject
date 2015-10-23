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
import jade.core.behaviours.ReceiverBehaviour;
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
	protected HashMap<AID,HashMap<Resource, Double>> agentCostsMap;

	//Current negative reply
	protected ACLMessage negativeReply;

	//Set of positive/negative replies depending on the user input/service agg decision
	protected HashSet<ACLMessage> posReply;


	//Current offer in xml
	protected Element offerElement;

	//Automated i.e. no user input required
	protected boolean automated =false;
	
	protected volatile boolean processing = false;
	
	protected boolean rejected=false;

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

		offer = new wendtris.OfferFactory();

		gui.registerOfferInCapacityCanvas(offer);
		gui.setOfferInOfferCanvas(offer);
		gui.setOffer(offer);
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
			negativeReply = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
			negativeReply.setLanguage("Java");
			negativeReply.setOntology("");
			negativeReply.setSender(this.myAgent.getAID());
			//Evaluate each offer by iterating through agents
			for(AID agent : agentCostsMap.keySet()){
				//XML Stuff
				Element sender = doc.createElement(agent.getLocalName());
				//Get the offers of an agent
				HashMap<Resource, Double> resourceOfferMap = agentCostsMap.get(agent);
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
//						System.out.println(bestAgent);
			for(Resource r : bestAgent.keySet()){
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
//			System.err.println(a);
			//Visualize a suggestions for the user if he should accept the offer or not
			if(!automated){
				if(a >10){
					gui.greenAcceptButton();
				}else{
					gui.redAcceptButton();
				}
			}else{
				if(a>10){
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
				this.myAgent.addBehaviour(new ServiceAggStartBehaviour(myAgent));
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
			
			if(posReply!=null){
				//Creating the corresponding XML element
				Element reject = doc.createElement("REJECT_PROPOSAL");
				offerElement.appendChild(reject);
				//Informing all resource agents of the outcome
				for(ACLMessage e : posReply){
					e.setPerformative(ACLMessage.REJECT_PROPOSAL);
					this.myAgent.send(e);	
				}
				//Calling GUI Methods
				gui.updateLogTextAreaReject();
				offer.rejectActiveObject();
				writeHistoryToXML();
			}
			processing=false;
			finished = true;
			block();
		}
		@Override
		public boolean done() {
			this.myAgent.addBehaviour(new ServiceAggStartBehaviour(myAgent));
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
//												System.err.println("-------" + msg.getSender().getLocalName() + ":: " + map);
						//AID -> Resource, D
						
						agentCostsMap.put(msg.getSender(), map);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					if(offer.getContactedAgents().size() == agentCostsMap.size()){
//						System.err.println("######"+offer.getContactedAgents().size() + agentCostsMap.size());
						if(!rejected){
							this.myAgent.addBehaviour(new EvaluateBehaviour(this.myAgent));
						}else {
							if(automated) this.myAgent.addBehaviour(new RejectBehaviour(myAgent));
						}
					}
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
					try {
						gui.updateLogTextArea(msg.getSender().getLocalName() +" refused the offer ");
						Resource tmp = (Resource) msg.getContentObject();
						offer.getContactedAgents().get(msg.getSender()).remove(tmp);
						if(offer.getContactedAgents().get(msg.getSender()).isEmpty())
						{
							offer.getContactedAgents().remove(msg.getSender());
						}
						boolean remainingCap = false;
						for(AID id : offer.getContactedAgents().keySet()){
							if(offer.getContactedAgents().get(id).contains(tmp)) remainingCap=true;
						}
						if(!remainingCap){
							gui.updateLogTextArea("Not enough remaining capacity for resource " + ((Resource) msg.getContentObject()).toString());
							gui.toggleAcceptButton(false);
							rejected=true;
						}
						
						if(offer.getContactedAgents().size() == agentCostsMap.size()){
							if(!rejected){
								this.myAgent.addBehaviour(new EvaluateBehaviour(this.myAgent));
							}else {
								if(automated) this.myAgent.addBehaviour(new RejectBehaviour(myAgent));
							}
						}

						//						
						//						//Check whether there a agents remaining that can fulfill the request
						//						if(tmp!= null)
						//						{
						//							tmp.remove(msg.getSender());
						//							if(tmp.isEmpty()){
						//								Set<AID> contacts = new HashSet<AID>();
						//								for(Resource r : offer.getResponsibleAgentsForRes().keySet()){
						//									contacts.addAll(offer.getResponsibleAgentsForRes().get(r));
						//								}
						//								this.myAgent.addBehaviour(new RefusalBehaviour(this.myAgent, contacts));
						//								gui.toggleAcceptButton(false);
						//							}else{
						//								//The refusal message could be the last message -> so we need the possibility to start the Evaluation
						//								//TODO Problem we can't just remove the agent from the contacted agents list as he may propose another offer for another resource
						//								//Since only "single" resource agents respond with refuse
						//								if(offer.getContactedAgents().size() == agentCostsMap.size()){
						//									this.myAgent.addBehaviour(new EvaluateBehaviour(this.myAgent));
						//								}			
						//							}
						//						}else{
						//							gui.updateLogTextArea("Not enough remaining capacity for resource " + ((Resource) msg.getContentObject()).toString());
						//							Set<AID> contacts = new HashSet<AID>();
						//							for(Resource r : offer.getResponsibleAgentsForRes().keySet()){
						//								contacts.addAll(offer.getResponsibleAgentsForRes().get(r));
						//							}
						//							this.myAgent.addBehaviour(new RefusalBehaviour(this.myAgent, contacts));
						//							gui.toggleAcceptButton(false);
						//						}

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
			block();
			processing = false;
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
			gui.repaintCapacityCanvas();
			gui.repaintOfferCanvas();
			unautomate();
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
		public ServiceAggStartBehaviour(Agent a){
			super(a);
		}
		@Override
		public void action() {
			if(offer.isLimitSteps() && offer.getStep() <= offer.getMaxStep()){
				processing = true;
				rejected=false;
				//New offer, Repaint Offer Canvas, Update Price Per Res and Price for this order
				offer.activateObject();
				gui.repaintOfferCanvas();
				gui.updatePricePerResTextField(); 
				gui.updatePriceOrderTextField();
				//XML creation of new offer
				offerElement = doc.createElement("Offer");
				//Initializing a new currCosts Maps
				agentCostsMap= new HashMap<AID, HashMap<Resource, Double>>();
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
				try {
					msg.setContentObject(offer.getActiveObjectMap());
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.myAgent.send(msg);
				finished = true;
				block();
			}
		}
		@Override
		public boolean done() {
			return finished;
		}
	}
	public void unautomate() {
		this.automated=false;		
	}
}
