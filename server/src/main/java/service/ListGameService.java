package service;

import java.util.Collection;
import java.util.HashSet;

import dataaccess.AuthDAO;
import dataaccess.AuthenticationException;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;

import model.GameData;

public class ListGameService extends AuthenticableService {
	//
	// ================= PUBLIC STATIC CLASSES ==================
	//
	
	public static record ListGameRequest(String authToken) {}

	public static record ListGameResult(Collection<GameData> games) {}
	
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
	
	/**
	 * Takes a valid ListGamesRequest and returns a Response that includes the formated 
	 * data of all games in the database ready to be serialized into a HTTP response.
	 *
	 * @param request The ListGamesRequest 
	 *
	 * @return The response containing revelant data
	 */
	public ListGameResult listGames(ListGameRequest request) throws AuthenticationException, DataAccessException {
		if (!this.isAuthenticated(this.authDAO, request.authToken())) {
			throw new AuthenticationException("User is not authenticated");
		}

		Collection<GameData> games = this.gameDAO.getAllGames();

		ListGameResult result = new ListGameResult(games); 
		return result;
	}
}
