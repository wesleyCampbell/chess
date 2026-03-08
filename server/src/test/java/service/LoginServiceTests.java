package service;

import dataaccess.*;
import dataaccess.memorydao.*;

import service.RegisterService.RegisterRequest;
import service.RegisterService.RegisterResult;
import service.LoginService.LoginRequest;
import service.LoginService.LoginResult;
import service.LogoutService.LogoutRequest;
import service.LogoutService.LogoutResult;

import org.junit.jupiter.api.*;

public class LoginServiceTests {
	private static AuthDAO authDAO;
	private static UserDAO userDAO;
	private static GameDAO gameDAO;

	private static String authToken;

	private static String username;
	private static String password;

	//
	// ======================= TEST SETUP/CLEANUP ======================= 
	//
	
	@BeforeEach
	public void init() {
		authDAO = new MemoryAuthDAO();
		userDAO = new MemoryUserDAO();
		gameDAO = new MemoryGameDAO();

		RegisterService registerService = new RegisterService(authDAO, userDAO);
		LogoutService logoutService = new LogoutService(authDAO);

		username = "Samwise Fooman";
		password = "p@sw@ord";
		String email = "thisisanemail@email.com";
		RegisterRequest regRequest = new RegisterRequest(username, password, email);
		RegisterResult regResult;

		regResult = Assertions.assertDoesNotThrow(() -> registerService.register(regRequest));

		authToken = regResult.authToken();

		Assertions.assertDoesNotThrow(() -> logoutService.logout(new LogoutRequest(authToken)));
	}

	//
	// ======================= TEST CASES ======================= 
	//

	@Test
	@DisplayName("Correct Login User Tests")
	public void logoutUserCorect() {
		LoginService loginService = new LoginService(authDAO, userDAO);

		LoginRequest loginRequest = new LoginRequest(username, password);

		Assertions.assertDoesNotThrow(() -> loginService.login(loginRequest));
	}

	@Test
	@DisplayName("Incorrect Login User Tests")
	public void loginUserIncorrect() {
		LoginService loginService = new LoginService(authDAO, userDAO);

		String wrongUsername = "THIS IS NOT THE USERNAME";
		LoginRequest loginRequest1 = new LoginRequest(wrongUsername, password);

		Assertions.assertThrows(DataAccessException.class, () -> loginService.login(loginRequest1));

		String wrongPassword = "THIS IS NOT THE PASSWORD";
		LoginRequest loginRequest2 = new LoginRequest(username, wrongPassword);
		Assertions.assertThrows(DataAccessException.class, () -> loginService.login(loginRequest1));

	}
}

