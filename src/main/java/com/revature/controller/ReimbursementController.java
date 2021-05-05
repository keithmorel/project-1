package com.revature.controller;

import java.io.InputStream;
import java.sql.Blob;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import com.revature.dto.MessageDTO;
import com.revature.dto.PrettyReimbursement;
import com.revature.dto.ReimbursementDTO;
import com.revature.exceptions.BadParameterException;
import com.revature.model.User;
import com.revature.service.ReimbursementService;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.http.UploadedFile;

public class ReimbursementController implements Controller {
	
	private ReimbursementService reimbursementService;

	public ReimbursementController() {
		this.reimbursementService = new ReimbursementService();
	}

	private Handler getAllReimbursementsHandler = (ctx) -> {
		User user = (User) ctx.sessionAttribute("currentlyLoggedInUser");
		
		List<PrettyReimbursement> reimbursements = reimbursementService.getAllReimbursements(user);
		
		ctx.json(reimbursements);
	};
	
	private Handler createReimbursementHandler = (ctx) -> {
		User user = (User) ctx.sessionAttribute("currentlyLoggedInUser");
		ctx.contentType("multipart/form-data");
		
		if (ctx.formParam("amount").equals("") || ctx.formParam("description").equals("") || ctx.formParam("type").equals("")) {
			throw new BadParameterException("All fields except receipt are required");
		}
		ReimbursementDTO reimbursementDTO = new ReimbursementDTO();
		reimbursementDTO.setAmount(Double.parseDouble(ctx.formParam("amount")));
		reimbursementDTO.setDescription(ctx.formParam("description"));
		reimbursementDTO.setType(ctx.formParam("type"));
		
		// Check if there is a receipt file to persist to db
		if (ctx.uploadedFiles().size() != 0) {
			UploadedFile uploadedFile = ctx.uploadedFiles().get(0);
			InputStream byteStream = uploadedFile.component1();
			byte[] byteArray = byteStream.readAllBytes();
			Blob blob = new SerialBlob(byteArray);
			reimbursementDTO.setReceipt(blob);
		} else reimbursementDTO.setReceipt(null);
		
		PrettyReimbursement reimbursement = reimbursementService.createReimbursement(user, reimbursementDTO);
		
		ctx.json(reimbursement);
	};
	
	private Handler approveReimbursementHandler = (ctx) -> {
		User user = (User) ctx.sessionAttribute("currentlyLoggedInUser");
		String reimbId = ctx.pathParam("reimbId");
		
		reimbursementService.approveReimbursement(user, reimbId);
		
		ctx.json(new MessageDTO("Reimbursement approved"));
	};
	
	private Handler denyReimbursementHandler = (ctx) -> {
		User user = (User) ctx.sessionAttribute("currentlyLoggedInUser");
		String reimbId = ctx.pathParam("reimbId");
		
		reimbursementService.denyReimbursement(user, reimbId);
		
		ctx.json(new MessageDTO("Reimbursement denied"));
	};
	
	@Override
	public void mapEndpoints(Javalin app) {
		app.get("/reimbursements", getAllReimbursementsHandler);
		app.post("/reimbursements", createReimbursementHandler);
		app.post("/reimbursements/:reimbId/approve", approveReimbursementHandler);
		app.post("/reimbursements/:reimbId/deny", denyReimbursementHandler);

	}

}
