package set10111.SupplyChain_ontology.elements;
import jade.content.Concept;
import jade.content.onto.annotations.Slot;

public class Component implements Concept{ 
	private String size; 
	private int deliveryTime;
	private double quantity;
	@Slot(mandatory = true)
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	@Slot(mandatory = true)
	public int getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(int deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	@Slot(mandatory = true)
	public double getQuantity() {
		return quantity;
	}
    
	public void setQuantity(double d) {
		this.quantity = d;
	}

}
