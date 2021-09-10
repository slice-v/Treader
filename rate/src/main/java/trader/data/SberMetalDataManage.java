package trader.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import trader.rate.models.Product;
import trader.rate.models.ProductSaveToDb;
import trader.rate.parsers.SberMetalHistoryPharser;
import trader.rate.parsers.SberMetalPharser;

//TODO: Around Session session to try/catch block
public class SberMetalDataManage {

	private SessionFactory sessionFactory;
	private Session session;
	
	
	
	
	//TODO: In String Boot service should be run by schedule
	
	public void SaveMetalRateToDb () {
		
		SberMetalPharser sberMetalPharser = new SberMetalPharser();

		ArrayList<ProductSaveToDb> lastSavedProducts = getLastSavedProducts();
		ArrayList<ProductSaveToDb> newParseProducts = sberMetalPharser.getMetalProductsSaveToDb();
		ArrayList<ProductSaveToDb> tmpProdList = new ArrayList<ProductSaveToDb>();
		
		
		 sessionFactory = new Configuration().configure().buildSessionFactory();
		 session = sessionFactory.openSession(); session.beginTransaction();
		
		 tmpProdList = (ArrayList<ProductSaveToDb>) lastSavedProducts.stream()
																		.flatMap(newProd ->
																		newParseProducts.stream()
																			.filter(lastProd -> newProd.compareTo(lastProd) == 1)
																				)
																		.collect(Collectors.toList());
		 
		 if (tmpProdList.size() >0 )
			 //tmpProdList.forEach(System.out::println);
			 tmpProdList.forEach(product -> session.save(product));
		 
		 
		 session.getTransaction().commit(); session.close();
		 
			
	}
	
	public void SaveMetalRateHistoryToDb () {
		
		SberMetalHistoryPharser sberMetalHistoryParser = new SberMetalHistoryPharser();
		
		sessionFactory = new Configuration().configure().buildSessionFactory();
		session = sessionFactory.openSession();
		session.beginTransaction();
		
		for(ProductSaveToDb saveProduct : sberMetalHistoryParser.getMetalProductsHistorySaveToDb())
			session.save(saveProduct);
		session.getTransaction().commit();
		session.close();
	}
	
	
	/*
	 * Use for tests
	 * For fill Data Base History only 5 days before
	 * 
	 * */
	public void TestSaveMetalRateHistoryToDb () {
			
			SberMetalHistoryPharser sberMetalHistoryParser = new SberMetalHistoryPharser();
			
			sessionFactory = new Configuration().configure().buildSessionFactory();
			session = sessionFactory.openSession();
			session.beginTransaction();
			
			for(ProductSaveToDb saveProduct : sberMetalHistoryParser.TestgetMetalProductsHistorySaveToDb())
				session.save(saveProduct);
			session.getTransaction().commit();
			session.close();
		}
	
		
	private ArrayList<ProductSaveToDb> getLastSavedProducts () {
		
		ArrayList<ProductSaveToDb> result = new ArrayList<ProductSaveToDb>();
		ZonedDateTime date = getLastDbRateRecord();
		
		sessionFactory = new Configuration().configure().buildSessionFactory();
		session = sessionFactory.openSession();
						
		Query hqlQuery = session.createQuery("from ProductSaveToDb where priceDate BETWEEN :pastDate AND :nowDate");
		hqlQuery.setParameter("pastDate", date);
		hqlQuery.setParameter("nowDate", ZonedDateTime.now());
						
		result = (ArrayList<ProductSaveToDb>)hqlQuery.list();
				
		session.close();
				
		return result;
			
	}
	
	
	
	private ZonedDateTime getLastDbRateRecord()
	{
		ZonedDateTime result;
				
		sessionFactory = new Configuration().configure().buildSessionFactory();
		session = sessionFactory.openSession();
		
		Query query = session.createQuery("select max(priceDate) from ProductSaveToDb");
		
		result = (ZonedDateTime)query.list().get(0);
		
		
		session.close();
		
		return result.minusHours(1);
		
		
	}
	
	public void SaveMetalRateBetweenDateToDb() {
		
		SberMetalHistoryPharser sberMetalHistoryParser = new SberMetalHistoryPharser();
		
		ArrayList<ProductSaveToDb> tmpProducts = sberMetalHistoryParser.getMetalProductsHistorySaveToDb(ZonedDateTime.now(), getLastDbRateRecord());
		
		sessionFactory = new Configuration().configure().buildSessionFactory();
		session = sessionFactory.openSession();
		session.beginTransaction();
		
		//for (ProductSaveToDb saveProduct : tmpProducts)
			 //session.save(saveProduct);
		tmpProducts.forEach(product -> session.save(product));
		
		session.getTransaction().commit();
		session.close();
	
	}

	
}
