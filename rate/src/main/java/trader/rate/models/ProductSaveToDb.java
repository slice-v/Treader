package trader.rate.models;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.NamedNativeQuery;


@Entity
@NamedNativeQuery (name = "lastSavedProducts", query = "SELECT * FROM productsavetodb WHERE CAST (pricedate AS TEXT) LIKE ':date%'", resultClass = ProductSaveToDb.class)
public class ProductSaveToDb implements Comparable <ProductSaveToDb>{
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;
	
	String name;
	Float count;
	Float buyPrice;
	Float sellPrice;
	ZonedDateTime priceDate;
	
	@Override
	public String toString() {
		
		StringBuilder result = new StringBuilder();
		
		result.append("Название металла: " + name + "\n");
		result.append("\t" + "Дата стоимости: " + priceDate + "\n");
		result.append("\t" + "Количество металла: " + count + "\n");
		result.append("\t" + "Продать " + sellPrice + "\n");
		result.append("\t" + "Купить " + buyPrice + "\n");
		result.append("******************" + "\n");
		
		return result.toString();
	}
	
	@Override
	public int compareTo(ProductSaveToDb o) {
		
		if ((this.name.compareTo(o.name) == 0) 
				&& (this.count.compareTo(o.count) == 0)
				&& ((this.buyPrice.compareTo(o.buyPrice) != 0) 
				|| (this.sellPrice.compareTo(o.sellPrice) !=0)))
			return 1;
		else if ((this.name.compareTo(o.name) == 0) 
				&& (this.count.compareTo(o.count) == 0)
				&& (this.buyPrice.compareTo(o.buyPrice) == 0) 
				&& (this.sellPrice.compareTo(o.sellPrice) == 0))
			return 0;
		else 
			return -1;
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Float getCount() {
		return count;
	}
	public void setCount(Float count) {
		this.count = count;
	}
	public Float getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(Float buyPrice) {
		this.buyPrice = buyPrice;
	}
	public Float getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(Float sellPrice) {
		this.sellPrice = sellPrice;
	}
	public ZonedDateTime getPriceDate() {
		return priceDate;
	}
	public void setPriceDate(ZonedDateTime priceDate) {
		this.priceDate = priceDate;
	}
}
