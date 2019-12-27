package set10111.SupplyChain_ontology.elements;

import jade.content.AgentAction;
import jade.core.AID;

public class Sell implements AgentAction{
	private Component component;
	private int price;
	private int deliveryDays; 
	private AID supplierAID;
	public Component getComponent() {
		return component;
	}
	public void setComponent(Component component) {
		this.component = component;
	}

	public AID getSupplierAID() {
		return supplierAID;
	}
	public void setSupplierAID(AID supplierAID) {
		this.supplierAID = supplierAID;
	}
	
}
