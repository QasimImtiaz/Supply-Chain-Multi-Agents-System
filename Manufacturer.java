package set10111.SupplyChain;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.annotations.Slot;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import set10111.SupplyChain_ontology.SupplyChainOntology;
import set10111.SupplyChain_ontology.elements.Battery;
import set10111.SupplyChain_ontology.elements.Component;
import set10111.SupplyChain_ontology.elements.Deliver;
import set10111.SupplyChain_ontology.elements.Order;
import set10111.SupplyChain_ontology.elements.Phablet_Smartphone;
import set10111.SupplyChain_ontology.elements.Purchase;
import set10111.SupplyChain_ontology.elements.RAM;
import set10111.SupplyChain_ontology.elements.Screen;
import set10111.SupplyChain_ontology.elements.Ship;
import set10111.SupplyChain_ontology.elements.Small_Smartphone;
import set10111.SupplyChain_ontology.elements.Smartphone;
import set10111.SupplyChain_ontology.elements.Storage;




public class Manufacturer extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplyChainOntology.getInstance();
	private ArrayList<Smartphone> smartphones = new ArrayList<>(); 
	private ArrayList<Smartphone> todaySmartphones = new ArrayList<>(); 
	private ArrayList<AID> suppliers= new ArrayList<>();
	private ArrayList<AID> suppliersTwo = new ArrayList<>();
	private ArrayList<AID> customers = new ArrayList<>(); 
	private ArrayList<Component> deliveredComponents = new ArrayList<>(); 
	private ArrayList<Component> warehouse = new ArrayList<>(); 
	private int numberOfComponentsOrdered = 0; 
	private int numberOfComponentsOrdered2 = 0; 
	private double  totalProfit = 0; 
	private double totalProfit2 = 0; 
	private double warehouseCost = 0; 
	private double totalPenaltyCost = 0;
	private double totalComponentsCost = 0; 
	private double numberOfAssembledSmartphones = 0; 
	private AID tickerAgent;
	private int quantity = 0; 
	 

	
	protected void setup() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		//add this agent to the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Manufacturer");
		sd.setName(getLocalName() + "-Manufacturer");
		dfd.addServices(sd);
		try{
			DFService.register(this, dfd);
		}
		catch(FIPAException e){
			e.printStackTrace();
		}
		addBehaviour(new TickerWaiter(this));
	}
	
	@Override
	protected void takeDown() {
		//Deregister from the yellow pages
		try{
			DFService.deregister(this);
		}
		catch(FIPAException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * This behaviour would wait for a 'new day' message from the Ticker Agent!
	 * When the 'new day' message is received, daily behaviours would operate sequentially
	 */
	public class TickerWaiter extends CyclicBehaviour {
		//behaviour to wait for a new day
		public TickerWaiter(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchContent("new day"),
					MessageTemplate.MatchContent("terminate"));
			ACLMessage msg = myAgent.receive(mt); 
			if(msg != null) {
				if(tickerAgent == null) {
					tickerAgent = msg.getSender();
				}
				if(msg.getContent().equals("new day")) {
					SequentialBehaviour dailyActivity = new SequentialBehaviour();
					dailyActivity.addSubBehaviour(new FindCustomers(myAgent));
					dailyActivity.addSubBehaviour(new FindSuppliersOne(myAgent));
					dailyActivity.addSubBehaviour(new FindSuppliersTwo(myAgent));
					dailyActivity.addSubBehaviour(new OffersServer(myAgent));
					dailyActivity.addSubBehaviour(new OrdersServerOne(myAgent));
					dailyActivity.addSubBehaviour(new OrdersServerTwo(myAgent));
					dailyActivity.addSubBehaviour(new recieveComponentsOne(myAgent));
					dailyActivity.addSubBehaviour(new recieveComponentsTwo(myAgent));
					dailyActivity.addSubBehaviour(new assembleandship(myAgent));
					dailyActivity.addSubBehaviour(new EndDay(myAgent)); 
					myAgent.addBehaviour(dailyActivity);
				}
				else {
					myAgent.doDelete();
				}
			}
			else{
				block();
			}
		}
	}
	
	/*
	 * Find each customer from the yellow pages and then add it to a Array List
	 */
	public class FindCustomers extends OneShotBehaviour{

		public FindCustomers(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			DFAgentDescription buyerTemplate = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("Customer");
			buyerTemplate.addServices(sd);
			try{
				customers.clear();
				DFAgentDescription[] agentsType1  = DFService.search(myAgent,buyerTemplate); 
				for(int i=0; i<agentsType1.length; i++){
					customers.add(agentsType1[i].getName()); // this is the AID
				}
			}
			catch(FIPAException e) {
				e.printStackTrace();
			}

		}
		
	}
	
	/*
	 * Find supplier number 1 from the yellow pages and then add it to a Array List
	 */
	public class FindSuppliersOne extends OneShotBehaviour{
		public FindSuppliersOne(Agent a) {
			super(a); 
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub
			DFAgentDescription buyerTemplate2 = new DFAgentDescription();
			ServiceDescription sd2 = new ServiceDescription();
			sd2.setType("Supplier");
			buyerTemplate2.addServices(sd2);
			try{
				suppliers.clear();
				DFAgentDescription[] agentsType2  = DFService.search(myAgent,buyerTemplate2); 
				for(int i=0; i<agentsType2.length; i++){
					suppliers.add(agentsType2[i].getName()); // this is the AID
				}
			}
			catch(FIPAException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/*
	 * Find supplier number 2 from the yellow pages and then add it to a Array List
	 */
	public class FindSuppliersTwo extends OneShotBehaviour{
		public FindSuppliersTwo(Agent a) {
			super(a); 
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub
			DFAgentDescription buyerTemplate3 = new DFAgentDescription();
			ServiceDescription sd3 = new ServiceDescription();
			sd3.setType("SupplierTwo");
			buyerTemplate3.addServices(sd3);
			try{
				suppliersTwo.clear();
				DFAgentDescription[] agentsType3  = DFService.search(myAgent,buyerTemplate3); 
				for(int i=0; i<agentsType3.length; i++){
					suppliersTwo.add(agentsType3[i].getName()); // this is the AID
				}
			}
			catch(FIPAException e) {
				e.printStackTrace();
			}
		}	
	}
	
 
    /*
     *The OffersServer behaviour within the Manufacturer Agent is a Sequence Behaviour which would receive an Order Action request from each 
     *customer and extract the message and check whether or not that the (todayQuantity + Customer smartphones order quantity <= 50) and 
     *the (Due Date is >= 4 or per day penalty is equal to 0). If the condition is true, the manufacturer would add the smartphone to an ArrayList
     * and reply with an ACCEPT_PROPOSAL (“Accept”) message to the Customer. Otherwise, the manufacturer would send a REJECT_PROPOSAL (“Decline”) 
     * message to the customer. This behaviour would keep recieving messages from customers until the number of replies is equal to numbers
     * of customers
     */
	public class OffersServer extends Behaviour{
		int numOfReplies = 0;
		 
		public OffersServer(Agent a) {
			super(a);
		}
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null) {
				numOfReplies++; 
				ACLMessage reply = msg.createReply();
				try {
					ContentElement ce = null;
					// Let JADE convert from String to Java objects
					// Output will be a ContentElement
					ce = getContentManager().extractContent(msg);
					if (ce instanceof Action) {
						Concept action = ((Action)ce).getAction(); 
						if(action instanceof Order) {
							Order order = (Order) action; 
							Smartphone smartphone = order.getSmartphone();
							if((quantity + smartphone.getQuantity()) <= 50 && (smartphone.getDueDate() >= 4 || smartphone.getPerDayPenality() == 0) ){
								todaySmartphones.add(smartphone); 
								smartphones.add(smartphone);
								reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
								reply.setContent("Accept");
								quantity += smartphone.getQuantity();
								
							}
							else {
								reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
								reply.setContent("Decline!");
							}
							myAgent.send(reply);
						}
					}
				}
				catch (CodecException ce) {
					ce.printStackTrace();
				}
				catch (OntologyException oe) {
					oe.printStackTrace();
				}
			}
			else {
				block();
			}
		}
		
		@Override
		public boolean done() {
			return (numOfReplies == customers.size());
		}
	}

	/*
	 * The OrdersServerOne is a OneShotBehaviour that loops through the current day accepted ordered Smartphones, 
	 * and within the loop, the behaviour transmits a Purchase Action Request to Supplier One to order the appropriate 
	 * Screen and Battery with appropriate sizes and calculates the components price and sends the price to SupplierOne as message content. 
	 */
	public class OrdersServerOne extends OneShotBehaviour {
		public OrdersServerOne(Agent a) {
			super(a);
		}
		@Override
		public void action() {
			for(Smartphone smartphone : todaySmartphones) {		
				Screen screen = smartphone.getScreen();
				Battery battery = smartphone.getBattery(); 
				battery.setQuantity(smartphone.getQuantity());
				screen.setQuantity(smartphone.getQuantity());
				numberOfComponentsOrdered += 2;
				ACLMessage purchaseMsg = new ACLMessage(ACLMessage.REQUEST);
				for(AID supplier : suppliers) {
					purchaseMsg.addReceiver(supplier);
				}
				purchaseMsg.setLanguage(codec.getName());
				purchaseMsg.setOntology(ontology.getName());
				Purchase purchase = new Purchase();
				purchase.setManufacturerAID(myAgent.getAID());
				purchase.setBattery(battery);
				purchase.setScreen(screen);
				purchase.setSupplierType("Supplier1");
				double screenPrice = 0;
				double batteryPrice = 0;
				double totalPrice = 0;
				if(screen.getSize().equals("5inch")) {
					screenPrice = 100;
				}
				else {
					screenPrice = 150;
				}
				if(battery.getSize().equals("2000mAh")) {
					batteryPrice = 70;
				}
				else {
					batteryPrice = 100;
				}
				totalPrice = (screenPrice + batteryPrice) * smartphone.getQuantity(); 
				totalComponentsCost = totalComponentsCost - totalPrice; 
				purchase.setTotalPrice(totalPrice);
				Action request = new Action();
				request.setAction(purchase);
				for(AID supplier : suppliers) {
					request.setActor(supplier);
				}
				try {
					getContentManager().fillContent(purchaseMsg, request);
					send(purchaseMsg); 
				}
				catch(CodecException ce) {
					ce.printStackTrace();
				}
				catch(OntologyException oe) {
					oe.printStackTrace();
				}
			}	
		}
	}
	
	/*
	 * The OrdersServerTwo behaviour is a OneShotBehaviour that loops through the current day accepted ordered Smartphones, 
	 * and within the loop, the behaviour transmits a Purchase Action Request to Supplier Two to order the appropriate Storage 
	 * and RAM with appropriate sizes and calculates the components price and sends the price to Supplier Two as message content.
	 */
	public class OrdersServerTwo extends OneShotBehaviour {
		public OrdersServerTwo(Agent a) {
			super(a);
		}
		@Override
		public void action() {	
			for(Smartphone smartphone : todaySmartphones) {		
				Storage storage = smartphone.getStorage();
				RAM ram = smartphone.getRam();
				ram.setQuantity(smartphone.getQuantity());
				storage.setQuantity(smartphone.getQuantity());
				numberOfComponentsOrdered2 += 2; 
				
				ACLMessage purchaseMsg = new ACLMessage(ACLMessage.REQUEST);
				for(AID supplier : suppliersTwo) {
					purchaseMsg.addReceiver(supplier);
				}
				purchaseMsg.setLanguage(codec.getName());
				purchaseMsg.setOntology(ontology.getName());
				Purchase purchase = new Purchase();
				purchase.setManufacturerAID(myAgent.getAID());
				purchase.setStorage(storage);
				purchase.setRam(ram);
				purchase.setSupplierType("Supplier2");
				int ramPrice = 0;
				int storagePrice = 0;
				double totalPrice = 0;
				if(storage.getSize().equals("64GB")) {
					storagePrice = 15;
				}
				else {
					storagePrice = 40;
				}
				if(ram.getSize().equals("4GB")) {
					ramPrice = 20;
				}
				else {
					ramPrice = 35;
				}
				totalPrice = (storagePrice + ramPrice)* smartphone.getQuantity();  
				totalComponentsCost = totalComponentsCost - totalPrice; 
				purchase.setTotalPrice(totalPrice);
				Action request = new Action();
				request.setAction(purchase);
				for(AID supplier : suppliersTwo) {
					request.setActor(supplier);
				}
				try {
					getContentManager().fillContent(purchaseMsg, request);
					send(purchaseMsg); 
				}
				catch(CodecException ce) {
					ce.printStackTrace();
				}
				catch(OntologyException oe) {
					oe.printStackTrace();
				}
			}
			
			
		}
	}
	
	/*
	 *keep receiving Deliver Request Action messages that from supplier one sequentially until the number of replies is equal to the 
	 *number of components ordered from Supplier one in the same day or the number of parts ordered is 0. 
	 *Each message received, the behaviour would extract the message and add the received component to an array list. 
	 *The Manufacturer would then reply to the message with a ACCEPT_PROPOSAL (“Success”) message.
	 */
	public class recieveComponentsOne extends Behaviour {
		private int count = 0; 
		boolean block = false; 
		public recieveComponentsOne(Agent a) {
			super(a);
		}
		@Override
		public void action() {
			// TODO Auto-generated method stub
			if(numberOfComponentsOrdered != 0) {
				MessageTemplate mt = MessageTemplate.MatchConversationId("Delivery");
				ACLMessage msg = myAgent.receive(mt);
				if(msg != null) {
					ACLMessage reply = msg.createReply();
					try {
						ContentElement ce = null;
						ce = getContentManager().extractContent(msg);
						if(ce instanceof Action) {
							Concept action = ((Action)ce).getAction(); 
							if(action instanceof Deliver) {
								count++; 
								Deliver deliver = (Deliver) action; 
								Component component = deliver.getComponent();
								//System.out.println("Component Quantity is " + component.getQuantity());
								System.out.println("Delivery time is " + component.getDeliveryTime());
								deliveredComponents.add(component);
								reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
								reply.setContent("Success");
								myAgent.send(reply);	
							}
						}
					}catch (CodecException ce) {
						ce.printStackTrace();
					}
					catch (OntologyException oe) {
						oe.printStackTrace();
					}
				} 
				else {
					block(); 
				}
			}
			else {
				block = true;
			}
		}
		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return (count == (numberOfComponentsOrdered) || block == true);
		}
	}
	
	/*
	 * This behaviour keeps receiving Deliver Request Action messages from supplier two sequentially until the number of replies is 
	 * equal to the number of components ordered from Supplier two on the same day, or the number of parts ordered is 0. 
	 * Each message received, the behaviour would extract the message and add the received component to an array list.
	 *  The Manufacturer would then reply to the message with a ACCEPT_PROPOSAL (“Success”) message. 
	 */
	public class recieveComponentsTwo extends Behaviour {
		private int count = 0; 
		boolean block = false; 
		public recieveComponentsTwo(Agent a) {
			super(a);
		}
		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.MatchConversationId("DeliveryTwo");
			ACLMessage msg = myAgent.receive(mt);
			if(numberOfComponentsOrdered2 != 0) {
				if(msg != null) {
					ACLMessage reply = msg.createReply();
					try {
						ContentElement ce = null;
						ce = getContentManager().extractContent(msg);
						if(ce instanceof Action) {
							Concept action = ((Action)ce).getAction(); 
							if(action instanceof Deliver) {
								Deliver deliver = (Deliver) action; 
								Component component = deliver.getComponent();
								System.out.println("Delivery time is " + component.getDeliveryTime());
								deliveredComponents.add(component);
								reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
								reply.setContent("Success");
								myAgent.send(reply);
								
								count++; 
							}
						}
					}catch (CodecException ce) {
						ce.printStackTrace();
					}
					catch (OntologyException oe) {
						oe.printStackTrace();
					}
				} 
				else {
					block(); 
				}
			}
			else {
				block = true; 
			}
			
		}
		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return (count == (numberOfComponentsOrdered2) || block == true);
		}
	}
	
	/*
	  The assembleandship behaviour is a OneShotBehaviour of the Manufacturer agent which uses the components within the warehouse and uses these 
	  components to assemble from 1 to 50 smartphones. 
	 * Each constructed smartphone delivered to the appropriate Customer through a Ship Action Request message. 
	 * Each time the manufacturer transmits 
	 * smartphones to a customer, a profit would increase by number of smartphones * unit price. The used components would be removed from the 
	 * warehouse.  
	 */
	public class assembleandship extends OneShotBehaviour{
		
		public assembleandship(Agent a) {
			super(a);
		}
		@Override
		public void action() {
			for(int j = 0; j < smartphones.size(); j++) {
				Smartphone newSmartphone = smartphones.get(j);
				//This make sure number of smartphones that is being assembled today is less than or equal to 50
				if((numberOfAssembledSmartphones +smartphones.get(j).getQuantity()) <= 50) {
					for(int i = 0; i < warehouse.size(); i++) {
						if(smartphones.get(j).getBattery().getSize().equals(warehouse.get(i).getSize()) 
								&& smartphones.get(j).getBattery().getQuantity() <= warehouse.get(i).getQuantity()) {
							newSmartphone.setBattery(smartphones.get(j).getBattery());
						}
						if(smartphones.get(j).getRam().getSize().equals(warehouse.get(i).getSize()) && 
								smartphones.get(j).getRam().getQuantity() == warehouse.get(i).getQuantity()) {
							newSmartphone.setRam(smartphones.get(j).getRam());
						}
						if(smartphones.get(j).getScreen().getSize().equals(warehouse.get(i).getSize()) && 
								smartphones.get(j).getScreen().getQuantity() == warehouse.get(i).getQuantity()) {
							newSmartphone.setScreen(smartphones.get(j).getScreen());
						}
						if(smartphones.get(j).getStorage().getSize().equals(warehouse.get(i).getSize()) && 
								smartphones.get(j).getStorage().getQuantity() == warehouse.get(i).getQuantity()) {
							newSmartphone.setStorage(smartphones.get(j).getStorage());
						}
							
					}
					//smartphone is only shipped if it has all of the needed components
					if(newSmartphone.getBattery() != null  && newSmartphone.getRam() != null && newSmartphone.getScreen() != null && 
							newSmartphone.getStorage() != null) {
						newSmartphone.setQuantity(smartphones.get(j).getQuantity());
						newSmartphone.setCustomerAID(smartphones.get(j).getCustomerAID());
						newSmartphone.setDueDate(smartphones.get(j).getDueDate());
						newSmartphone.setPerDayPenality(smartphones.get(j).getPerDayPenality());
						newSmartphone.setPrice(smartphones.get(j).getPrice());
						newSmartphone.setWaitingTime(smartphones.get(j).getWaitingTime());
						//part of the totalProfit calculation
						totalProfit += (newSmartphone.getPrice() * newSmartphone.getQuantity()); 
						smartphones.remove(j);
						numberOfAssembledSmartphones = numberOfAssembledSmartphones + newSmartphone.getQuantity();
						double waitTime = 0;
						waitTime = newSmartphone.getWaitingTime() - newSmartphone.getDueDate();
						if(waitTime > 0) {
							double penaltyCost = 0; 
							//penalty cost is calculated
							penaltyCost = (int) (waitTime * newSmartphone.getPerDayPenality());
							totalPenaltyCost += penaltyCost; 
						}
						else {
							
						}
						for(int i = 0; i < warehouse.size(); i++) {
							if((warehouse.get(i).getSize().equals(newSmartphone.getBattery().getSize()) || warehouse.get(i).getSize().equals(newSmartphone.getRam().getSize()) || warehouse.get(i).getSize().equals(newSmartphone.getScreen().getSize()) || warehouse.get(i).getSize().equals(newSmartphone.getStorage().getSize()) && warehouse.get(i).getQuantity() == newSmartphone.getQuantity())) {
								warehouse.remove(i); 
								
							}
						}
						ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
						msg.addReceiver(newSmartphone.getCustomerAID()); 
						msg.setLanguage(codec.getName());
						msg.setOntology(ontology.getName());
						msg.setConversationId("Ship");
						Ship ship = new Ship();
						ship.setManufacturerAid(myAgent.getAID());
						ship.setSmartphone(newSmartphone);	
						Action request = new Action();
						request.setAction(ship);
						request.setActor(newSmartphone.getCustomerAID());
						try {
							getContentManager().fillContent(msg, request);
							send(msg); 
						}
						catch(CodecException ce) {
							ce.printStackTrace();
						}
						catch(OntologyException oe) {
							oe.printStackTrace();
						}
						
					}
					
				}
			}
		}
	}

	public class EndDay extends OneShotBehaviour {
			
		public EndDay(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			System.out.println("Warehouse size is " + warehouse.size());
			//Calculates the per-day per-component warehouse storage cost
			for(int i = 0; i < warehouse.size(); i++) {
				int totalWareHouseCost = 0; 
				Component component = warehouse.get(i);
				warehouseCost += (10 * component.getQuantity());  
				
			}
			for(int k = 0; k < deliveredComponents.size(); k++) {
				//Component is added to the warehouse when delivery time is 0
				if(deliveredComponents.get(k).getDeliveryTime() == 0) {
					Component component = deliveredComponents.get(k);
					warehouse.add(component);					
					deliveredComponents.remove(k);
				}
				else {
					Component component = new Component(); 
					//component delivery time decremented by 1 
					component.setDeliveryTime(deliveredComponents.get(k).getDeliveryTime() - 1);
					component.setQuantity(deliveredComponents.get(k).getQuantity());
					component.setSize(deliveredComponents.get(k).getSize());
					deliveredComponents.set(k, component);
				}
			}	
			for(int j = 0; j < smartphones.size(); j++) {
				Smartphone smartphone = smartphones.get(j);
				smartphone.setWaitingTime(smartphones.get(j).getWaitingTime() + 1);
				smartphones.set(j, smartphone);
			}
			todaySmartphones.clear();
			numberOfAssembledSmartphones = 0;
			numberOfComponentsOrdered = 0;
			numberOfComponentsOrdered2 = 0; 
			quantity = 0;
			//part of the totalProfit calculation
			totalProfit -= warehouseCost;
			totalProfit -= totalComponentsCost;
			totalProfit -= totalPenaltyCost;
			System.out.println("The total profit is £" + totalProfit);  
			warehouseCost = 0; 
			totalComponentsCost = 0;
			totalPenaltyCost = 0; 
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(tickerAgent);
			msg.setContent("done");
			myAgent.send(msg);
			ACLMessage supplierOneDone = new ACLMessage(ACLMessage.INFORM);
			supplierOneDone.setContent("done");
			for(AID supplier : suppliers) {
				supplierOneDone.addReceiver(supplier);
			}
			myAgent.send(supplierOneDone);
			ACLMessage supplierTwoDone = new ACLMessage(ACLMessage.INFORM);
			supplierTwoDone.setContent("done");
			for(AID supplier : suppliersTwo) {
				supplierTwoDone.addReceiver(supplier);
			}
			myAgent.send(supplierTwoDone);
		}
		
	}
}