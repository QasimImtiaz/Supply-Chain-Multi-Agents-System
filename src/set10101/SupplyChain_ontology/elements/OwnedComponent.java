package set10111.SupplyChain_ontology.elements;

import jade.content.Predicate;
import jade.core.AID;

public class OwnedComponent implements Predicate {
	private Component component; 
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
