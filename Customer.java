package set10111.SupplyChain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
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
import set10111.SupplyChain.Manufacturer.EndDay;
import set10111.SupplyChain_ontology.SupplyChainOntology;
import set10111.SupplyChain_ontology.elements.Battery;
import set10111.SupplyChain_ontology.elements.Order;
import set10111.SupplyChain_ontology.elements.Phablet_Smartphone;
import set10111.SupplyChain_ontology.elements.RAM;
import set10111.SupplyChain_ontology.elements.Screen;
import set10111.SupplyChain_ontology.elements.Ship;
import set10111.SupplyChain_ontology.elements.Small_Smartphone;
import set10111.SupplyChain_ontology.elements.Smartphone;
import set10111.SupplyChain_ontology.elements.Storage;




public class Customer extends Agent {
	private ArrayList<AID> manufacturers = new ArrayList<>();
	private ArrayList<Smartphone>  smartphonesToBuy = new ArrayList<>();
	private AID tickerAgent;
	private int numQueriesSent;
	private int totalPrice = 0; 
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplyChainOntology.getInstance();
	private boolean flag = false; 
	
	
	protected void setup(){
		//register language and ontology
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		//add this agent to the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Customer");
		sd.setName(getLocalName() + "-Customer");
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
					//spawn new sequential behaviour for day's activities
					SequentialBehaviour dailyActivity = new SequentialBehaviour();
					//sub-behaviours will execute in the order they are added
					dailyActivity.addSubBehaviour(new FindManufacturers(myAgent));
					dailyActivity.addSubBehaviour(new OrderSmartphones(myAgent));
				    //dailyActivity.addSubBehaviour(new getSmartphones(myAgent));
				    
					dailyActivity.addSubBehaviour(new EndDay(myAgent));
					//dailyActivity.addSubBehaviour(new CollectOffers(myAgent));
					
					//dailyActivity.addSubBehaviour(new EndDay(myAgent));
					myAgent.addBehaviour(dailyActivity);
				}
				else {
					//termination message to end simulation
					myAgent.doDelete();
				}
			}
			else{
				block();
			}
		}

	}
	
	public class FindManufacturers extends OneShotBehaviour {
		public FindManufacturers(Agent a) {
			super(a); 
		}

		@Override
		public void action() {
			DFAgentDescription manufacturerTemplate = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("Manufacturer");
			manufacturerTemplate.addServices(sd);
			try{
				manufacturers.clear();
				DFAgentDescription[] agentsType1  = DFService.search(myAgent,manufacturerTemplate); 
				
				manufacturers.add(agentsType1[0].getName()); // this is the AID
				
			}
			catch(FIPAException e) {
				e.printStackTrace();
			}
			
		}
	}

	/*
	 * This Behaviour would send an Order Action request to the Manufacturer to buy a 
	 * random amount of smartphones ranging from 1 to 50 which can be a 
	 * Phablet or Small Smartphone with specific 
	 * sizes of the battery, storage, screen and RAM components
	 */
	private class OrderSmartphones extends OneShotBehaviour{
		
		public OrderSmartphones(Agent a) {
			super(a); 
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub
			double randomValue = (Math.random() * 1);
			if(randomValue < 0.5) {
				ACLMessage enquiry = new ACLMessage(ACLMessage.REQUEST);
				enquiry.addReceiver(manufacturers.get(0));
				enquiry.setLanguage(codec.getName());
				enquiry.setOntology(ontology.getName());
				Small_Smartphone ss = new Small_Smartphone(); 
				Screen screen = new Screen();
				//Making sure screen size is 5 inch
				screen.setSize("5inch");
				ss.setScreen(screen);
				Battery battery = new Battery(); 
				//Making sure the battery size is 2000mAh
				battery.setSize("2000mAh");
				ss.setBattery(battery);
				RAM ram = new RAM(); 
				Storage storage = new Storage();
				double randomValue2 = (Math.random() * 1);
				//Making sure the ram and storage has only one value
				if(randomValue2 < 0.5) {
					ram.setSize("4GB"); 
					storage.setSize("64GB");
				}
				else {
					ram.setSize("8GB");
					storage.setSize("256GB");
				}
				ss.setRam(ram);
				ss.setStorage(storage);
				Random random1 = new Random();
				double numberOfSmartphones = Math.floor(random1.nextInt((50 - 1) + 1) + 1);
				ss.setQuantity(numberOfSmartphones);
				Random random2 = new Random(); 
				ss.setPrice(Math.floor(random2.nextInt((500 - 100) + 100) + 100));
				Random random3 = new Random(); 
				ss.setDueDate(Math.floor((random3.nextInt((10 - 1) + 1) + 1))); 
				Random random4 = new Random(); 
				ss.setPerDayPenality(numberOfSmartphones * Math.floor(random4.nextInt((50 - 1) + 1) + 1));
			    ss.setCustomerAID(myAgent.getAID());
				Order order = new Order(); 
				ss.setWaitingTime(0);
				order.setSmartphone(ss);
				Action request = new Action();
				request.setAction(order);
				request.setActor(manufacturers.get(0));
				try {
				 getContentManager().fillContent(enquiry, request); //send the wrapper object
				 send(enquiry);
				}
				catch (CodecException ce) {
				 ce.printStackTrace();
				}
				catch (OntologyException oe) {
				 oe.printStackTrace();
				} 
			}
			else {
				ACLMessage enquiry = new ACLMessage(ACLMessage.REQUEST);
				enquiry.addReceiver(manufacturers.get(0));
				
				enquiry.setLanguage(codec.getName());
				enquiry.setOntology(ontology.getName());
				Phablet_Smartphone ps = new Phablet_Smartphone();
				Screen screen = new Screen();
				//Making sure the screen size is 7 inch
				screen.setSize("7inch");
				ps.setScreen(screen);
				Battery battery = new Battery(); 
				//Making sure the battery size is 3000mAh
				battery.setSize("3000mAh");
				ps.setBattery(battery);
				RAM ram = new RAM(); 
				Storage storage = new Storage();
				double randomValue2 = (Math.random() * 1);
				//Making sure the ram and storage both has one value
				if(randomValue2 < 0.5) {
					ram.setSize("4GB"); 
					storage.setSize("64GB");
				}
				else {
					ram.setSize("8GB");
					storage.setSize("256GB");
				}
				ps.setRam(ram);
				ps.setStorage(storage);	 
				Random random1 = new Random();
				double numberOfSmartphones = Math.floor(random1.nextInt((50 - 1) + 1) + 1);
				ps.setQuantity(numberOfSmartphones);
				Random random2 = new Random(); 
				ps.setPrice(Math.floor(random2.nextInt((500 - 100) + 100) + 100));
				Random random3 = new Random(); 
				ps.setDueDate(Math.floor((random3.nextInt((10 - 1) + 1) + 1))); 
				Random random4 = new Random(); 
				ps.setPerDayPenality(numberOfSmartphones * Math.floor(random4.nextInt((50 - 1) + 1) + 1));
				ps.setCustomerAID(myAgent.getAID());
				ps.setWaitingTime(0);
				Order order = new Order(); 
				order.setSmartphone(ps);
				Action request = new Action();
				request.setAction(order);
				request.setActor(manufacturers.get(0));
				try {
					getContentManager().fillContent(enquiry, request); //send the wrapper object
					send(enquiry);
				}
				catch (CodecException ce) {
					ce.printStackTrace();
				}
				catch (OntologyException oe) {
					oe.printStackTrace();
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
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(tickerAgent);
			msg.setContent("done");
			myAgent.send(msg);
		}		
	}
}
