import jade.wrapper.AgentController;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.tools.sniffer.Sniffer;
import jade.wrapper.StaleProxyException;
import offer.Resource;
import jade.core.Runtime;


public class Start {
	public static void main(String[] args) {
		// Get a hold on JADE runtime
		Runtime rt = Runtime.instance();

		// Exit the JVM when there are no more containers around
		rt.setCloseVM(true);
		System.out.print("Runtime created\n");

		// Create a default profile
		Profile profile = new ProfileImpl(null, 1200, null);
		System.out.print("Profile created\n");

		System.out.println("Profile: "+profile);
		jade.wrapper.AgentContainer mainContainer = rt.createMainContainer(profile);

		System.out.println("Containers created\n");
		System.out.println("Launching agents...");

		try {			
			// Starts a rma agent -> GUI agent of Jade 
			AgentController rma;
			rma = mainContainer.createNewAgent("rma",
					"jade.tools.rma.rma", new Object[0]);
//			rma.start();
			System.out.println("RMA agent launched...");
			System.out.println("Setting up resource agents ...");
			//args[0] =  # resource slots
			//New agent arguments Resource<<Starting Price << Capacity
			AgentController res1 = mainContainer.createNewAgent("Agent1",
					"agent.resourceAgent.ResourceAgent",  new String[] {Resource.NINE.toString()+"<<4<<6"});
			AgentController res5 = mainContainer.createNewAgent("Agent5",
					"agent.resourceAgent.ResourceAgent",  new String[] {Resource.FIVE.toString()+"<<5<<8"});
			AgentController res5_1 = mainContainer.createNewAgent("Agent5_1", 
					"agent.resourceAgent.ResourceAgent",  new String[] {Resource.FIVE.toString()+"<<1<<8"});
			AgentController res6 = mainContainer.createNewAgent("Agent6",
					"agent.resourceAgent.ResourceAgent", new String[] {Resource.SIX.toString()+"<<5<<8"});
			AgentController res7 = mainContainer.createNewAgent("Agent7", 
					"agent.resourceAgent.ResourceAgent",  new String[] {Resource.SEVEN+  "<<5<<8"});
			AgentController res8 = mainContainer.createNewAgent("Agent8",
					"agent.resourceAgent.ResourceAgent", new String[] {Resource.EIGHT.toString()+"<<5<<6"});
			AgentController res9 = mainContainer.createNewAgent("Agent9", 
					"agent.resourceAgent.ResourceAgent",  new String[] {Resource.NINE.toString() + "<<5<<8"});
			AgentController res10 = mainContainer.createNewAgent("Agent10", 
					"agent.resourceAgent.ResourceAgent", new String[] {Resource.TEN.toString()+"<<5<<6"});
			AgentController multiResAgent1 = mainContainer.createNewAgent("MRA1", 
					"agent.resourceAgent.ResourceAgent", new String[] {"SIX<<5<<8", "SEVEN<<5<<8","ONE<<5<<8"});
			AgentController multiResAgent2 = mainContainer.createNewAgent("MRA2", 
					"agent.resourceAgent.ResourceAgent", new String[] {Resource.TEN.toString()+ "<<5<<8",
							Resource.SEVEN.toString() + "<<5<<8"});
			System.out.println("Setting up service aggregator agent ...");
			AgentController serviceAggAgent= mainContainer.createNewAgent("ServiceAggregator",
					"agent.serviceAgent.ServiceAggregatorAgent", new String[] {});

			System.out.println("Starting all agents ...");
			res1.start();
			res5.start();
			res5_1.start();
			res6.start();			
			res7.start();
			res8.start();			
			res9.start();
			res10.start();
			multiResAgent1.start();
			multiResAgent2.start();
			serviceAggAgent.start();

		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
}	
