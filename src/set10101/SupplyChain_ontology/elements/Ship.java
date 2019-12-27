package set10111.SupplyChain_ontology.elements;

import jade.content.AgentAction;
import jade.core.AID;

public class Ship implements AgentAction{
	private AID manufacturerAid;
	private Smartphone smartphone; 
	
	public AID getManufacturerAid() {
		return manufacturerAid;
	}
	public void setManufacturerAid(AID manufacturerAid) {
		this.manufacturerAid = manufacturerAid;
	}
	public Smartphone getSmartphone() {
		return smartphone;
	}
	public void setSmartphone(Smartphone smartphone) {
		this.smartphone = smartphone;
	} 
	
	
}
