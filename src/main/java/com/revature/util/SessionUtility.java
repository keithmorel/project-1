package com.revature.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionUtility {

	private static SessionFactory sessionFactory;
	
	public synchronized static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			sessionFactory = new Configuration()
								.setProperty("hibernate.connection.username", System.getenv("db_username"))
								.setProperty("hibernate.connection.password", System.getenv("db_password"))
								.configure("hibernate.cfg.xml")
								.buildSessionFactory();
		}
		
		return sessionFactory;
	}

}
