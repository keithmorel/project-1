package com.revature.service;

import java.util.List;

import com.revature.dao.ReimbursementRepository;
import com.revature.dto.PrettyReimbursement;
import com.revature.dto.ReimbursementDTO;
import com.revature.exceptions.AuthenticateException;
import com.revature.exceptions.BadParameterException;
import com.revature.exceptions.DatabaseException;
import com.revature.model.User;

public class ReimbursementService {
	
private ReimbursementRepository reimbursementRepository;
	
	public ReimbursementService() {
		this.reimbursementRepository = new ReimbursementRepository();
	}
	
	public ReimbursementService(ReimbursementRepository reimbursementRepository) {
		this.reimbursementRepository = reimbursementRepository;
	}

	public List<PrettyReimbursement> getAllReimbursements(User user) throws DatabaseException, BadParameterException {
		
		if (user == null) {
			throw new BadParameterException("You must be logged in to view reimbursements");
		}
		
		return reimbursementRepository.getAllReimbursements(user, user.getRole().getRole());
		
	}

	public PrettyReimbursement createReimbursement(User user, ReimbursementDTO reimbursementDTO) throws BadParameterException, DatabaseException {
		
		if (user == null) {
			throw new BadParameterException("You must be logged in to create a reimbursement");
		}
		
		return reimbursementRepository.createReimbursement(user, reimbursementDTO);
		
	}

	public void approveReimbursement(User user, String reimbId) throws BadParameterException, DatabaseException, AuthenticateException {

		if (user == null) {
			throw new BadParameterException("You must be logged in to approve a reimbursement");
		}
		
		reimbursementRepository.approveReimbursement(user, reimbId);
		
	}
	
	public void denyReimbursement(User user, String reimbId) throws BadParameterException, DatabaseException, AuthenticateException {

		if (user == null) {
			throw new BadParameterException("You must be logged in to deny a reimbursement");
		}
		
		reimbursementRepository.denyReimbursement(user, reimbId);
		
	}

}
