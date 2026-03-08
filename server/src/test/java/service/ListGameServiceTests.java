package service;

import dataaccess.*;
import dataaccess.memorydao.*;

import service.RegisterService.RegisterRequest;
import service.RegisterService.RegisterResult;

import service.CreateGameService.CreateGameRequest;
import service.ListGameService.GameDataAPI;
import service.ListGameService.ListGameRequest;
import service.ListGameService.ListGameResult;


import org.junit.jupiter.api.*;


public class ListGameServiceTests {
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
		CreateGameService gamesService = new CreateGameService(authDAO, gameDAO);

		String username = "Samwise Fooman";
		String password = "p@sw@ord";
		String email = "thisisanemail@email.com";
		RegisterRequest regRequest = new RegisterRequest(username, password, email);
		RegisterResult regResult;

		regResult = Assertions.assertDoesNotThrow(() -> registerService.register(regRequest));

		authToken = regResult.authToken();

		for (int i = 0; i < createGamesNum; i++) {
			String gameName = String.format("game%d", i + 1);
			CreateGameRequest request = new CreateGameRequest(authToken, gameName);
			Assertions.assertDoesNotThrow(() -> gamesService.createGame(request));
		}
	}
	
	//
	// ======================= TEST CASES ======================= 
	//
	
	@Test
	@DisplayName("Correct List Games User Tests")
	public void listGamesCorrect() {
		ListGameService service = new ListGameService(authDAO, gameDAO);
		
		ListGameRequest request = new ListGameRequest(authToken);
		ListGameResult result;
		result = Assertions.assertDoesNotThrow(() -> service.listGames(request));
		
		Assertions.assertEquals(result.games().size(), createGamesNum);
	}

	@Test
	@DisplayName("Incorrect List Games User Tests")
	public void listGamesIncorrect() {
		ListGameService service = new ListGameService(authDAO, gameDAO);

		ListGameRequest request = new ListGameRequest("WRONG AUTH TOKEN");

		Assertions.assertThrows(AuthenticationException.class, () -> service.listGames(request));
	}
}
