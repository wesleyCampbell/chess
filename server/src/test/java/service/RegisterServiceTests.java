package service;

import dataaccess.*;
import dataaccess.memorydao.*;
import service.*;

import service.RegisterService.RegisterRequest;
import service.RegisterService.RegisterResult;

import service.LogoutService.LogoutRequest;
import service.LogoutService.LogoutResult;

import service.LoginService.LoginRequest;
import service.LoginService.LoginResult;

import service.ListGameService.ListGameRequest;
import service.ListGameService.ListGameResult;

import service.JoinGameService.JoinGameRequest;
import service.JoinGameService.JoinGameResult;

import service.CreateGameService.CreateGameRequest;
import service.CreateGameService.CreateGameResult;

import service.ClearDataService.ClearDataRequest;
import service.ClearDataService.ClearDataResult;

import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.HashSet;

public class RegisterServiceTests {

	private static AuthDAO authDAO;
	private static UserDAO userDAO;
	private static GameDAO gameDAO;

	//
	// ======================= TEST SETUP/CLEANUP ======================= 
	//
	
	@BeforeEach
	public void init() {
		authDAO = new MemoryAuthDAO();
		userDAO = new MemoryUserDAO();
		gameDAO = new MemoryGameDAO();
	}

	//
	// ======================= TEST CASES ======================= 
	//
	
	@Test
	@DisplayName("Correct Register User Tests")
	public void registerUserCorrect() {
		RegisterService service = new RegisterService(authDAO, userDAO);	

		String username = "MySuperUsername";
		String password = "SuperSecure";
		String email = "hehe@hehe.com";

		RegisterRequest request = new RegisterRequest(username, password, email);
		RegisterResult result;
		// shouldn't throw
		result = Assertions.assertDoesNotThrow(() -> service.register(request));

		// Test to make sure the username is returned
		Assertions.assertEquals(result.username(), username);
		// Make sure an auth token was returned
		Assertions.assertNotNull(result.authToken());
	}

	@Test
	@DisplayName("Incorrect Register User Tests")
	public void registerUserIncorrect() {
		RegisterService service = new RegisterService(authDAO, userDAO);

		String username = "FooMan";

		RegisterRequest request1 = new RegisterRequest(username, "p@ssword", "email@email");
		// Shouldn't throw
		Assertions.assertDoesNotThrow(() -> service.register(request1));

		RegisterRequest request2 = new RegisterRequest(username, "newPassword", "newEmail@email");
		Assertions.assertThrows(DataAccessException.class, () -> service.register(request2));
	}
}
