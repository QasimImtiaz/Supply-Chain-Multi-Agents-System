package set10111.SupplyChain;
import java.util.ArrayList;
import java.util.List;

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
import set10111.SupplyChain_ontology.SupplyChainOntology;
import set10111.SupplyChain_ontology.elements.Battery;
import set10111.SupplyChain_ontology.elements.Component;
import set10111.SupplyChain_ontology.elements.Deliver;
import set10111.SupplyChain_ontology.elements.Purchase;
import set10111.SupplyChain_ontology.elements.RAM;
import set10111.SupplyChain_ontology.elements.Screen;
import set10111.SupplyChain_ontology.elements.Storage;

public class Supplier extends Agent {
	private ArrayList<AID> manufacturers = new ArrayList<>();
	private AID tickerAgent; 
	private boolean flag = false; 
	private ArrayList<Component> componentsOrders = new ArrayList<Component>(); 
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplyChainOntology.getInstance();
	protected void setup() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Supplier");
		sd.setName(getLocalName() + "-Supplier");
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
	 * This behaviour would wait for a new day and then all behaviours would be operating until the end of the day. 
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
					CyclicBehaviour os = new DeliverServer(myAgent);
					//doWait(1000);
					myAgent.addBehaviour(os);
					
					//myAgent.addBehaviour(new DeliverServer(myAgent));
					ArrayList<Behaviour> cyclicBehaviours = new ArrayList<>();
					cyclicBehaviours.add(os);
					myAgent.addBehaviour(new EndDayListener(myAgent,cyclicBehaviours));
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
	 * The DeliversServer behaviour Is a CyclicBehaviour that receives a Purchase Request message, 
	 * extract the message and add the ordered screen and battery within the purchase Request message to a Array List. 
	 * The behaviour would then reply with a ACCEPT_REQUEST (“Accept”) to the Manufacturer. It would then send a Deliver Action Request message to the
	 *  Manufacturer in order to send each ordered screen and battery to the manufacturer through a for loop.
	 */
	public class DeliverServer extends CyclicBehaviour{
		public DeliverServer(Agent a) {
			super(a);
		}
		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null) {
				ACLMessage reply = msg.createReply();
				try {
					ContentElement ce = null;	
					ce = getContentManager().extractContent(msg);
					if(ce instanceof Action) {
						Concept action = ((Action)ce).getAction(); 
						if(action instanceof Purchase) {
							Purchase purchase = (Purchase) action; 			
							Screen screen = purchase.getScreen();
							Battery battery = purchase.getBattery();
							componentsOrders.add(screen);
							componentsOrders.add(battery);
							reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
							reply.setContent("Accept");
							myAgent.send(reply);
							DFAgentDescription buyerTemplate = new DFAgentDescription();
							ServiceDescription sd = new ServiceDescription();
							sd.setType("Manufacturer");
							buyerTemplate.addServices(sd);
							try{
								manufacturers.clear();
								DFAgentDescription[] agentsType1  = DFService.search(myAgent,buyerTemplate); 
								for(int i=0; i<agentsType1.length; i++){
									manufacturers.add(agentsType1[i].getName()); // this is the AID
								}
							}
							catch(FIPAException e) {
								e.printStackTrace();
							}
							for(Component component : componentsOrders) {
								
								ACLMessage msg2 = new ACLMessage(ACLMessage.REQUEST);
								msg2.setConversationId("Delivery");
								for(AID manufacturer : manufacturers) {
									msg2.addReceiver(manufacturer);
									
								}
								msg2.setLanguage(codec.getName());
								msg2.setOntology(ontology.getName());
								Deliver deliver = new Deliver();
								deliver.setSupplierAID(myAgent.getAID());
								//Making sure the delivery time is 1
								component.setDeliveryTime(1);
								deliver.setComponent(component);			
								Action request = new Action();
								request.setAction(deliver);
								for(AID manufacturer : manufacturers) {
									request.setActor(manufacturer);
									
								}
								try {
									getContentManager().fillContent(msg2, request);
									send(msg2); 
								}
								catch(CodecException ce2) {
									ce2.printStackTrace();
								}
								catch(OntologyException oe) {
									oe.printStackTrace();
								}
								
							}
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
	}
	
	

	/*
	 * This behaviour would wait for a new day and then all behaviours would be operating until the end of the day. 
	 */
	public class EndDayListener extends CyclicBehaviour {
		private int buyersFinished = 0;
		private List<Behaviour> toRemove;
		
		public EndDayListener(Agent a, List<Behaviour> toRemove) {
			super(a);
			this.toRemove = toRemove;
		}

		@Override
		public void action() {
			flag = false; 
			MessageTemplate mt = MessageTemplate.MatchContent("done");
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null) {
				//we are finished
				componentsOrders.clear(); 
				ACLMessage tick = new ACLMessage(ACLMessage.INFORM);
				tick.setContent("done");
				tick.addReceiver(tickerAgent);
				myAgent.send(tick);
				//remove behaviours
				for(Behaviour b : toRemove) {
					myAgent.removeBehaviour(b);
				}
				myAgent.removeBehaviour(this);
			}
			else {
				block();
			}
		}
	}

}




