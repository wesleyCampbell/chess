package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.DataAccessException;

public class CreateGameService {
	//
	// ================== PUBLIC STATIC CLASS ====================
	//
	
	public static record CreateGameRequest(String authToken, String gameName) {}
	public static record CreateGameResponse(String gameID) {}
	
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
	
	public CreateGameRequest createGame(CreateGameRequest request) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");
	}
}
