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

public class LoginServiceTests extends WithDataTests {
	//
	// ======================= TEST SETUP/CLEANUP ======================= 
	//

	@Order(1)
	@BeforeEach
	public void logout() {
		LogoutService logoutService = new LogoutService(authDAO);
		
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

		Assertions.assertThrows(AuthenticationException.class, () -> loginService.login(loginRequest1));

		String wrongPassword = "THIS IS NOT THE PASSWORD";
		LoginRequest loginRequest2 = new LoginRequest(username, wrongPassword);
		Assertions.assertThrows(AuthenticationException.class, () -> loginService.login(loginRequest1));

	}
}

