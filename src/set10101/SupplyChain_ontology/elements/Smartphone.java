package set10111.SupplyChain_ontology.elements;
import jade.content.Concept;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

public class Smartphone implements Concept {
	//Variables:
	private Storage storage;
	private RAM ram;
	private Screen screen; 
	private Battery battery; 
	private double quantity; 
	private double price; 
	private double dueDate; 
	private double perDayPenality; 
	private AID customerAID;
	private int waitingTime; 
	
	//Get and set methods
	@Slot(mandatory = true)
	public Storage getStorage() {
		return storage;
	}
	public void setStorage(Storage storage) {
		this.storage = storage;
	}
	
	@Slot(mandatory = true)
	public RAM getRam() {
		return ram;
	}
	public void setRam(RAM ram) {
		this.ram = ram;
	}
	
	@Slot(mandatory = true)
	public Screen getScreen() {
		return screen;
	}
	public void setScreen(Screen screen) {
		this.screen = screen;
	}
	
	@Slot(mandatory = true)
	public Battery getBattery() {
		return battery;
	}
	public void setBattery(Battery battery) {
		this.battery = battery;
	}
	@Slot(mandatory = true)
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	@Slot(mandatory = true)
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	@Slot(mandatory = true)
	public double getDueDate() {
		return dueDate;
	}
	public void setDueDate(double dueDate) {
		this.dueDate = dueDate;
	}
	@Slot(mandatory = true)
	public double getPerDayPenality() {
		return perDayPenality;
	}
	public void setPerDayPenality(double perDayPenality) {
		this.perDayPenality = perDayPenality;
	}
	public AID getCustomerAID() {
		return customerAID;
	}
	@Slot(mandatory = true)
	public void setCustomerAID(AID customerAID) {
		this.customerAID = customerAID;
	}
	
	@Slot(mandatory = true)
	public int getWaitingTime() {
		return waitingTime;
	}
	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	} 
	
}
