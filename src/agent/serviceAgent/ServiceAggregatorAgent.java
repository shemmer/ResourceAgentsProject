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

	//Current negative reply
	protected ACLMessage negativeReply;

	//Set of positive/negative replies depending on the user input/service agg decision
	protected HashSet<ACLMessage> posReplies;

	//Current offer in xml
	protected Element offerElement;

	//Automated i.e. no user input required
	protected boolean automated =false;
	
	//MinimalProfit of an offer
	protected int minimumProfit = 10;
	
	//Best Agents
	HashMap<Resource, AID> bestAgent;

	protected int replied;
	
	//Best Agents
	HashMap<Resource, HashMap<Integer,AID>> bestAgentCapacity;
		
	/**
	 * Overloading setup -> Adding ServiceAggregator to the DF; Creating GUI; Adding Message ReceiverBehaviour
	 * 
	 */
	protected void setup(){
		super.setup();
		bestAgent = new HashMap<Resource, AID>();
		bestAgentCapacity = new HashMap<Resource, HashMap<Integer,AID>>();
		root= doc.createElement(this.getLocalName());
		logFile = new File("./"+this.getLocalName() + "_hist.xml");
		path= logFile.getAbsolutePath();
		doc.appendChild(root);
		this.service = new String[1];
		this.service[0] = "ServiceAggregation";
		this.serviceName="Aggregator";
		this.registerAtDF();
		gui = new MainWindow(this);
		addBehaviour(new ReceiverBehaviour(this));

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
	public void unautomate() {
		this.automated=false;		
	}
	public void setMinimumProfit(int minimumProfit) {
		this.minimumProfit = minimumProfit;
	}
	
	/**
	 * Start Behaviour
	 * @author stefan
	 *
	 */
	public class StartBehaviour extends SimpleBehaviour {
		private boolean finished = false;
		public StartBehaviour(Agent a){
			super(a);
		}
		@Override
		public void action() {
			offer.activateObject();
			if(offer.isLimitSteps() && offer.getStep() < offer.getMaxStep()){		
				negativeReply = new ACLMessage(ACLMessage.REJECT_PROPOSAL);			
				//New offer, Repaint Offer Canvas, Update Price Per Res and Price for this order
				gui.repaintOfferCanvas();
				gui.updatePricePerResTextField(); 
				gui.updatePriceOrderTextField();
				//XML creation of new offer
				offerElement = doc.createElement("Offer");
				replied=0;
				//Initializing a new currCosts Maps
				offer.setAgentCostsMap(new HashMap<AID, HashMap<Resource, Double>>());

				offer.setAgentCostsDivMap(new HashMap<Resource, HashMap<Integer, HashMap<AID, Double>>>());
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
							offer.setRejected(true);
							this.myAgent.addBehaviour(new RejectBehaviour(this.myAgent));
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
					activeObjectMap.put(Resource.ID, (byte) offer.getID());
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
			bestAgent = new HashMap<Resource, AID>();
			//Preparing the ACLMessages for the Agents; A "positive" reply i.e. a reply with the possibility
			//that the offer will be accepted and a "negative" reply for the agents that are clearly out
			//i.e. there a better offers for this resource
			posReplies = new HashSet<ACLMessage>();
			//Evaluate each offer by iterating through agents
			for(AID agent : offer.getAgentCostsMap().keySet()){
				//XML Stuff
				Element sender = doc.createElement(agent.getLocalName());
				//Get the offers of an agent
				HashMap<Resource, Double> perAgentResourceOfferMap = offer.getAgentCostsMap().get(agent);
				if(offer.getAgentCostsMap().get(agent).size() == 1){
					//For each offer on a resource
					for(Resource r : perAgentResourceOfferMap.keySet()){
						//If the current best cost is bigger than that of the loop variable
						gui.updateLogTextArea("Agent "  + agent.getLocalName() + " proposed " + perAgentResourceOfferMap.get(r)/offer.getActiveObjectMap().get(r) + " for "+ r);
						
						if(!offer.getBestCost().containsKey(r)){
							offer.putBestCost(r, perAgentResourceOfferMap.get(r));
							bestAgent.put(r, agent);
						}else{
							if(offer.getBestCost().get(r) > perAgentResourceOfferMap.get(r))
							{
								if(bestAgent.containsKey(r)){
									negativeReply.addReceiver(bestAgent.get(r));
								}
								//Add the current
								bestAgent.put(r, agent);	
								offer.putBestCost(r, perAgentResourceOfferMap.get(r));
							}else{
								negativeReply.addReceiver(agent);
							}
						}
						//More XML Stuff
						Element prop = doc.createElement(r.toString());
						prop.setTextContent(perAgentResourceOfferMap.get(r).toString());
						sender.appendChild(prop);
					}
				}else{
					//For each offer on a resource
					HashSet<Resource> bestRes = new HashSet<Resource>();
					for(Resource r : perAgentResourceOfferMap.keySet()){
						//If the current best cost is bigger than that of the loop variable
						gui.updateLogTextArea("Agent "  + agent.getLocalName() + " proposed " 
								+ perAgentResourceOfferMap.get(r)/offer.getActiveObjectMap().get(r) + " for "+ r);	
						if(!offer.getBestCost().containsKey(r)){
							bestRes.add(r);
						}else{
							if(offer.getBestCost().get(r) > perAgentResourceOfferMap.get(r)){
								bestRes.add(r);
							}
						}
						//More XML Stuff
						Element prop = doc.createElement(r.toString());
						prop.setTextContent(perAgentResourceOfferMap.get(r).toString());
						sender.appendChild(prop);
					}
					if(bestRes.size() == perAgentResourceOfferMap.size()){
						for(Resource r : bestRes){
							offer.putBestCost(r, perAgentResourceOfferMap.get(r));
							if(bestAgent.get(r)!= null) negativeReply.addReceiver(bestAgent.get(r));
							bestAgent.put(r, agent);
						}
					}else{
						negativeReply.addReceiver(agent);
					}			
				}
				offerElement.appendChild(sender);
			}
			gui.updateLogTextArea("--------------------- Evaluation for No." +offer.getID()+ ": ---------------------");
			for(Resource r : bestAgent.keySet()){
				gui.updateLogTextArea("Resource " + r + " would go to " + bestAgent.get(r).getLocalName() + " with a total cost of " + offer.getBestCost().get(r));
				@SuppressWarnings("deprecation")
				ACLMessage singlePositiveReply = new ACLMessage();
				singlePositiveReply.setLanguage("Java");
				singlePositiveReply.setOntology("");
				singlePositiveReply.setSender(this.myAgent.getAID());
				SimpleEntry<Resource, Double> tmp = new SimpleEntry
						<Resource,Double>(r, offer.getBestCost().get(r));
				try {
					singlePositiveReply.setContentObject(tmp);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				singlePositiveReply.addReceiver(bestAgent.get(r));
				posReplies.add(singlePositiveReply);
			}
			this.myAgent.send(negativeReply);
			for (Resource key : offer.getBestCost().keySet()){
				offer.setAggCost(offer.getAggCost() + offer.getBestCost().get(key));
			}
			gui.updateAggCost(offer.getAggCost());
			//			gui.updateProfit(Double.toString(offer.getAggCost()- offer.getActiveObjectIncome()));
			double currentOfferProfit = offer.getActiveObjectIncome() -offer.getAggCost();
			gui.updateIncomeTextField(currentOfferProfit);
			//Visualize a suggestions for the user if he should accept the offer or not
			if(!automated){
				gui.toggleAcceptButton(true);
				gui.toggleRejectButton(true);
				if(currentOfferProfit>minimumProfit){
					gui.greenAcceptButton();
				}else{
					gui.redAcceptButton();
				}
			}else{
				if(currentOfferProfit>minimumProfit){
					this.myAgent.addBehaviour(new AcceptBehaviour());
				}else{
					this.myAgent.addBehaviour(new RejectBehaviour(myAgent));
				}
			}
			finished =true;
			block();

		}
		@Override
		public boolean done() {
			this.myAgent.removeBehaviour(this);
			return finished;
		}
	}
	
	
	/**
	 * Evaluate Behaviour
	 */
	private class EvaluateDividableBehaviour extends SimpleBehaviour {
		private boolean finished = false;

		public EvaluateDividableBehaviour(Agent me){
			super(me);
		}
		@Override
		public void action() {	
			gui.updateLogTextArea("--------------------- Agent proposals ---------------------");
			//A HashMap containing the "best" offers i.e. the lowest offers for a resource
			bestAgentCapacity = new HashMap<Resource, HashMap<Integer,AID>>();
			//Preparing the ACLMessages for the Agents; A "positive" reply i.e. a reply with the possibility
			//that the offer will be accepted and a "negative" reply for the agents that are clearly out
			//i.e. there a better offers for this resource
			posReplies = new HashSet<ACLMessage>();
			HashMap<AID, Double> best = new HashMap<AID, Double>();
			//For every element of the offer
			System.out.println(offer.getActiveObjectMap().keySet());
			for(Resource r : offer.getActiveObjectMap().keySet()){
				if(r== Resource.DUMMY || r== Resource.DUMMY_MAX || r== Resource.ID) continue;
				HashMap<Integer, HashMap<AID, Double>> currentOffers =offer.getAgentCostsDivMap().get(r);
				for(int i =1 ; i<= currentOffers.size(); i++){
					System.out.println(r+":" + " ["+ i+"] "+ (offer.getActiveObjectMap().get(r)-i));
					if(currentOffers.containsKey(i)){
						int requestCapacity = offer.getActiveObjectMap().get(r);
						int openCapacity = requestCapacity - i;
						HashMap<AID,Double> tmp = currentOffers.get(i);
						for(int j = 1 ; j<=openCapacity; j++){
							HashMap<AID,Double> tmp2 = currentOffers.get(i);
						}
					}
				}
			}
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
			//Update gui action
			for(Resource r: bestAgent.keySet()){
				gui.updateAgentCanvas(bestAgent.get(r).getLocalName(), r, offer.getActiveObjectMap().get(r), offer.getShapeID());
			}
			//Creating the corresponding XML document
			Element accept = doc.createElement("ACCEPT_PROPOSAL");
			offerElement.appendChild(accept);
			
			//Informing all agents of the outcome
			for(ACLMessage e : posReplies){
				Element agent = doc.createElement("Agent");
				String name= ((AID) e.getAllReceiver().next()).getLocalName();
				agent.setAttribute("Name", name);
				accept.appendChild(agent);
				e.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				this.myAgent.send(e);	
				
			}
			
			offer.addIncomeTotal(offer.getActiveObjectIncome()-offer.getAggCost());
			offer.acceptActiveObject();
			//Calling GUI Methods 
			gui.updateLogTextAreaAccept();
//			gui.repaintCapacityCanvas();
//			gui.repaintAgentCanvas();

			gui.repaintAgentCanvas();
			
			gui.updateStepTextField();
			gui.updatePricePerResTextField(); 
			gui.updatePriceOrderTextField();
			gui.updateTurnOverTextField();
			gui.updateIncomeTotalTextField();
			root.appendChild(offerElement);
			writeHistoryToXML();
			gui.toggleAcceptButton(false);
			gui.toggleRejectButton(false);
			finished = true;
			block();
		}
		@Override
		public boolean done() {
			this.myAgent.addBehaviour(new StartBehaviour(myAgent));
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
			//Creating the corresponding XML element
			Element reject = doc.createElement("REJECT_PROPOSAL");
			offerElement.appendChild(reject);
			if(posReplies!=null){
				//Informing all resource agents of the outcome
				for(ACLMessage e : posReplies){
					if(offer.isRejected()) e.setPerformative(ACLMessage.CANCEL); 
					else e.setPerformative(ACLMessage.REJECT_PROPOSAL);
					this.myAgent.send(e);	
				}
			}
			//Calling GUI Methods
			gui.updateLogTextAreaReject();
			offer.rejectActiveObject();
			gui.updateStepTextField();
			gui.updatePricePerResTextField(); 
			gui.updatePriceOrderTextField();
			gui.updateTurnOverTextField();
			writeHistoryToXML();
			
			gui.toggleAcceptButton(false);
			gui.toggleRejectButton(false);
			if(offer.isRejected()) negativeReply.setPerformative(ACLMessage.CANCEL);
			this.myAgent.send(negativeReply);
			
			finished = true;
			block();
		}
		@Override
		public boolean done() {
			this.myAgent.addBehaviour(new StartBehaviour(myAgent));
			this.myAgent.removeBehaviour(this);
			return finished;
		}
	}


	/**
	 * 
	 * Receiver Behaviour
	 */
	private class ReceiverBehaviour extends CyclicBehaviour {
		public ReceiverBehaviour(Agent me){
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
					++replied;
					try {
						HashMap<Resource, Double> map = (HashMap<Resource, Double>) msg.getContentObject();				
						offer.putAgentCostsMap(msg.getSender(), map);
//						HashMap<Resource, HashMap<Integer, Double>> currentOffer = (HashMap<Resource, HashMap<Integer, Double>>) msg.getContentObject();
//						for(Resource r: currentOffer.keySet()){
//							HashMap<Integer, HashMap<AID, Double>> tmp = new HashMap<Integer, HashMap<AID, Double>>();
//							for(int i : currentOffer.get(r).keySet()){
//								HashMap<AID, Double> tmp2 = new HashMap<AID,Double>();
//								tmp2.put(msg.getSender(), currentOffer.get(r).get(i));
//								tmp.put(i, tmp2);
//							}
//							offer.putAgentCostsDivMap(r, tmp);
//						}
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					this.checkAndStart();
				}
				if(ACLMessage.REFUSE == msg.getPerformative()){
					replied++;
					negativeReply.addReceiver(msg.getSender());
					try {
						gui.updateLogTextArea(msg.getSender().getLocalName() +" refused the offer ");
						HashSet<Resource> tmp = (HashSet<Resource>) msg.getContentObject();
						offer.getContactedAgents().get(msg.getSender()).remove(tmp);
						for(Resource r : tmp){
							offer.getContactedAgents().get(msg.getSender()).remove(r);
						}
						offer.getContactedAgents().remove(msg.getSender());
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
									tmp.toString());
							if(!automated) gui.toggleRejectButton(true);
							offer.setRejected(true);
						}				
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					this.checkAndStart();
				}
				if(ACLMessage.INFORM_REF == msg.getPerformative()){
					msg.getContent();
					gui.addStats(msg.getSender().getLocalName(), msg.getContent());		
				}			
			}
		}
		
		private void checkAndStart(){
			if(offer.getContactedAgents().size() == offer.getAgentCostsMap().size()){
				if(!offer.isRejected()){
					this.myAgent.addBehaviour(new EvaluateBehaviour(this.myAgent));
				}else {
					for(AID a : offer.getContactedAgents().keySet()) negativeReply.addReceiver(a);
					gui.toggleAcceptButton(false);
					if(automated)
						this.myAgent.addBehaviour(new RejectBehaviour(myAgent));
				}
			}
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
			gui.updateTurnOverTextField();
			gui.updateAggCost();
			gui.updateIncomeTotalTextField();
			gui.updateStepTextField();
			gui.repaintOfferCanvas();
			gui.addEmpty();
			
			gui.resetAgentCanvas();
//			gui.repaintCapacityCanvas();
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

	public int getMinimumProfit() {
		return minimumProfit;
	}
	
}
