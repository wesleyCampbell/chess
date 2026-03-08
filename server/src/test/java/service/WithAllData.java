package service;

import service.CreateGameService.CreateGameRequest;

import org.junit.jupiter.api.*;

public abstract class WithAllData extends WithData {
	@Order(1)
	@BeforeEach
	public void makeGames() {
		CreateGameService gamesService = new CreateGameService(authDAO, gameDAO);

		for (int i = 0; i < CREATE_GAMES_NUM; i++) {
			String gameName = String.format("game%d", i + 1);
			CreateGameRequest request = new CreateGameRequest(authToken, gameName);
			Assertions.assertDoesNotThrow(() -> gamesService.createGame(request));
		}
	}
}
