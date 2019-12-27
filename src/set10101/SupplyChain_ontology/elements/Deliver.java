package set10111.SupplyChain_ontology.elements;


import java.util.ArrayList;

import jade.content.AgentAction;
import jade.core.AID;

public class Deliver implements AgentAction {
	private Component component; 
	private AID supplierAID;
	
	public AID getSupplierAID() {
		return supplierAID;
	}

	public void setSupplierAID(AID supplierAID) {
		this.supplierAID = supplierAID;
	}
	
	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}
}
