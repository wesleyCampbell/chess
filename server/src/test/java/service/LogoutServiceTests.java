package service;

import dataaccess.*;
import dataaccess.memorydao.*;

import service.RegisterService.RegisterRequest;
import service.RegisterService.RegisterResult;

import service.LogoutService.LogoutRequest;
import service.LogoutService.LogoutResult;

import org.junit.jupiter.api.*;

public class LogoutServiceTests {
	private static AuthDAO authDAO;
	private static UserDAO userDAO;
	private static GameDAO gameDAO;

	private static String authToken;

	//
	// ======================= TEST SETUP/CLEANUP ======================= 
	//
	
	@BeforeEach
	public void init() {
		authDAO = new MemoryAuthDAO();
		userDAO = new MemoryUserDAO();
		gameDAO = new MemoryGameDAO();

		RegisterService registerService = new RegisterService(authDAO, userDAO);

		String username = "Samwise Fooman";
		String password = "p@sw@ord";
		String email = "thisisanemail@email.com";
		RegisterRequest regRequest = new RegisterRequest(username, password, email);
		RegisterResult regResult;

		regResult = Assertions.assertDoesNotThrow(() -> registerService.register(regRequest));

		authToken = regResult.authToken();
	}

	//
	// ======================= TEST CASES ======================= 
	//
	
	@Test
	@DisplayName("Correct Logout User Tests")
	public void logoutUserCorect() {
		LogoutService logoutService = new LogoutService(authDAO);

		LogoutRequest logoutRequest = new LogoutRequest(authToken);

		Assertions.assertDoesNotThrow(() -> logoutService.logout(logoutRequest));
	}

	@Test
	@DisplayName("Incorrect Logout User Tests")
	public void logoutUserIncorrect() {
		LogoutService logoutService = new LogoutService(authDAO);

		String wrongAuthToken = "THIS IS NOT AN AUTH TOKEN";
		LogoutRequest logoutRequest = new LogoutRequest(wrongAuthToken);

		Assertions.assertThrows(AuthenticationException.class, () -> logoutService.logout(logoutRequest));
	}
}

