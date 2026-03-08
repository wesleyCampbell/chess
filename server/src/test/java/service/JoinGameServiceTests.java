package service;

import dataaccess.*;
import dataaccess.memorydao.*;

import service.RegisterService.RegisterRequest;
import service.RegisterService.RegisterResult;

import service.CreateGameService.CreateGameRequest;
import service.JoinGameService.JoinGameRequest;

import org.junit.jupiter.api.*;

import chess.ChessGame.TeamColor;

public class JoinGameServiceTests extends WithAllData {
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
