package com.revature.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revature.dto.MessageDTO;
import com.revature.exceptions.AuthenticateException;
import com.revature.exceptions.BadParameterException;
import com.revature.exceptions.DatabaseException;
import com.revature.exceptions.LoginException;
import com.revature.exceptions.RegistrationException;

import io.javalin.Javalin;
import io.javalin.http.ExceptionHandler;

public class ExceptionController implements Controller {

	private Logger logger = LoggerFactory.getLogger(ExceptionController.class);
	
	/*
	 * Exception handler
	 */
	private ExceptionHandler<BadParameterException> badParameterExceptionHandler = (e, ctx) -> {
		logger.warn("A user provided a bad parameter. Exception message is: \n" + e.getMessage());
		ctx.status(400);
		ctx.json(new MessageDTO(e.getMessage()));
	};
	
	private ExceptionHandler<LoginException> loginExceptionHandler = (e, ctx) -> {
		logger.warn("Failed to login user. Exception message is: \n" + e.getMessage());
		ctx.status(400);
		ctx.json(new MessageDTO(e.getMessage()));
	};
	
	private ExceptionHandler<RegistrationException> registrationExceptionHandler = (e, ctx) -> {
		logger.warn("Failed to register user. Exception message is: \n" + e.getMessage());
		ctx.status(400);
		ctx.json(new MessageDTO(e.getMessage()));
	};
	
	private ExceptionHandler<DatabaseException> databaseExceptionHandler = (e, ctx) -> {
		logger.warn("Something went wrong with a database query. Exception message is: \n" + e.getMessage());
		ctx.status(400);
		ctx.json(new MessageDTO(e.getMessage()));
	};
	
	private ExceptionHandler<AuthenticateException> authenticateExceptionHandler = (e, ctx) -> {
		logger.warn("Authentication error. Exception message is: \n" + e.getMessage());
		ctx.status(400);
		ctx.json(new MessageDTO(e.getMessage()));
	};
	
	
	@Override
	public void mapEndpoints(Javalin app) {
		app.exception(BadParameterException.class, badParameterExceptionHandler);
		app.exception(LoginException.class, loginExceptionHandler);
		app.exception(RegistrationException.class, registrationExceptionHandler);
		app.exception(DatabaseException.class, databaseExceptionHandler);
		app.exception(AuthenticateException.class, authenticateExceptionHandler);
	}

}
