package trader.rate.parsers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import trader.rate.models.ProductSaveToDb;


//TODO: probably with Spring static modifier with WebDriver driver willn't need?

public class SberMetalHistoryPharser {

	private WebElement webElement; //Is used to read content form page
	static private WebDriver driver;
	
	String countPattern = "\\от\\s*\\d+ | \\от\\s*\\d+\\s*\\d{3}"; //Pattern for metal count Matcher
    String timePattern = "\\d{2}\\:\\d{2}"; //Pattern for rate time Matcher
    String pricePattern = "\\d+\\s*?\\d+\\,\\d{2}"; //Pattern for buy/sell price Matcher
    
    private int daysToPast = 183; //Amount of Days how long metal rate history had been saving
    private int metalTypeCount = 4; //Amount of metal types 
    
    
    /*******
    *
    * Must be call first
    * 
    *******/
    private void OpenHistoryRatePopupWindow() {
		
		System.setProperty("webdriver.chrome.driver",
				"D:\\Programming\\Projects\\Java\\Trader\\Java\\RatePharser\\chromedriver_92_0_4515_107\\chromedriver.exe");
		driver = new ChromeDriver();

		
		driver.get("https://www.sberbank.ru/ru/quotes/metalbeznal");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
				
		//accept Coockie and close the popup window
		webElement = driver.findElement(By.xpath("/html/body/div[1]/div[4]/div/div/div/button"));
		webElement.click();
		
		
		//wait while link "История курсов" will be visible and click it
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"page-main\"]/div[2]/div[3]/div[1]/div/div/div/div/div/div[2]/div/div/div[2]/div/span[2]/span")));
		webElement = driver.findElement(By.xpath("//*[@id=\"page-main\"]/div[2]/div[3]/div[1]/div/div/div/div/div/div[2]/div/div/div[2]/div/span[2]/span"));
		webElement.click();
		
				
	}
    
    private void SendDateToInputField(LocalDate date) {
    		
    		Actions actions = new Actions(driver); //Doing some actions on page		
    	
    		//Get date calendar field	
    		webElement = driver.findElement(By.name("RatesForm_date-in-history"));
    		//Wait for page load    		
    		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    		//Clear Calendar input field before paste the date
    		actions.doubleClick(webElement).sendKeys(webElement, Keys.DELETE).build().perform();
    		
    		//Sending only weekdays
    		if ((date.getDayOfWeek() != DayOfWeek.SUNDAY) && (date.getDayOfWeek() != DayOfWeek.SATURDAY)) {
    			actions.sendKeys(webElement, date.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")).toString()).build().perform();
    			    			
    		}
    	}
    
    private void SwitchMetal() {
    	
    		webElement = driver.findElement(By.xpath("/html/body/div[4]/div/div/div/div/div/div/div/div/div[1]/div/div/div[1]/div[2]/div[1]"));
		webElement.sendKeys(Keys.ARROW_DOWN);
    }
    
    private String getMetalName() {
    	
    		WebElement metalName = driver.findElement(By.xpath("/html/body/div[4]/div/div/div/div/div/div/div/div/div[1]/div/div/div[1]/div[2]/div[1]"));
    		return metalName.getText();
    }
	
    
    private ArrayList<ProductSaveToDb> parseOnePage (String name, LocalDate date, String[] rateText) {
		
		ArrayList<ProductSaveToDb> result = new ArrayList<ProductSaveToDb>();
		
		ZonedDateTime priceDate = ZonedDateTime.of(date, LocalTime.parse("00:00"), ZoneId.of("Europe/Moscow"));
		LocalTime time = LocalTime.parse("23:00");
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
			else {
				StringBuilder tmpString = new StringBuilder(time.format(DateTimeFormatter.ofPattern("hh:mm")).toString());
				tmpString.append(" ");
				tmpString.append(rowStr);
				rowStr = tmpString.toString();
				
			}
			
			product.setPriceDate(priceDate);
			
			pattern = Pattern.compile(countPattern);
			matcher = pattern.matcher(rowStr);
			if (matcher.find()) {
				
				count = Float.parseFloat(matcher.group().replaceAll("[^0-9[,]]",""));
			}
				product.setCount(count);
			
			
			pattern = Pattern.compile(pricePattern);
			matcher = pattern.matcher(rowStr.replaceAll(countPattern, "").replaceAll(timePattern, ""));
			
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
    
    
    
    
    /*
	 * TODO: Delete this test function
	 * Test For fill Data Base History only 5 days before 
	 * 
	 */
    public ArrayList<ProductSaveToDb> TestgetMetalProductsHistorySaveToDb() {
    	
		SberMetalHistoryPharser sberMetHistPars = new SberMetalHistoryPharser();
		ArrayList<ProductSaveToDb> result = new ArrayList<ProductSaveToDb>();
		
		
		sberMetHistPars.OpenHistoryRatePopupWindow();
		
		for (int i=1; i<=sberMetHistPars.metalTypeCount; i++) {
			
			for (int j=1; j<=5; j++) {	
				
				ArrayList<ProductSaveToDb> tmpResult = new ArrayList<ProductSaveToDb>();
				sberMetHistPars.SendDateToInputField(LocalDate.now().minusDays(j));
				tmpResult = sberMetHistPars.parseAllMetalProducts(sberMetHistPars.getMetalName(), LocalDate.now().minusDays(j));
				result.addAll(tmpResult);
			}
			if (i!=4)
				sberMetHistPars.SwitchMetal();
		}
	driver.close();
	return result;	
}
    
    
    private ArrayList<ProductSaveToDb> parseAllMetalProducts(String name, LocalDate date){
		
		ArrayList<ProductSaveToDb> result = new ArrayList<ProductSaveToDb>();
		ArrayList<WebElement> ratePerTime = new ArrayList<WebElement>();
		WebElement rateHistoryTable;
		
		rateHistoryTable = driver.findElement(By.xpath("/html/body/div[4]/div/div/div/div/div/div/div/div/div[2]/div/div/div[1]/table"));
		ratePerTime = (ArrayList<WebElement>) rateHistoryTable.findElements(By.className("history-table__list-wrap"));
		
				
		for (WebElement rate: ratePerTime) {
			
			String[] rateText = rate.getText().split("\n");
			
			result.addAll(parseOnePage(name, date, rateText));
			
		}
		
		return result;
    }
		
    
    public ArrayList<ProductSaveToDb> getMetalProductsHistorySaveToDb() {
	
    		SberMetalHistoryPharser sberMetHistPars = new SberMetalHistoryPharser();
    		ArrayList<ProductSaveToDb> result = new ArrayList<ProductSaveToDb>();
    		
    		
    		sberMetHistPars.OpenHistoryRatePopupWindow();
    		
    		for (int i=1; i<=sberMetHistPars.metalTypeCount; i++) {
    			
    			for (int j=1; j<=sberMetHistPars.daysToPast; j++) {	
    				
    				ArrayList<ProductSaveToDb> tmpResult = new ArrayList<ProductSaveToDb>();
    				sberMetHistPars.SendDateToInputField(LocalDate.now().minusDays(j));
    				tmpResult = sberMetHistPars.parseAllMetalProducts(sberMetHistPars.getMetalName(), LocalDate.now().minusDays(j));
    				result.addAll(tmpResult);
    			}
    			if (i!=4)
    				sberMetHistPars.SwitchMetal();
    		}
    	driver.close();
    	return result;	
    }
    
    /*
     * startDate - probably now
     * endDate - date in the past
     * The loop go throw from startDate (now) to endDate (in the past)
     * 
     * */
    
    public ArrayList<ProductSaveToDb> getMetalProductsHistorySaveToDb(ZonedDateTime startDate, ZonedDateTime endDate) {
    	
		SberMetalHistoryPharser sberMetHistPars = new SberMetalHistoryPharser();
		ArrayList<ProductSaveToDb> result = new ArrayList<ProductSaveToDb>();
		
		int daysCount = (int) Math.abs(ChronoUnit.DAYS.between(startDate, endDate));
		
		
		sberMetHistPars.OpenHistoryRatePopupWindow();
		
		for (int i=1; i<=sberMetHistPars.metalTypeCount; i++) {
			
			for (int j=1; j<=daysCount; j++) {	
				
				ArrayList<ProductSaveToDb> tmpResult = new ArrayList<ProductSaveToDb>();
				sberMetHistPars.SendDateToInputField(startDate.toLocalDate().minusDays(j));
				tmpResult = sberMetHistPars.parseAllMetalProducts(sberMetHistPars.getMetalName(), LocalDate.now().minusDays(j));
				result.addAll(tmpResult);
			}
			if (i!=4)
				sberMetHistPars.SwitchMetal();
		}
	
	driver.close();
	return result;	
    }

}
