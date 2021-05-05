package com.revature.util;

import java.sql.Date;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementStatus;
import com.revature.model.ReimbursementType;
import com.revature.model.User;
import com.revature.model.UserRole;

public class SetupDatabaseUtility {

	public static void setupDB() {

		Session session = SessionUtility.getSessionFactory().openSession();

		// Create transaction for initializing data to Reimbursement Status table
		Transaction txStatusInit = session.beginTransaction();

		ReimbursementStatus pending = new ReimbursementStatus("Pending");
		ReimbursementStatus approved = new ReimbursementStatus("Approved");
		ReimbursementStatus denied = new ReimbursementStatus("Denied");
		session.persist(pending);
		session.persist(approved);
		session.persist(denied);

		txStatusInit.commit();

		// Create transaction for initializing data to Reimbursement Type table
		Transaction txTypeInit = session.beginTransaction();

		ReimbursementType lodging = new ReimbursementType("Lodging");
		ReimbursementType travel = new ReimbursementType("Travel");
		ReimbursementType food = new ReimbursementType("Food");
		ReimbursementType other = new ReimbursementType("Other");
		session.persist(lodging);
		session.persist(travel);
		session.persist(food);
		session.persist(other);

		txTypeInit.commit();

		// Create transaction for initializing data to User Role table
		Transaction txRoleInit = session.beginTransaction();

		UserRole employee = new UserRole("Employee");
		UserRole manager = new UserRole("Manager");
		session.persist(employee);
		session.persist(manager);

		txRoleInit.commit();

		// Create transaction for initializing data to User table
		Transaction txUserInit = session.beginTransaction();

		// Get UserRole objects to construct User objects 
		UserRole employeeRole = (UserRole) session.createQuery("FROM UserRole WHERE id = 1").getSingleResult();
		UserRole managerRole = (UserRole) session.createQuery("FROM UserRole WHERE id = 2").getSingleResult();

		// Get password hash
		String hashedPassword = HashPassword.hashPassword("password");
		
		User user1 = new User("employee", hashedPassword, "test", "employee", "employee@email.com", employeeRole);
		User user2 = new User("manager", hashedPassword, "test", "manager", "manager@email.com", managerRole);
		session.persist(user1);
		session.persist(user2);

		txUserInit.commit();
		
		// Create transaction for initializing data to Reimbursement table
		Transaction txReimbursementInit = session.beginTransaction();

		// Get User, ReimbursementStatus, and ReimbursementType objects to construct Reimbursement objects
		User author = (User) session.createQuery("FROM User WHERE id = 1").getSingleResult();
		User resolver = (User) session.createQuery("FROM User WHERE id = 2").getSingleResult();
		
		ReimbursementStatus pendingStatus = (ReimbursementStatus) session.createQuery("FROM ReimbursementStatus WHERE id = 1").getSingleResult();
		ReimbursementStatus approvedStatus = (ReimbursementStatus) session.createQuery("FROM ReimbursementStatus WHERE id = 2").getSingleResult();
		ReimbursementStatus deniedStatus = (ReimbursementStatus) session.createQuery("FROM ReimbursementStatus WHERE id = 3").getSingleResult();
		
		ReimbursementType lodgingType = (ReimbursementType) session.createQuery("FROM ReimbursementType WHERE id = 1").getSingleResult();
		ReimbursementType travelType = (ReimbursementType) session.createQuery("FROM ReimbursementType WHERE id = 2").getSingleResult();
		ReimbursementType foodType = (ReimbursementType) session.createQuery("FROM ReimbursementType WHERE id = 3").getSingleResult();
		ReimbursementType otherType = (ReimbursementType) session.createQuery("FROM ReimbursementType WHERE id = 4").getSingleResult();

		Date resolved = new Date(10000000);
		Date submitted = new Date(20000000);
		Date now = new Date(System.currentTimeMillis());
		
		Reimbursement reimb1 = new Reimbursement(5000, submitted, null, "This is a test lodging reimbursement.", null, author, null, pendingStatus, lodgingType);
		Reimbursement reimb2 = new Reimbursement(10000, now, null, "This is a test travel reimbursement.", null, author, null, pendingStatus, travelType);
		Reimbursement reimb3 = new Reimbursement(250, submitted, resolved, "This is a test food reimbursement.", null, author, resolver, deniedStatus, foodType);
		Reimbursement reimb4 = new Reimbursement(50, submitted, resolved, "This is a test other reimbursement.", null, resolver, resolver, approvedStatus, otherType);
		Reimbursement reimb5 = new Reimbursement(235, submitted, resolved, "This is a test other reimbursement.", null, resolver, resolver, approvedStatus, otherType);
		session.persist(reimb1);
		session.persist(reimb2);
		session.persist(reimb3);
		session.persist(reimb4);
		session.persist(reimb5);

		txReimbursementInit.commit();
	}

}
