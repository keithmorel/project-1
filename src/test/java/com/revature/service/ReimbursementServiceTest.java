package com.revature.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.revature.dao.ReimbursementRepository;
import com.revature.dto.PrettyReimbursement;
import com.revature.dto.ReimbursementDTO;
import com.revature.exceptions.AuthenticateException;
import com.revature.exceptions.BadParameterException;
import com.revature.exceptions.DatabaseException;
import com.revature.model.User;
import com.revature.model.UserRole;
import com.revature.util.SessionUtility;

public class ReimbursementServiceTest {
	
	private static ReimbursementRepository mockReimbursementRepository;
	private static SessionFactory mockSessionFactory;
	
	private ReimbursementService reimbursementService;
	
	@BeforeClass
	public static void setUp() throws DatabaseException {
		mockReimbursementRepository = mock(ReimbursementRepository.class);
		mockSessionFactory = mock(SessionFactory.class);
		
		User employee = new User("username", "password", "firstName", "lastName", "test@email.com", new UserRole("Employee"));
		List<PrettyReimbursement> reimbList = new ArrayList<>();
		PrettyReimbursement prettyReimb1 = new PrettyReimbursement(1, 100, new Date(100), new Date(200), "test", null, "firstName lastName", null, "Pending", "Food");
		PrettyReimbursement prettyReimb2 = new PrettyReimbursement(2, 500, new Date(101), new Date(201), "test", null, "mister manager", "mister manager", "Denied", "Travel");
		reimbList.add(prettyReimb1);
		reimbList.add(prettyReimb2);

		when(mockReimbursementRepository.getAllReimbursements(eq(employee), eq("Employee")))
			.thenReturn(reimbList);
		
		ReimbursementDTO reimbDTO = new ReimbursementDTO(100, "test", null, "Food");	
		when(mockReimbursementRepository.createReimbursement(eq(employee), eq(reimbDTO)))
			.thenReturn(prettyReimb1);
		
	}
	
	@Before
	public void beforeTest() {
		reimbursementService = new ReimbursementService(mockReimbursementRepository);
	}
	
	@Test 
	public void test_happyPath_getAllReimbursements() throws DatabaseException, BadParameterException {
	
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			List<PrettyReimbursement> actual = reimbursementService.getAllReimbursements(new User("username", "password", "firstName", "lastName", "test@email.com", new UserRole("Employee")));
			List<PrettyReimbursement> expected = new ArrayList<>();
			PrettyReimbursement prettyReimbExpected1 = new PrettyReimbursement(1, 100, new Date(100), new Date(200), "test", null, "firstName lastName", null, "Pending", "Food");
			PrettyReimbursement prettyReimbExpected2 = new PrettyReimbursement(2, 500, new Date(101), new Date(201), "test", null, "mister manager", "mister manager", "Denied", "Travel");
			expected.add(prettyReimbExpected1);
			expected.add(prettyReimbExpected2);
			
			assertEquals(expected, actual);
		}
		
	}
	
	@Test 
	public void test_happyPath_createReimbursement() throws BadParameterException, DatabaseException {
	
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			PrettyReimbursement actual = reimbursementService.createReimbursement(new User("username", "password", "firstName", "lastName", "test@email.com", new UserRole("Employee")), new ReimbursementDTO(100, "test", null, "Food"));
			PrettyReimbursement expected = new PrettyReimbursement(1, 100, new Date(100), new Date(200), "test", null, "firstName lastName", null, "Pending", "Food");
			
			assertEquals(expected, actual);
		}
		
	}
	
	@Test 
	public void test_happyPath_approveReimbursement() throws DatabaseException, AuthenticateException {
	
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				reimbursementService.approveReimbursement(new User("manager", "password", "mister", "manager", "manager@email.com", new UserRole("Manager")), "1");
			} catch (BadParameterException e) {
				fail("Function call shouldn't fail with given input");
			} catch (AuthenticateException e) {
				fail("Function call shouldn't fail with given input");
			}
			
		}
		
	}
	
	@Test 
	public void test_happyPath_denyReimbursement() throws DatabaseException, AuthenticateException {
	
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				reimbursementService.denyReimbursement(new User("manager", "password", "mister", "manager", "manager@email.com", new UserRole("Manager")), "1");
			} catch (BadParameterException e) {
				fail("Function call shouldn't fail with given input");
			} catch (AuthenticateException e) {
				fail("Function call shouldn't fail with given input");
			}
			
		}
		
	}
	
	@Test
	public void test_nullUser_getAllReimbursements() throws DatabaseException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				reimbursementService.getAllReimbursements(null);
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "You must be logged in to view reimbursements");
			}
		}
		
	}
	
	@Test
	public void test_nullUser_createReimbursement() throws DatabaseException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				reimbursementService.createReimbursement(null, new ReimbursementDTO(100, "test", null, "Food"));
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "You must be logged in to create a reimbursement");
			}
		}
		
	}
	
	@Test
	public void test_nullUser_approveReimbursement() throws DatabaseException, AuthenticateException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				reimbursementService.approveReimbursement(null, "1");
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "You must be logged in to approve a reimbursement");
			}
		}
		
	}
	
	@Test
	public void test_nullUser_denyReimbursement() throws DatabaseException, AuthenticateException {
		
		try(MockedStatic<SessionUtility> mockedSessionUtil = mockStatic(SessionUtility.class)) {
			mockedSessionUtil.when(SessionUtility::getSessionFactory).thenReturn(mockSessionFactory);
			
			try {
				reimbursementService.denyReimbursement(null, "1");
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "You must be logged in to deny a reimbursement");
			}
		}
		
	}

}
