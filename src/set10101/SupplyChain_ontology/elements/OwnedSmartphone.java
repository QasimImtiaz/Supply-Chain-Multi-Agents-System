package set10111.SupplyChain_ontology.elements;

import jade.content.Predicate;

public class OwnedSmartphone implements Predicate{
	private Smartphone smartphone; 
	private int price;
	public Smartphone getSmartphone() {
		return smartphone;
	}
	public void setSmartphone(Smartphone smartphone) {
		this.smartphone = smartphone;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	} 
	
	
}
