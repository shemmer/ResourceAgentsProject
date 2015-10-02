import jade.wrapper.AgentController;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.tools.sniffer.Sniffer;
import jade.wrapper.StaleProxyException;
import jade.core.Runtime;

import wendtris.MyDemonstrator;

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
			// Starts a 
			AgentController rma;
			rma = mainContainer.createNewAgent("rma",
					"jade.tools.rma.rma", new Object[0]);
			rma.start();
			System.out.println("RMA agent launched...");
			System.out.println("Setting up agents ...");
			//args[0] =  # resource slots
			AgentController res1 = mainContainer.createNewAgent("Res1", "mas.resourceAgent.ResourceAgent",  new String[] {});
			AgentController res2 = mainContainer.createNewAgent("Res2", "mas.resourceAgent.ResourceAgent", new String[] {});

			res1.start();
			res2.start();
		} catch (StaleProxyException e) {
			
			e.printStackTrace();
		}
	new MyDemonstrator();
	}
}	
