package com.revature.controller;

import com.revature.dto.LoginDTO;
import com.revature.dto.MessageDTO;
import com.revature.dto.SignupDTO;
import com.revature.model.User;
import com.revature.service.LoginService;

import io.javalin.Javalin;
import io.javalin.http.Handler;

public class LoginController implements Controller {

	private LoginService loginService;

	public LoginController() {
		this.loginService = new LoginService();
	}

	private Handler loginHandler = (ctx) -> {
		LoginDTO loginDTO = ctx.bodyAsClass(LoginDTO.class);

		User user = loginService.getUserByUsernameAndPassword(loginDTO);

		ctx.sessionAttribute("currentlyLoggedInUser", user);
		ctx.json(user);
	};
	
	private Handler signupHandler = (ctx) -> {
		SignupDTO userInfo = ctx.bodyAsClass(SignupDTO.class);
		
		User newUser = loginService.registerUser(userInfo);
		
		ctx.sessionAttribute("currentlyLoggedInUser", newUser);
		ctx.json(newUser);
		ctx.status(201);
	};

	private Handler currentUserHandler = (ctx) -> {
		User user = (User) ctx.sessionAttribute("currentlyLoggedInUser");

		if (user == null) {
			MessageDTO messageDTO = new MessageDTO();
			messageDTO.setMessage("User is not currently logged in!");
			ctx.json(messageDTO);
			ctx.status(400);
		} else {
			ctx.json(user);
		}

	};

	private Handler logoutHandler = (ctx) -> {

		ctx.req.getSession().invalidate();
		ctx.json(new MessageDTO("Successfully logged out"));

	};

	@Override
	public void mapEndpoints(Javalin app) {

		app.post("/signup", signupHandler);
		app.post("/login", loginHandler);
		app.get("currentUser", currentUserHandler);
		app.post("/logout", logoutHandler);

	}

}
