package com.revature.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revature.app.Application;
import com.revature.dto.LoginDTO;
import com.revature.dto.MessageDTO;
import com.revature.dto.SignupDTO;
import com.revature.model.User;
import com.revature.service.LoginService;

import io.javalin.Javalin;
import io.javalin.http.Handler;

public class LoginController implements Controller {

	private LoginService loginService;
	private static Logger logger = LoggerFactory.getLogger(Application.class);

	public LoginController() {
		this.loginService = new LoginService();
	}

	private Handler loginHandler = (ctx) -> {
		LoginDTO loginDTO = ctx.bodyAsClass(LoginDTO.class);

		User user = loginService.getUserByUsernameAndPassword(loginDTO);

		logger.info("Successfully logged in user: \n" + user);
		ctx.sessionAttribute("currentlyLoggedInUser", user);
		ctx.json(user);
	};
	
	private Handler signupHandler = (ctx) -> {
		SignupDTO userInfo = ctx.bodyAsClass(SignupDTO.class);
		
		User newUser = loginService.registerUser(userInfo);
		
		logger.info("Successfully registered user: \n" + newUser);
		ctx.sessionAttribute("currentlyLoggedInUser", newUser);
		ctx.json(newUser);
		ctx.status(201);
	};

	private Handler logoutHandler = (ctx) -> {

		logger.info("Successfully logged out current user");
		ctx.req.getSession().invalidate();
		ctx.json(new MessageDTO("Successfully logged out"));

	};

	@Override
	public void mapEndpoints(Javalin app) {

		app.post("/signup", signupHandler);
		app.post("/login", loginHandler);
		app.post("/logout", logoutHandler);

	}

}
