package service;

import dataaccess.*;

import service.CreateGameService.CreateGameRequest;
import service.CreateGameService.CreateGameResult;

import org.junit.jupiter.api.*;

public class CreateGameServiceTests extends WithData {
	
	//
	// ======================= TEST CASES ======================= 
	//

	@Test
	@DisplayName("Correct Create Game User Tests")
	public void logoutUserCorect() {
		CreateGameService gamesService = new CreateGameService(authDAO, gameDAO);

		for (int i = 0; i < CREATE_GAMES_NUM; i++) {
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
