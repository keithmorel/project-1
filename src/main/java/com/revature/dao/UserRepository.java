package com.revature.dao;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.dto.LoginDTO;
import com.revature.dto.SignupDTO;
import com.revature.exceptions.LoginException;
import com.revature.exceptions.RegistrationException;
import com.revature.model.User;
import com.revature.model.UserRole;
import com.revature.util.SessionUtility;
import com.revature.util.HashPassword;

public class UserRepository {

	public User getUserByUsernameAndPassword(LoginDTO loginDTO) throws LoginException {

		try (Session session = SessionUtility.getSessionFactory().openSession()) {

			String hashedPassword = HashPassword.hashPassword(loginDTO.getPassword());
			
			return (User) session.createQuery("FROM User WHERE username=:un AND password=:pw")
					.setParameter("un", loginDTO.getUsername())
					.setParameter("pw", hashedPassword)
					.getSingleResult();

		} catch (NoResultException e) {
			throw new LoginException("Invalid username or password given");
		}

	}

	public User registerUser(SignupDTO userInfo) throws RegistrationException {

		try (Session session = SessionUtility.getSessionFactory().openSession()) {

			// Create a transaction to persist the new user to the database
			Transaction tx = session.beginTransaction();
			
			// Get role object for the provided role type
			UserRole role = null;
			if (userInfo.getRole().equals("Employee")) role = (UserRole) session.get(UserRole.class, 1);
			else if (userInfo.getRole().equals("Manager")) role = (UserRole) session.get(UserRole.class, 2);
			String hashedPassword = HashPassword.hashPassword(userInfo.getPassword());
			User newUser = new User(userInfo.getUsername(), hashedPassword, userInfo.getFirstName(), userInfo.getLastName(), userInfo.getEmail(), role);
			
			session.persist(newUser);
			tx.commit();
			
			// Try to grab the user back out of the database, as it should be in there now (username, email are both unique fields)
			return (User) session.createQuery("FROM User WHERE username=:un AND email=:em")
					.setParameter("un", userInfo.getUsername()).setParameter("em", userInfo.getEmail())
					.getSingleResult();

		} catch (NoResultException e) {
			throw new RegistrationException("Failed to commit new user to the database");
		} catch (PersistenceException e) {
			// Check for duplicate username/email
			throw new RegistrationException("A user with that username/email already exists");
		}

	}

}
