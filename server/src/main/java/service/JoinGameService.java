package service;

import chess.ChessGame.TeamColor;
import dataaccess.*;

import model.GameData;

public class JoinGameService extends AuthenticableService {

	//
	// ================== STATIC PUBLIC CLASSES =====================
	//
	
	public static record JoinGameRequest(String authToken, TeamColor playerColor, String gameID) {}
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
	
	public JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException, AuthenticationException {
		if (!this.isAuthenticated(this.authDAO, request.authToken())) {
			throw new AuthenticationException("User is not authenticated");
		}
		
		// Will throw an error if the game doesn't exist. 
		// We will let it propogate up.
		GameData game = this.gameDAO.getGame(request.gameID);

		TeamColor playerColor = request.playerColor();
		String username = this.authDAO.getAuth(request.authToken()).username();

		String blackUsername, whiteUsername;
		switch (playerColor) {
			case TeamColor.WHITE:
				blackUsername = game.blackUsername();
				if (!game.whiteUsername().isEmpty()) {
					throw new DataAccessException("White already taken");
				}
				whiteUsername = username;
				break;
			case TeamColor.BLACK:
				whiteUsername = game.whiteUsername();
				if (!game.blackUsername().isEmpty()) {
					throw new DataAccessException("Black already taken");
				}
				blackUsername = username;
				break;
			default:
				throw new DataAccessException("Only black and white teams currently supported");
		}

		GameData newGameData = new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), game.game());

		this.gameDAO.updateGame(game.gameID(), newGameData);

		return new JoinGameResult();
	}
}
