package service;

import com.google.gson.Gson;

import dataaccess.AuthDAO;
import dataaccess.AuthenticationException;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;

import model.GameData;
import util.Debugger;

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
	
	public CreateGameResult createGame(CreateGameRequest request) throws AuthenticationException, DataAccessException{
		Debugger.debug("In createGame()...", 0);
		if (this.isAuthenticated(authDAO, request.authToken())) {
			GameData data = this.gameDAO.createGame(request.gameName());

			Debugger.debug(String.format("gameData: %s", data), 1);
			Debugger.debug(String.format("gameJson: %s", new Gson().toJson(data)), 2);

			CreateGameResult result = new CreateGameResult(data.gameID());

			return result;
		}
		
		throw new AuthenticationException("User is not authenticated");
	}
}
