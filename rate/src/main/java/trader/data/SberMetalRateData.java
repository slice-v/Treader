package trader.data;

import javax.persistence.*;

import java.util.*;

import trader.rate.models.Product;
import trader.rate.parsers.SberMetalPharser;

@Entity
public class SberMetalRateData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	
	@ElementCollection
	@CollectionTable(name = "Product")
	List<Product> MetalProduct = new ArrayList<Product>();

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	
		
	
	public List<Product> getMetalProduct() {
		
		SberMetalPharser sberMetalPharser = new SberMetalPharser();
		
		MetalProduct = sberMetalPharser.getMetalProducts();
		
		return MetalProduct;
	}

	public void setMetalProduct(ArrayList<Product> metalProduct) {
		MetalProduct = metalProduct;
	}
	
			
	

		
	
	
}
