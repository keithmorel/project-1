package com.revature.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.revature.dao.UserRepository;
import com.revature.dto.LoginDTO;
import com.revature.dto.SignupDTO;
import com.revature.exceptions.BadParameterException;
import com.revature.exceptions.DatabaseException;
import com.revature.exceptions.LoginException;
import com.revature.exceptions.RegistrationException;
import com.revature.model.User;
import com.revature.model.UserRole;
import com.revature.util.SessionUtility;

public class LoginServiceTest {
	
	private static UserRepository mockUserRepository;
	private static SessionFactory mockSessionFactory;
	
	private LoginService loginService;
	
	@BeforeClass
	public static void setUp() throws DatabaseException, LoginException, RegistrationException {
		mockUserRepository = mock(UserRepository.class);
		mockSessionFactory = mock(SessionFactory.class);
		
		User returnedUser = new User("username", "password", "firstName", "lastName", "test@email.com", new UserRole("Employee"));
		when(mockUserRepository.getUserByUsernameAndPassword(eq(new LoginDTO("username", "password"))))
			.thenReturn(returnedUser);
		
		when(mockUserRepository.registerUser(eq(new SignupDTO("username", "password", "firstName", "lastName", "test@email.com", "Employee"))))
			.thenReturn(returnedUser);
	}
	
	@Before
	public void beforeTest() {
		loginService = new LoginService(mockUserRepository);
	}
	
	@Test 
	public void test_happyPath_getUserByUsernameAndPassword() throws BadParameterException, LoginException {
	
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			User actual = loginService.getUserByUsernameAndPassword(new LoginDTO("username", "password"));
			User expected = new User("username", "password", "firstName", "lastName", "test@email.com", new UserRole("Employee"));
			assertEquals(expected, actual);
		}
		
	}
	
	@Test 
	public void test_happyPath_registerUser() throws RegistrationException, BadParameterException {
	
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			User actual = loginService.registerUser(new SignupDTO("username", "password", "firstName", "lastName", "test@email.com", "Employee"));
			User expected = new User("username", "password", "firstName", "lastName", "test@email.com", new UserRole("Employee"));
			assertEquals(expected, actual);
		}
		
	}
	
	@Test
	public void test_blankUsername_blankPassword_getUserByUsernameAndPassword() throws BadParameterException, LoginException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				loginService.getUserByUsernameAndPassword(new LoginDTO("", ""));
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "Cannot have blank username and/or password");
			}
		}
		
	}
	
	@Test
	public void test_nonBlankUsername_blankPassword_getUserByUsernameAndPassword() throws BadParameterException, LoginException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				loginService.getUserByUsernameAndPassword(new LoginDTO("test", ""));
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "Cannot have blank username and/or password");
			}
		}
		
	}
	
	@Test
	public void test_blankUsername_nonBlankPassword_getUserByUsernameAndPassword() throws BadParameterException, LoginException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				loginService.getUserByUsernameAndPassword(new LoginDTO("", "test"));
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "Cannot have blank username and/or password");
			}
		}
		
	}
	
	@Test
	public void test_blankUsernameSpaces_blankPassword_getUserByUsernameAndPassword() throws BadParameterException, LoginException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				loginService.getUserByUsernameAndPassword(new LoginDTO("    ", ""));
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "Cannot have blank username and/or password");
			}
		}
		
	}
	
	@Test
	public void test_blankUsername_blankPasswordSpaces_getUserByUsernameAndPassword() throws BadParameterException, LoginException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				loginService.getUserByUsernameAndPassword(new LoginDTO("", "    "));
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "Cannot have blank username and/or password");
			}
		}
		
	}
	
	@Test
	public void test_blankUsernameSpaces_blankPasswordSpaces_getUserByUsernameAndPassword() throws BadParameterException, LoginException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				loginService.getUserByUsernameAndPassword(new LoginDTO("    ", "      "));
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "Cannot have blank username and/or password");
			}
		}
		
	}
	
	@Test
	public void test_blankUsername_registerUser() throws RegistrationException, BadParameterException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				loginService.registerUser(new SignupDTO("", "password", "firstName", "lastName", "test@email.com", "Employee"));
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "All fields are required");
			}
		}
		
	}
	
	@Test
	public void test_blankPassword_registerUser() throws RegistrationException, BadParameterException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				loginService.registerUser(new SignupDTO("username", "", "firstName", "lastName", "test@email.com", "Employee"));
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "All fields are required");
			}
		}
		
	}
	
	@Test
	public void test_blankFirstName_registerUser() throws RegistrationException, BadParameterException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				loginService.registerUser(new SignupDTO("username", "password", "", "lastName", "test@email.com", "Employee"));
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "All fields are required");
			}
		}
		
	}
	
	@Test
	public void test_blankLastName_registerUser() throws RegistrationException, BadParameterException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				loginService.registerUser(new SignupDTO("username", "password", "firstName", "", "test@email.com", "Employee"));
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "All fields are required");
			}
		}
		
	}
	
	@Test
	public void test_blankEmail_registerUser() throws RegistrationException, BadParameterException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				loginService.registerUser(new SignupDTO("username", "password", "firstName", "lastName", "", "Employee"));
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "All fields are required");
			}
		}
		
	}
	
	@Test
	public void test_blankRole_registerUser() throws RegistrationException, BadParameterException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				loginService.registerUser(new SignupDTO("username", "password", "firstName", "lastName", "test@email.com", ""));
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "All fields are required");
			}
		}
		
	}
	
	@Test
	public void test_invalidEmail_registerUser() throws RegistrationException, BadParameterException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				loginService.registerUser(new SignupDTO("username", "password", "firstName", "lastName", "test", "Employee"));
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "Email must be of the form '[email]@[website].[domain]'");
			}
		}
		
	}

}
