package service;

import java.util.Collection;

import dataAccess.AuthDAO;
import dataAccess.AuthenticationException;
import dataAccess.GameDAO;
import dataAccess.DataAccessException;

import model.GameData;

public class ListGameService extends AuthenticableService {
	//
	// ================= PUBLIC STATIC CLASSES ==================
	//
	
	public static record ListGameRequest(String authToken) {}

	public static record ListGameResult(Collection<GameData> gameData) {}
	
	//
	// ================= CONSTRUCTORS ==================
	//
	
	private AuthDAO authDAO;
	private GameDAO gameDAO;

	public ListGameService(AuthDAO authDAO, GameDAO gameDAO) {
		this.authDAO = authDAO;
		this.gameDAO = gameDAO;
	}

	//
	// ================= MEMBER METHODS ==================
	//
	
	public ListGameResult listGames(ListGameRequest request) throws AuthenticationException {
		if (!this.isAuthenticated(this.authDAO, request.authToken())) {
			throw new AuthenticationException("User is not authenticated");
		}

		Collection<GameData> games = this.gameDAO.getAllGames();

		return new ListGameResult(games);
	}
}
