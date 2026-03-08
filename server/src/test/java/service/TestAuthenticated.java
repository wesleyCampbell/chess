package service;

import dataaccess.*;
import dataaccess.memorydao.*;

import service.RegisterService.RegisterRequest;
import service.RegisterService.RegisterResult;
import service.ClearDataService.ClearDataRequest;
import service.CreateGameService.CreateGameRequest;
import service.CreateGameService.CreateGameResult;

import org.junit.jupiter.api.*;

public abstract class TestAuthenticated {
	private AuthDAO authDAO;
	private UserDAO userDAO;
	private GameDAO gameDAO;

	private int createGamesNum;

	public TestAuthenticated(int createGamesNum) {
		authDAO = new MemoryAuthDAO();
		userDAO = new MemoryUserDAO();
		gameDAO = new MemoryGameDAO();
		this.createGamesNum = createGamesNum;
	}

	private final static int CREATE_GAMES_NUM = 4;

	private static String authToken;

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
}
