package service;

import dataAccess.*;

public class JoinGameService {

	//
	// ================== STATIC PUBLIC CLASSES =====================
	//
	
	public static record JoinGameRequest(String authToken, String playerColor, String gameID) {}
	public static record JoinGameResult() {}

	//
	// ================== CONSTRUCTORS =====================
	//
	
	private AuthDAO authDAO;
	private GameDAO gameDAO;

	public JoinGameService(AuthDAO authDAO, GameDAO gameDAO) {
		this.authDAO = authDAO;
		this.gameDAO = gameDAO;
	}

	//
	// ================== CONSTRUCTORS =====================
	//
	
	public JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");
	}
}
