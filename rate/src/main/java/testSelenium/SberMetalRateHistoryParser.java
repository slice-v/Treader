package testSelenium;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import trader.rate.models.ProductSaveToDb;

import org.openqa.selenium.support.ui.ExpectedConditions;


//TODO: Delete 'statics'

public class SberMetalRateHistoryParser {
	
	private static WebElement webElement;
	private static WebElement rateHistoryTable;
	
    /********
     * 
     * Regular pattern expressions for Matcher for getting values from varible WebElement rate
     * 
     ********/
	
	private static String countPattern = "(\\от\\s+\\d+)";
    private static String timePattern = "(\\d{2}\\:\\d{2})";
    private static String pricePattern = "(\\d+\\s+\\d+\\,\\d{2})";
		
	
	
	private static ArrayList<ProductSaveToDb> getMetalProduct (String name, LocalDate date, String[] rateText) {
		
		ArrayList<ProductSaveToDb> result = new ArrayList<ProductSaveToDb>();
		
		ZonedDateTime priceDate = ZonedDateTime.of(date, LocalTime.parse("00:00"), ZoneId.of("Europe/Moscow"));
		LocalTime time;
		Float count = Float.valueOf(0);
		Float buyPrice = Float.valueOf(0);
		Float sellPrice = Float.valueOf(0);
		
		
		for (String rowStr: rateText) {
			
			ProductSaveToDb product = new ProductSaveToDb();
			product.setName(name);
			
			Pattern pattern = Pattern.compile(timePattern);
			Matcher matcher = pattern.matcher(rowStr);
			if (matcher.find()) {
				
				time = LocalTime.parse(matcher.group().toString());
				priceDate = ZonedDateTime.of(date, time, ZoneId.of("Europe/Moscow"));
			}
			product.setPriceDate(priceDate);
			
			pattern = Pattern.compile(countPattern);
			matcher = pattern.matcher(rowStr);
			if (matcher.find()) {
				
				count = Float.parseFloat(matcher.group().replaceAll("[^0-9[,]]",""));
			}
				product.setCount(count);
			
			
			pattern = Pattern.compile(pricePattern);
			matcher = pattern.matcher(rowStr);
			
			if (matcher.find()) {
			
				buyPrice = Float.parseFloat(matcher.group().replaceAll("[^0-9[,]]","").replaceAll(",", "."));
			}
			
			product.setBuyPrice(buyPrice);
			
			if (matcher.find()) {
				
				sellPrice = Float.parseFloat(matcher.group().replaceAll("[^0-9[,]]","").replaceAll(",", "."));
			}
			
			product.setSellPrice(sellPrice);
			
			result.add(product);
		}
		
	
		return result;
	}
    
    
    private static void  getMetalProducts(WebDriver driver){
		
		ArrayList<ProductSaveToDb> result = new ArrayList<ProductSaveToDb>();
		ArrayList<WebElement> ratePerTime = new ArrayList<WebElement>();
		
		rateHistoryTable = driver.findElement(By.xpath("/html/body/div[4]/div/div/div/div/div/div/div/div/div[2]/div/div/div[1]/table"));
		ratePerTime = (ArrayList<WebElement>) rateHistoryTable.findElements(By.className("history-table__list-wrap"));
		
		/*********
		 * 
		 * Get rate time
		 * 
		 *********/
		
		for (WebElement rate: ratePerTime) {
			
			String[] rateText = rate.getText().split("\n");
			
			
			result = getMetalProduct("Палладий", LocalDate.parse("24.08.2021", DateTimeFormatter.ofPattern("dd.MM.uuuu")), rateText);
			
			
		}
		
		
				
	}
	
	
	private static void getMetalRateHistory() throws InterruptedException {
		
		System.setProperty("webdriver.chrome.driver",
				"D:\\Programming\\Projects\\Java\\Trader\\Java\\RatePharser\\chromedriver_92_0_4515_107\\chromedriver.exe");

		WebDriver driver = new ChromeDriver();
		Actions actions = new Actions(driver);
		driver.get("https://www.sberbank.ru/ru/quotes/metalbeznal");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		/**************
		 * //*[@id="page-main"]/div[2]/div[3]/div[1]/div/div/div/div/div/div[2]/div/div/div[2]/div/span[2]/span
		 *  XPath for button "История курсов" 
		 *  /html/body/div[1]/div[2]/div[2]/div[3]/div[1]/div/div/div/div/div/div[2]/div/div/div[2]/div/span[2]
		 *  /html/body/div[1]/div[2]/div[2]/div[3]/div[1]/div/div/div/div/div/div[2]/div/div/div[2]/div/span[2]/span
		 *  *********************************
		 *  XPath Accept Coockie Window
		 *  /html/body/div[1]/div[4]/div/div/div/button
		 *
		**************/	
		
		//accept Coockie and close the popup window
		webElement = driver.findElement(By.xpath("/html/body/div[1]/div[4]/div/div/div/button"));
		webElement.click();
		
		
		//wait while link "История курсов" will be visible and click it
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"page-main\"]/div[2]/div[3]/div[1]/div/div/div/div/div/div[2]/div/div/div[2]/div/span[2]/span")));
		webElement = driver.findElement(By.xpath("//*[@id=\"page-main\"]/div[2]/div[3]/div[1]/div/div/div/div/div/div[2]/div/div/div[2]/div/span[2]/span"));
		webElement.click();
		
		//Get date calendar field	
		webElement = driver.findElement(By.name("RatesForm_date-in-history"));
		//parentWebElement = webElement.findElement(By.xpath("./.."));
		
		//Insert date to calendar field
		//webElement.clear();
		//webElement.sendKeys("25.08.2021");
		
		driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
		//Thread.sleep(2000);
		//webElement.clear();
		//Send date to calendar input
		actions.doubleClick(webElement).sendKeys(webElement, Keys.DELETE).build().perform();
		actions.sendKeys(webElement, "24.08.2021").build().perform();
		
		
		//webElement.sendKeys("29.08.2021");
		System.out.println(webElement.getAttribute("value"));
		
			
				
		//Change metal
		webElement = driver.findElement(By.xpath("/html/body/div[4]/div/div/div/div/div/div/div/div/div[1]/div/div/div[1]/div[2]/div[1]"));
		webElement.sendKeys(Keys.ARROW_DOWN);
		webElement.sendKeys(Keys.ARROW_DOWN);
		webElement.sendKeys(Keys.ARROW_DOWN);
		
		
		
		System.out.println("Metall name? " + webElement.getText());
		
		//Close modal window
		//webElement = driver.findElement(By.xpath("/html/body/div[4]/div/div/div/div/div/div/i"));
		
		
		
		//getMetalProducts(driver);
		
		
		driver.close();
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		getMetalRateHistory();
	
	}

}
