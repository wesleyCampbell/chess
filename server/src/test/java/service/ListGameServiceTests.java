package service;

import dataaccess.*;
import service.ListGameService.ListGameRequest;
import service.ListGameService.ListGameResult;


import org.junit.jupiter.api.*;

public class ListGameServiceTests extends WithAllData {
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
		
		Assertions.assertEquals(result.games().size(), CREATE_GAMES_NUM);
	}

	@Test
	@DisplayName("Incorrect List Games User Tests")
	public void listGamesIncorrect() {
		ListGameService service = new ListGameService(authDAO, gameDAO);

		ListGameRequest request = new ListGameRequest("WRONG AUTH TOKEN");

		Assertions.assertThrows(AuthenticationException.class, () -> service.listGames(request));
	}
}
