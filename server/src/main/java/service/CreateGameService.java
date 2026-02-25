package service;

import dataAccess.AuthDAO;
import dataAccess.AuthenticationException;
import dataAccess.GameDAO;
import dataAccess.DataAccessException;

public class CreateGameService {
	//
	// ================== PUBLIC STATIC CLASS ====================
	//
	
	public static record CreateGameRequest(String authToken, String gameName) {}
	public static record CreateGameResult(String gameID) {}
	
	//
	// ================== CONSTRUCTORS ====================
	//
	
	private AuthDAO authDAO;
	private GameDAO gameDAO;

	public CreateGameService(AuthDAO authDAO, GameDAO gameDAO) {
		this.authDAO = authDAO;
		this.gameDAO = gameDAO;
	}

	//
	// ================== MEMBER METHODS ====================
	//
	
	public CreateGameResult createGame(CreateGameRequest request) throws AuthenticationException {
		throw new AuthenticationException("NOT IMPLEMENTED YET");
	}
}
