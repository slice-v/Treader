package tests.Compare;

import java.time.ZonedDateTime;

public class testProdComp {

public static void main(String[] args) {
	
	ProductSaveToDb product1 = new ProductSaveToDb();
	product1.setName("Золото");
	product1.setCount(10F);
	product1.setBuyPrice(4000.00F);
	product1.setSellPrice(3500.01F);
	product1.setPriceDate(ZonedDateTime.now());
	
	
	
	ProductSaveToDb product2 = new ProductSaveToDb();
	
	product2.setName("Золото");
	product2.setCount(10F);
	product2.setBuyPrice(4000.00F);
	product2.setSellPrice(3500.01F);
	product2.setPriceDate(ZonedDateTime.now());
	
	System.out.println(product1.compareTo(product2));
	
}

}
