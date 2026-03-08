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

	public static record GameDataAPI(String gameID, String whiteUsername, String blackUsername, String gameName) {
		public GameDataAPI(GameData game) {
			this(game.gameID(), game.whiteUsername(),
					game.blackUsername(), game.gameName());
		}
	}

	public static record ListGameResult(Collection<GameDataAPI> games) {}
	
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
	 * Formats a collection of gameData to exclude the actual ChessGame object,
	 * which we don't necessarily want to serialize into a HTTP response.
	 *
	 * @param gamesData The collection of game data
	 *
	 * @return A ListGamesResult ready to be serialized into json for HTTP
	 */
	private ListGameResult formatGamesData(Collection<GameData> gamesData) {
		Collection<GameDataAPI> formatedGameData = new HashSet<>();

		for (GameData game : gamesData) {
			formatedGameData.add(new GameDataAPI(game));
		}

		return new ListGameResult(formatedGameData);
	}
	
	/**
	 * Takes a valid ListGamesRequest and returns a Response that includes the formated 
	 * data of all games in the database ready to be serialized into a HTTP response.
	 *
	 * @param request The ListGamesRequest 
	 *
	 * @return The response containing revelant data
	 */
	public ListGameResult listGames(ListGameRequest request) throws AuthenticationException {
		if (!this.isAuthenticated(this.authDAO, request.authToken())) {
			throw new AuthenticationException("User is not authenticated");
		}

		Collection<GameData> games = this.gameDAO.getAllGames();

		ListGameResult result = this.formatGamesData(games);

		return result;
	}
}
