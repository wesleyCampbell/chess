package service;

import dataaccess.*;
import dataaccess.memorydao.*;

import service.RegisterService.RegisterRequest;
import service.RegisterService.RegisterResult;

import service.CreateGameService.CreateGameRequest;
import service.JoinGameService.JoinGameRequest;

import org.junit.jupiter.api.*;

import chess.ChessGame.TeamColor;

public class JoinGameServiceTests {
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
	@DisplayName("Correct Join Game User Tests")
	public void logoutUserCorect() {
		JoinGameService service = new JoinGameService(authDAO, gameDAO);
		JoinGameRequest request = new JoinGameRequest(authToken, TeamColor.WHITE, "1");

		Assertions.assertDoesNotThrow(() -> service.joinGame(request));
	}

	@Test
	@DisplayName("Incorrect Join Game User Tests")
	public void logoutUserIncorrect() {
		JoinGameService service = new JoinGameService(authDAO, gameDAO);
		String badAuthToken = "BAD AUTH TOKEN";
		JoinGameRequest request = new JoinGameRequest(badAuthToken, TeamColor.WHITE, "1");

		Assertions.assertThrows(AuthenticationException.class, () -> service.joinGame(request));


		JoinGameRequest request1 = new JoinGameRequest(authToken, TeamColor.WHITE, "1");
		Assertions.assertDoesNotThrow(() -> service.joinGame(request1));

		Assertions.assertThrows(DataAccessException.class, () -> service.joinGame(request1));

		JoinGameRequest request2 = new JoinGameRequest(authToken, TeamColor.BLACK, "1");
		Assertions.assertDoesNotThrow(() -> service.joinGame(request2));
		Assertions.assertThrows(DataAccessException.class, () -> service.joinGame(request2));
	}
}
