/* Pharse page "https://www.sberbank.ru/ru/quotes/metalbeznal"
 * Table XPath: "/html/body/div[1]/div[2]/div[2]/div[3]/div[1]/div/div/div/div/div/div[2]/div/div/div[2]/table"
 * 
 * 
 * */

package trader.rate.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static java.util.Map.entry;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;

import trader.rate.models.Product;
import trader.rate.models.ProductSaveToDb;

public class SberMetalPharser {

	private WebElement rateTable;

	private Product getProductRate(ArrayList<WebElement> rows, int startRow, int EndRow) {

		Product result = new Product();
		HashMap<Float, HashMap<String, Float>> price = new HashMap<Float, HashMap<String, Float>>();

		Float count;
		Float buyPrice;
		Float sellPrice;
		
		
		while (startRow <= EndRow) {

			HashMap<String, Float> tmpPrice = new HashMap<String, Float>();
			ArrayList<WebElement> cells = (ArrayList<WebElement>) rows.get(startRow).findElements(By.tagName("td"));

			if (!cells.get(0).getText().isEmpty())
				result.setName(cells.get(0).getText().replaceAll("\\s+", ""));

			count = Float.parseFloat(cells.get(1).getText().replaceAll("\\W+", "")); // количество
			
			sellPrice = Float.parseFloat(cells.get(2).getText().replaceAll("[^0-9,]", "").replaceAll(",", ".")); // продать
			buyPrice = Float.parseFloat(cells.get(3).getText().replaceAll("[^0-9,]", "").replaceAll(",", ".")); // купить
			tmpPrice.put("Sell", sellPrice);
			tmpPrice.put("Buy", buyPrice);
			
			price.put(count, tmpPrice);

			startRow++;
		}

		result.setPrice(price);
		result.setPriceDate(ZonedDateTime.now().withNano(0));
		return result;
	}

	public ArrayList<Product> getMetalProducts() {

		ArrayList<Product> result = new ArrayList<Product>();
		
		System.setProperty("webdriver.chrome.driver",
				"D:\\Programming\\Projects\\Java\\Trader\\Java\\RatePharser\\chromedriver_92_0_4515_107\\chromedriver.exe");

		WebDriver driver = new ChromeDriver();
		
		Product gold = new Product();
		Product silver = new Product();
		Product platinum = new Product();
		Product palladium = new Product();

		driver.get("https://www.sberbank.ru/ru/quotes/metalbeznal");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		// driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[3]/div[1]/div/div/div/div/div/div[2]/div/div/div[2]/table"));
		rateTable = driver.findElement(By.tagName("table"));

		ArrayList<WebElement> rows = (ArrayList<WebElement>) rateTable.findElements(By.tagName("tr"));
		
		gold = getProductRate(rows, 2, 4);
		result.add(gold);
		
		silver = getProductRate(rows, 6, 8);
		result.add(silver);
		
		platinum = getProductRate(rows, 10, 12);
		result.add(platinum);
		
		palladium = getProductRate(rows, 14, 16);
		result.add(palladium);

		driver.close();
		
		return result;

	}
	
	public ArrayList<ProductSaveToDb> getMetalProductsSaveToDb () {
		
		ArrayList<ProductSaveToDb> result = new ArrayList<ProductSaveToDb>();
				
		ArrayList<Product> products = getMetalProducts();
		
		for (Product tmpProduct : products) {
			
			for (Float count : tmpProduct.getPrice().keySet()) {
				
				ProductSaveToDb productSaveToDb = new ProductSaveToDb();
				
				for (String tmpAction : tmpProduct.getPrice().get(count).keySet())
				{
					
					
					productSaveToDb.setName(tmpProduct.getName());
					productSaveToDb.setPriceDate(tmpProduct.getPriceDate());
					productSaveToDb.setCount(count);
					
					
					
					switch (tmpAction) {
					
					case "Sell" :
							productSaveToDb.setSellPrice(tmpProduct.getPrice().get(count).get(tmpAction));
							break;
							
					case "Buy" :
							productSaveToDb.setBuyPrice(tmpProduct.getPrice().get(count).get(tmpAction));
							break;
					}
			
				}
				result.add(productSaveToDb);
			}
							
		}
		
	return result;
	}
}
