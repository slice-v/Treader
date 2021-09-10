package tests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpr {
	
	
	public static void main(String[] args) {
		
		String testStr = "  4 060,00";
		String testPtrn = "(\\-?!\\s*\\d+\\s*\\d+\\,\\d{2})";
		String resultStr = testStr.replaceAll("[^0-9,]", "").replaceAll(",", ".");
		Pattern ptrn;
		Matcher matcher;
		
		
		ptrn = Pattern.compile(testPtrn);
		matcher = ptrn.matcher(testStr);
		
		
		
		String inputString = "23:32 от 2 500 55,64 60,36";
        
		String countPattern = "\\от\\s*\\d+ | \\от\\s*\\d+\\s*\\d{3}"; //Pattern for metal count Matcher
	    String timePattern = "\\d{2}\\:\\d{2}"; //Pattern for rate time Matcher
	    String pricePattern = "\\d+\\s*?\\d+\\,\\d{2}";
        
        
        ptrn = Pattern.compile(timePattern);
        matcher = ptrn.matcher(inputString);
                
                
         
        if(matcher.find()){
            System.out.println("Дата: " + matcher.group().replaceAll("\\s+",""));
        }
        else {
        		System.out.println("Didn't find");
        }
        
        
        ptrn = Pattern.compile(countPattern);
        matcher = ptrn.matcher(inputString);
                                
         
        if(matcher.find()){
            System.out.println("Количество: " + matcher.group().replaceAll("[^0-9]", ""));
        }
        else {
        		System.out.println("Didn't find");
        }
        
        
        ptrn = Pattern.compile(pricePattern);
        matcher = ptrn.matcher(inputString.replaceAll(countPattern, "").replaceAll(timePattern, ""));
                
       
        if(matcher.find()){
            System.out.println("Купить: " + matcher.group().replaceAll("[^0-9[,]]","").replaceAll(",", "."));
        }
        else {
        		System.out.println("Didn't find");
        }
        
        if(matcher.find()){
            System.out.println("Продать: " + matcher.group().replaceAll("[^0-9[,]]","").replaceAll(",", "."));
        }
        else {
        		System.out.println("Didn't find");
        }

                	
	}

}
