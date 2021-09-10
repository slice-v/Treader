package trader.rate.models;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;



public class Product {

	
	private String name;
	
	private HashMap<Float, HashMap<String, Float>> price;
	private ZonedDateTime priceDate;
	
	
	
	
	@Override
	public String toString() {
		
		StringBuilder result = new StringBuilder();
		
		result.append("Product: " + name + "\n");
		result.append("Price Date and time: " + priceDate + "\n");
		result.append("Rates: " + "\n");
		
		for (Float count : price.keySet()) {
			
			result.append("volume: " + count + "\t");
			
				for (String action : price.get(count).keySet()) {
					
					result.append(action + ": " + price.get(count).get(action) + "\t");
					
				}
			result.append("\n");
		}
		
		return result.toString();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public ZonedDateTime getPriceDate() {
		return priceDate;
	}
	public void setPriceDate(ZonedDateTime priceDate) {
		this.priceDate = priceDate;
	}
	public HashMap<Float, HashMap<String, Float>> getPrice() {
		return price;
	}
	public void setPrice(HashMap<Float, HashMap<String, Float>> price) {
		this.price = price;
	}
}
