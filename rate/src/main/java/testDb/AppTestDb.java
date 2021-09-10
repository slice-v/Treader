package testDb;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class AppTestDb {
	
	
	public static void main(String[] args) {
		
		UserTestDb test = new UserTestDb();
		
		test.setCity("NiNo");
		test.setName("Vasya");
		test.setNum(1111);
		test.setId(Long.valueOf(1));
		
		
		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(test);
		session.getTransaction().commit();
		session.close();
		
	}

}
