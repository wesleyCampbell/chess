package service;

import dataaccess.*;
import dataaccess.memorydao.*;

import service.RegisterService.RegisterRequest;
import service.RegisterService.RegisterResult;
import service.ClearDataService.ClearDataRequest;
import service.CreateGameService.CreateGameRequest;
import service.CreateGameService.CreateGameResult;

import org.junit.jupiter.api.*;

public class ClearDataServiceTests {
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

		CreateGameService gamesService = new CreateGameService(authDAO, gameDAO);

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
	@DisplayName("ClearData Service Unit Tests") 
	public void clearData() {
		ClearDataService service = new ClearDataService(authDAO, gameDAO, userDAO);


		ClearDataRequest request = new ClearDataRequest();

		Assertions.assertDoesNotThrow(() -> service.clearData(request));
	}

}
