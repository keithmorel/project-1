package com.revature.service;

import com.revature.dao.UserRepository;
import com.revature.dto.LoginDTO;
import com.revature.dto.SignupDTO;
import com.revature.exceptions.BadParameterException;
import com.revature.exceptions.LoginException;
import com.revature.exceptions.RegistrationException;
import com.revature.model.User;

public class LoginService {
	
	private UserRepository userRepository;
	
	public LoginService() {
		this.userRepository = new UserRepository();
	}
	
	public LoginService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public User getUserByUsernameAndPassword(LoginDTO loginDTO) throws BadParameterException, LoginException {
		// Do some checking for blank username, blank password
		if (loginDTO.getUsername().trim().equals("") || loginDTO.getPassword().trim().equals("")) {
			throw new BadParameterException("Cannot have blank username and/or password");
		}
		
		User user = userRepository.getUserByUsernameAndPassword(loginDTO);
		
		return user;
	}

	public User registerUser(SignupDTO userInfo) throws RegistrationException, BadParameterException {
		
		// Check if info provided is empty
		if (userInfo.getUsername().trim().equals("") || userInfo.getPassword().trim().equals("") || userInfo.getFirstName().trim().equals("") ||
			userInfo.getLastName().trim().equals("") || userInfo.getEmail().trim().equals("") || userInfo.getRole().trim().equals("")) {
			throw new BadParameterException("All fields are required");
		} else if (!userInfo.getEmail().matches("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")) {
			throw new BadParameterException("Email must be of the form '[email]@[website].[domain]'");
		}

		User validatedUser = userRepository.registerUser(userInfo);
		
		if (validatedUser == null) {
			throw new RegistrationException("Unable to register a new user with the information provided");
		}
		
		return validatedUser;
		
	}

}
