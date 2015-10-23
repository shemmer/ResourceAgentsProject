package resourceAgent;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class ResourceAgent extends AbstractAgent{
	//Current costs per res unit
	protected HashMap<Resource ,Double> costPerUnit;
	//Current remaining capacity 
	protected HashMap<Resource, Integer> capacityMap;
	//Current offer
	protected double profit;
	//Current offer map
	protected HashMap<Resource, Byte> offer;
	protected Element requestElement;
	
	/**
	 * Overloading setup to also execute the Registration of the agent with the DF
	 */
	protected void setup(){
		super.setup();
		root= doc.createElement(this.getLocalName());
		doc.appendChild(root);
		path= new File("./log/" + this.getLocalName()+"_hist.xml").getAbsolutePath();
		Object[] args = getArguments();
		capacityMap=new HashMap<Resource,Integer>();
		this.service = new String[args.length-1];
		this.serviceName ="Resource";
		costPerUnit = new HashMap<Resource, Double>();
		for(int i =1; i<args.length; i++){
			this.costPerUnit.put(Resource.valueOf(args[i].toString()), Double.valueOf((String) args[0]));
			capacityMap.put(Resource.valueOf(args[i].toString()), 6);
			this.service[i-1] = args[i].toString();
		}
		
		
		this.registerAtDF();
		
		addBehaviour(new ResourceReceiverBehaviour(this));
	}

	
	
	/**
	 * Receiver Behavior
	 */
	private class ResourceReceiverBehaviour extends CyclicBehaviour {
		public ResourceReceiverBehaviour(Agent a){
			this.myAgent = (ResourceAgent) a;
		}

		@Override
		public void action() {
			ACLMessage msg = this.myAgent.receive();
			if(msg==null){
				block(1000);
			}else{
				//New Offer
				if(ACLMessage.REQUEST == msg.getPerformative()){
//					System.err.println(this.myAgent.getLocalName()  + " :: REQUEST");
					try {
						offer = (HashMap<Resource, Byte>) msg.getContentObject();
					} catch (UnreadableException e) {
						System.err.println("Error deserializing offer object");
						e.printStackTrace();
					}
					requestElement= doc.createElement("request");
					root.appendChild(requestElement);
					boolean enough =false;
					Resource res = null;
					for(Resource resType : capacityMap.keySet()){
						if(!offer.containsKey(resType))continue;
						//ResourceType handled by ResourceAgent and capacity sufficient for one resource though
						if(capacityMap.get(resType) - offer.get(resType) >=0){
//							System.err.println(myAgent.getLocalName() + " :" + (capacityMap.get(resType) - offer.get(resType)));
							enough=true;
						}else{
							res= resType;
//							System.err.println("FALSE");
							enough=false;
							break;
						}
					}
					if(enough){
						this.myAgent.addBehaviour(new CalculatingBehaviour(this.myAgent, msg.getSender()));
					}else{
						ACLMessage reply = new ACLMessage(ACLMessage.REFUSE);
						reply.setSender(this.myAgent.getAID());
						reply.setLanguage("Java");
						try {
							reply.setContentObject(res);
						} catch (IOException e) {
						    e.printStackTrace();
						}
						reply.setOntology("");
						reply.addReceiver(msg.getSender());
						this.myAgent.send(reply);
						
					}
				} 
				if(ACLMessage.ACCEPT_PROPOSAL == msg.getPerformative()){
					//Reserving capacity for the current
					try {
//						System.err.println(this.myAgent.getLocalName()  + " ACCEPT");
						SimpleEntry<Resource, Double> pair = 
								(SimpleEntry<Resource, Double>) msg.getContentObject();
						this.myAgent.addBehaviour(new ReservingBehaviour(this.myAgent,pair));
						
						//XML Stuff
						NodeList requestNodes = requestElement.getElementsByTagName(pair.getKey().toString());
						Element resElement = (Element) requestNodes.item(0);
						resElement.setAttribute("accepted","true");
						writeHistoryToXML();
		
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
				if(ACLMessage.REJECT_PROPOSAL==msg.getPerformative()){
//					System.out.println("Agent " + this.myAgent.getLocalName() +
//												" COST_REJECT");
//					Element reject = doc.createElement("REJECTED");
//					requestElement.appendChild(reject);
					writeHistoryToXML();
					
				}
				//"Cancel" behaviour i.e. the service aggregator aborts the current offer because
				//one of the resource does not have any agents with capacity left
				if(ACLMessage.CANCEL== msg.getPerformative()){
					//					System.out.println("Agent " + this.myAgent.getLocalName() +
					//							" : CANCEL " );
					offer=null;
				}
				//Reset Behaviour
				if(ACLMessage.PROPAGATE == msg.getPerformative()){
					for(Resource r : capacityMap.keySet()){
						capacityMap.put(r, 6);
					}
					for(Resource r : costPerUnit.keySet()){
						costPerUnit.put(r, 6.0);
					}
					offer = null;
				}
			}
		}

	}
	/**
	 * Calculating Behaviour
	 */
	private class CalculatingBehaviour extends SimpleBehaviour {
		private boolean finished = false;
		private AID serviceAgg;
		public CalculatingBehaviour(Agent me, AID serviceAgg)
		{
			this.myAgent=me;
			this.serviceAgg= serviceAgg;
		}
		@Override
		public void action() {
			HashMap<Resource, Double> local_resourceCostMap = new 
					HashMap<Resource, Double>();
			//Is bottleneck?
			boolean bottleneck =false;
			byte tmpValue=0;
			for(Resource r : offer.keySet()){
				if(offer.get(r)>tmpValue){
					tmpValue = offer.get(r);
				} 
			}//tmpValue = highest requested capacity
			for(Resource r1: offer.keySet()){
				if(capacityMap.containsKey(r1) && offer.get(r1)==tmpValue) bottleneck=true;
			}
			
			//Iterating through all resources that are in the capacityMap
			for(Resource r : capacityMap.keySet()){
				//Skipping every resource, that is not in the offer
				if(!offer.containsKey(r))continue;
				double currCost = costPerUnit.get(r);

				//Reading history information
				Element docElement = doc.getDocumentElement();
				NodeList requestNodes = docElement.getElementsByTagName("request");
				for(int i=0; i<requestNodes.getLength(); i++){
					Element requestElement = (Element) requestNodes.item(i);
					NodeList resourceNodes = requestElement.getChildNodes();
					for(int j = 0; j < resourceNodes.getLength(); j++){
						Element resElement = (Element) resourceNodes.item(j);
						if(resElement.getTagName().equals(r.toString())){
							if(resElement.hasAttribute("accepted")){
								if(Double.parseDouble(resElement.getAttributeNode("cost").getValue()) <= currCost) {
									//For each accepted offer where the current costs are lower or equal
									//to the cost in the accepted offer
									currCost = currCost + i * 0.1;
								}else{
									
								}
							}else{
								if(Double.parseDouble(resElement.getAttributeNode("cost").getValue()) >= currCost){ 
									currCost = currCost - i * 0.1;
								}else{
									
								}
							}
						}
					}
				}
				costPerUnit.put(r, currCost);


				byte quantity = offer.get(r);
				if(bottleneck){
					currCost = 2* quantity * costPerUnit.get(r);
				}else{
					currCost = quantity * costPerUnit.get(r);
				}
				
				local_resourceCostMap.put(r, currCost);
				//XML Stuff
				Element resElement = doc.createElement(r.toString());
				resElement.setTextContent(offer.get(r).toString());
				resElement.setAttribute("capacity", capacityMap.get(r).toString());
				resElement.setAttribute("cost", Double.toString(local_resourceCostMap.get(r)));
				requestElement.appendChild(resElement);
			}
			
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			msg.setSender(this.myAgent.getAID());
			msg.setLanguage("Java");
			try {
				msg.setContentObject(local_resourceCostMap);
			} catch (IOException e) {
				e.printStackTrace();
			}	
			msg.setOntology("");
			msg.addReceiver(serviceAgg);
			this.myAgent.send(msg);
			
			this.finished=true;
			
		}
		@Override
		public boolean done() {
			return finished;
		}
	}

	/**
	 * Reserving Behavior
	 */
	private class ReservingBehaviour extends SimpleBehaviour {
		private boolean finished = false;
		private SimpleEntry<Resource, Double> currCost;
		public ReservingBehaviour(Agent me, SimpleEntry<Resource, Double> simpleEntry){
			super(me);
			this.currCost = simpleEntry;
		}
		@Override
		public void action() {
//			System.out.println("------------Reserving " + myAgent.getLocalName() + currCost.getKey());
			capacityMap.put(currCost.getKey(), capacityMap.get(currCost.getKey()) - offer.get(currCost.getKey()));
			profit = profit + currCost.getValue();
//			System.err.println("#### Profit: " + this.myAgent.getLocalName() +" ; " + profit);
			this.finished = true;
		}
		@Override
		public boolean done() {
			return finished;
		}
	}
}
