import jade.wrapper.AgentController;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.tools.sniffer.Sniffer;
import jade.wrapper.StaleProxyException;
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
			AgentController res1 = mainContainer.createNewAgent("Res1", "agent.resourceAgent.ResourceAgent",  new String[] {"5","ONE"});
//			AgentController res2 = mainContainer.createNewAgent("Res2", "agent.resourceAgent.ResourceAgent", new String[] {"1","TWO"});
//			AgentController res3 = mainContainer.createNewAgent("Res3", "agent.resourceAgent.ResourceAgent",  new String[] {"1","THREE"});
//			AgentController res4 = mainContainer.createNewAgent("Res4", "agent.resourceAgent.ResourceAgent", new String[] {"1","FOUR"});
			AgentController res5 = mainContainer.createNewAgent("Res5", "agent.resourceAgent.ResourceAgent",  new String[] {"5","FIVE"});
			AgentController res6 = mainContainer.createNewAgent("Res6", "agent.resourceAgent.ResourceAgent", new String[] {"5","SIX"});
			AgentController res7 = mainContainer.createNewAgent("Res7", "agent.resourceAgent.ResourceAgent",  new String[] {"5","SEVEN"});
			AgentController res8 = mainContainer.createNewAgent("Res8", "agent.resourceAgent.ResourceAgent", new String[] {"5","EIGHT"});
			AgentController res9 = mainContainer.createNewAgent("Res9", "agent.resourceAgent.ResourceAgent",  new String[] {"5","NINE"});
			AgentController res10 = mainContainer.createNewAgent("Res10", "agent.resourceAgent.ResourceAgent", new String[] {"5","TEN"});
//			AgentController res11 = mainContainer.createNewAgent("Res11", "resourceAgent.ResourceAgent",  new String[] {"ELEVEN"});
//			AgentController res12 = mainContainer.createNewAgent("Res12", "resourceAgent.ResourceAgent", new String[] {"TWELVE"});
			AgentController multiAgent = mainContainer.createNewAgent("MultiAgent", "agent.resourceAgent.ResourceAgent", new String[] {"5","SIX", "SEVEN"});
			System.out.println("Setting up service aggregator agent ...");
			AgentController serviceAggAgent= mainContainer.createNewAgent("ServiceAggregator", "agent.serviceAgent.ServiceAggregatorAgent", new String[] {});

			System.out.println("Starting all agents ...");
			res1.start();
//			res2.start();
//			res3.start();
//			res4.start();
			res5.start();
			res6.start();			
			res7.start();
			res8.start();			
			res9.start();
			res10.start();
			multiAgent.start();
//			res11.start();
//			res12.start();
			serviceAggAgent.start();

		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
}	
