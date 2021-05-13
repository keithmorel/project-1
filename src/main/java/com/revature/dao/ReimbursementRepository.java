package com.revature.dao;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.dto.PrettyReimbursement;
import com.revature.dto.ReimbursementDTO;
import com.revature.exceptions.AuthenticateException;
import com.revature.exceptions.BadParameterException;
import com.revature.exceptions.DatabaseException;
import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementStatus;
import com.revature.model.ReimbursementType;
import com.revature.model.User;
import com.revature.util.SessionUtility;

public class ReimbursementRepository {

	public List<PrettyReimbursement> getAllReimbursements(User user, String role) throws DatabaseException {

		try (Session session = SessionUtility.getSessionFactory().openSession()) {

			List<PrettyReimbursement> prettyReimbList = new ArrayList<>();
			List<Reimbursement> reimbList;
			// Employees can only see requests they are the author of
			if (role.equals("Employee")) {
				reimbList = (List<Reimbursement>) session.createQuery("FROM Reimbursement WHERE author=:au", Reimbursement.class)
						.setParameter("au", user)
						.getResultList();
			} else {
				// Managers can see all reimbursement requests, so we get all reimbursement requests
				reimbList =  (List<Reimbursement>) session.createQuery("FROM Reimbursement", Reimbursement.class)
						.getResultList();
			}
			for (Reimbursement reimb : reimbList) {
				String authorName = reimb.getAuthor().getFirstName() + " " + reimb.getAuthor().getLastName();
				String resolverName = null;
				byte[] byteArr = null;
				// Resolver can be null if reimbursement is still pending
				if (reimb.getResolver() != null) resolverName = reimb.getResolver().getFirstName() + " " + reimb.getResolver().getLastName();
				if (reimb.getReceipt() != null) byteArr = reimb.getReceipt().getBytes(1, (int) reimb.getReceipt().length());
				PrettyReimbursement prettyReimb = new PrettyReimbursement(reimb.getId(), reimb.getAmount(), reimb.getSubmitted(), reimb.getResolved(),
						reimb.getDescription(), byteArr, authorName, resolverName, reimb.getStatus().getStatus(),
						reimb.getType().getType());
				prettyReimbList.add(prettyReimb);
			}
			return prettyReimbList;
				
		} catch (NoResultException e) {
			throw new DatabaseException("Failed to retrieve reimbursement list");
		} catch (SQLException e) {
			throw new DatabaseException("Something went wrong trying to get the receipt image");
		}

	}

	public PrettyReimbursement createReimbursement(User user, ReimbursementDTO reimbursementDTO) throws DatabaseException {

		try (Session session = SessionUtility.getSessionFactory().openSession()) {

			Transaction tx = session.beginTransaction();
			Date now = new Date(System.currentTimeMillis());
			// Create all objects needed to create a new reimbursement
			ReimbursementStatus pending = session.get(ReimbursementStatus.class, 1);
			ReimbursementType type;
			String typeString = reimbursementDTO.getType();
			if (typeString.equals("Lodging")) {
				type = session.get(ReimbursementType.class, 1);
			} else if (typeString.equals("Travel")) {
				type = session.get(ReimbursementType.class, 2);
			} else if (typeString.equals("Food")) {
				type = session.get(ReimbursementType.class, 3);
			} else if (typeString.equals("Other")) {
				type = session.get(ReimbursementType.class, 4);
			} else {
				throw new DatabaseException("Cannot create a reimbursement with the type given");
			}
			Reimbursement newReimbursement = new Reimbursement(reimbursementDTO.getAmount(), now, null,
					reimbursementDTO.getDescription(), reimbursementDTO.getReceipt(), user, null, pending, type);

			session.persist(newReimbursement);
			tx.commit();
			
			String authorName = user.getFirstName() + " " + user.getLastName();
			
			// Check if there is an image stored
			byte[] byteArr = null;
			if (newReimbursement.getReceipt() != null) {
				byteArr = newReimbursement.getReceipt().getBytes(1, (int) newReimbursement.getReceipt().length());	
			}			
			
			return new PrettyReimbursement(newReimbursement.getId(), newReimbursement.getAmount(), newReimbursement.getSubmitted(), null,
					newReimbursement.getDescription(), byteArr, authorName, null, newReimbursement.getStatus().getStatus(),
					newReimbursement.getType().getType());

		} catch (NoResultException e) {
			throw new DatabaseException("Failed to create reimbursement");
		} catch (SQLException e) {
			throw new DatabaseException("Something went wrong trying to get the receipt image");
		}

	}

	public void approveReimbursement(User user, String reimbId) throws DatabaseException, AuthenticateException, BadParameterException {

		try (Session session = SessionUtility.getSessionFactory().openSession()) {
			
			if (user.getRole().getRole().equals("Employee")) {
				throw new AuthenticateException("Only managers can approve reimbursement requests");
			}
			
			Transaction tx = session.beginTransaction();
			
			Date now = new Date(System.currentTimeMillis());
			ReimbursementStatus approved = session.get(ReimbursementStatus.class, 2);
			
			int intId = Integer.parseInt(reimbId);
			Reimbursement approvedReimbursement = session.get(Reimbursement.class, intId);
			
			if (approvedReimbursement == null) {
				throw new BadParameterException("There is no such reimbursement request");
			}
			
			if (!approvedReimbursement.getStatus().getStatus().equals("Pending")) {
				throw new BadParameterException("You only approve pending reimbursement requests");
			}
			
			approvedReimbursement.setResolver(user);
			approvedReimbursement.setStatus(approved);
			approvedReimbursement.setResolved(now);
			
			session.persist(approvedReimbursement);
			tx.commit();
			
		} catch (NoResultException e) {
			throw new DatabaseException("Failed to approve reimbursement");
		}

	}
	
	public void denyReimbursement(User user, String reimbId) throws DatabaseException, AuthenticateException, BadParameterException {

		try (Session session = SessionUtility.getSessionFactory().openSession()) {
			
			if (user.getRole().getRole().equals("Employee")) {
				throw new AuthenticateException("Only managers can deny reimbursement requests");
			}
			
			Transaction tx = session.beginTransaction();
			
			Date now = new Date(System.currentTimeMillis());
			ReimbursementStatus denied = (ReimbursementStatus) session.get(ReimbursementStatus.class, 3);
			
			int intId = Integer.parseInt(reimbId);
			Reimbursement deniedReimbursement = session.get(Reimbursement.class, intId);
			
			if (deniedReimbursement == null) {
				throw new BadParameterException("There is no such reimbursement request");
			}
			
			if (!deniedReimbursement.getStatus().getStatus().equals("Pending")) {
				throw new BadParameterException("You only deny pending reimbursement requests");
			}
			
			deniedReimbursement.setResolver(user);
			deniedReimbursement.setStatus(denied);
			deniedReimbursement.setResolved(now);
			
			session.persist(deniedReimbursement);
			tx.commit();
			
		} catch (NoResultException e) {
			throw new DatabaseException("Failed to deny reimbursement");
		}

	}

}
