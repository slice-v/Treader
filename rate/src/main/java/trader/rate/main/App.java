package trader.rate.main;

import trader.data.SberMetalDataManage;
import trader.data.SberMetalRateData;
import trader.rate.parsers.SberMetalPharser;

public class App {


    public static void main(String[] args)
    {
        
    		SberMetalPharser sberMetalPharser = new SberMetalPharser();
    		SberMetalRateData sberMetalRateData = new SberMetalRateData();
    		SberMetalDataManage sberMetalDataManage = new SberMetalDataManage();
    		
    		//sberMetalRateData.setMetalProduct(sberMetalPharser.getMetalProducts());
    		//sberMetalRateData.SaveMetalProductToDB();
    		//sberMetalDataManage.SaveMetalRateHistoryToDb();
    		sberMetalDataManage.SaveMetalRateBetweenDateToDb();
    		
    		//sberMetalDataManage.TestSaveMetalRateHistoryToDb();
    		//sberMetalDataManage.SaveMetalRateToDb();
    		    		
    		
    		
    }

	
}
