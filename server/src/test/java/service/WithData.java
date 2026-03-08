package service;

import dataaccess.*;
import dataaccess.memorydao.*;

import service.RegisterService.RegisterRequest;
import service.RegisterService.RegisterResult;

import org.junit.jupiter.api.*;


public abstract class WithData {
	protected static AuthDAO authDAO;
	protected static UserDAO userDAO;
	protected static GameDAO gameDAO;

	protected final static int CREATE_GAMES_NUM = 4;

	protected static String authToken;

	protected static String username;
	protected static String password;
	protected static String email;
	
	//
	// ======================= TEST SETUP/CLEANUP ======================= 
	//
	
	@Order(0)
	@BeforeEach
	public void init() {
		authDAO = new MemoryAuthDAO();
		userDAO = new MemoryUserDAO();
		gameDAO = new MemoryGameDAO();

		RegisterService registerService = new RegisterService(authDAO, userDAO);

		username = "Samwise Fooman";
		password = "p@sw@ord";
		email = "thisisanemail@email.com";

		RegisterRequest regRequest = new RegisterRequest(username, password, email);
		RegisterResult regResult;

		regResult = Assertions.assertDoesNotThrow(() -> registerService.register(regRequest));

		authToken = regResult.authToken();

	}
}
