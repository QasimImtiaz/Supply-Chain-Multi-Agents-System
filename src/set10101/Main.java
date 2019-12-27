package set10111.SupplyChain;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;



public class Main {
	public static void main(String[] args) {
		Profile myProfile = new ProfileImpl();
		Runtime myRuntime = Runtime.instance();
		try{
			ContainerController myContainer = myRuntime.createMainContainer(myProfile);	
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();
			
			AgentController manufacturerAgent = myContainer.createNewAgent("manufacturer",Manufacturer.class.getCanonicalName(), null);
			manufacturerAgent.start();
			AgentController supplierAgent = myContainer.createNewAgent("supplier",Supplier.class.getCanonicalName(), null);
			supplierAgent.start();
			AgentController supplierAgent2 = myContainer.createNewAgent("supplierTwo",SupplierTwo.class.getCanonicalName(), null);
			supplierAgent2.start();
			
			
			int numCustomers = 50;
			AgentController customer;
			for(int i=0; i<numCustomers; i++) {
				customer = myContainer.createNewAgent("customer" + i, Customer.class.getCanonicalName(), null);
				customer.start();
			}
			
			AgentController tickerAgent = myContainer.createNewAgent("ticker",TickerAgent.class.getCanonicalName(),
					null);
			tickerAgent.start();
			
		}
		catch(Exception e){
			System.out.println("Exception starting agent: " + e.toString());
		}
		
	}
}
