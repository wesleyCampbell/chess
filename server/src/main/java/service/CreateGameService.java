package service;

import dataAccess.AuthDAO;
import dataAccess.AuthenticationException;
import dataAccess.GameDAO;
import dataAccess.DataAccessException;

import model.GameData;

public class CreateGameService extends AuthenticableService {
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
		if (this.isAuthenticated(authDAO, request.authToken())) {
			GameData data = this.gameDAO.createGame(request.gameName());

			CreateGameResult result = new CreateGameResult(data.gameID());

			return result;
		}
		
		throw new AuthenticationException("User is not authenticated");
	}
}
