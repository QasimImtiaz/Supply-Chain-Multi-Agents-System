package set10111.SupplyChain_ontology.elements;

import jade.content.AgentAction;
import jade.core.AID;

public class Purchase implements AgentAction {
	private Battery battery; 
	private Screen screen; 
	private RAM ram;
	private Storage storage; 
	private AID manufacturerAID;
	private String supplierType; 
	private double totalPrice; 
	
	public AID getManufacturerAID() {
		return manufacturerAID;
	}
	public void setManufacturerAID(AID manufacturerAID) {
		this.manufacturerAID = manufacturerAID;
	} 
	public String getSupplierType() {
		return supplierType;
	}
	public void setSupplierType(String supplierType) {
		this.supplierType = supplierType;
	}
	public Battery getBattery() {
		return battery;
	}
	public void setBattery(Battery battery) {
		this.battery = battery;
	}
	public Screen getScreen() {
		return screen;
	}
	public void setScreen(Screen screen) {
		this.screen = screen;
	}
	public RAM getRam() {
		return ram;
	}
	public void setRam(RAM ram) {
		this.ram = ram;
	}
	public Storage getStorage() {
		return storage;
	}
	public void setStorage(Storage storage) {
		this.storage = storage;
	}
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice2) {
		this.totalPrice = totalPrice2;
	}
		
}
