package com.revature.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revature.controller.Controller;
import com.revature.controller.ExceptionController;
import com.revature.controller.LoginController;
import com.revature.controller.ReimbursementController;
import com.revature.controller.StaticFileController;
import com.revature.util.SetupDatabaseUtility;

import io.javalin.Javalin;

public class Application {
	
	private static Javalin app;
	private static Logger logger = LoggerFactory.getLogger(Application.class);
	
	public static void main(String[] args) {
		
		app = Javalin.create((config) -> {
			config.addStaticFiles("static");
			config.enableCorsForAllOrigins();
		});
		
		app.before(ctx -> {
			String URI = ctx.req.getRequestURI();
			String httpMethod = ctx.req.getMethod();
			logger.info(httpMethod + " request to endpoint " + URI + " received");
		});
		
		// Uncomment below line and change the property in hibernate.cfg.xml to create to initialize database with dummy data
//		SetupDatabaseUtility.setupDB();
		
		mapControllers(new ExceptionController(), new LoginController(), new ReimbursementController(), new StaticFileController());
		
		app.start(7000);
		
	}
	
	public static void mapControllers(Controller... controllers) {
		for (Controller c : controllers) {
			c.mapEndpoints(app);
		}
	}

}
