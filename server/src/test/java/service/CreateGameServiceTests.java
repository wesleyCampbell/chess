package service;

import dataaccess.*;
import dataaccess.memorydao.*;

import service.RegisterService.RegisterRequest;
import service.RegisterService.RegisterResult;

import service.CreateGameService.CreateGameRequest;
import service.CreateGameService.CreateGameResult;

import org.junit.jupiter.api.*;

public class CreateGameServiceTests {
	private static AuthDAO authDAO;
	private static UserDAO userDAO;
	private static GameDAO gameDAO;

	private final static int createGamesNum = 4;

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
	@DisplayName("Correct Create Game User Tests")
	public void logoutUserCorect() {
		CreateGameService gamesService = new CreateGameService(authDAO, gameDAO);

		for (int i = 0; i < createGamesNum; i++) {
			String gameName = String.format("game%d", i + 1);
			CreateGameRequest request = new CreateGameRequest(authToken, gameName);
			CreateGameResult result;
			result = Assertions.assertDoesNotThrow(() -> gamesService.createGame(request));
			Assertions.assertEquals(result.gameID(), String.valueOf(i + 1));
		}
	}

	@Test
	@DisplayName("Incorrect Create Game User Tests")
	public void logoutUserIncorrect() {
		CreateGameService gamesService = new CreateGameService(authDAO, gameDAO);
		CreateGameRequest request = new CreateGameRequest("BAD AUTH TOKEN", "game");

		Assertions.assertThrows(AuthenticationException.class, () -> gamesService.createGame(request));
	}
}
