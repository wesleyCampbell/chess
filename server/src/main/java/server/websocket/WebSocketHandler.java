package server.websocket;

import io.javalin.websocket.*;
import model.*;
import util.Debugger;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import chess.*;
import chess.ChessGame.TeamColor;

import dataaccess.*;

import websocket.commands.*;
import websocket.messages.*;

/**
 * A class that will translate web socket requests into ones the server can understand
 */
public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
	private final ConnectionsManager connections = new ConnectionsManager();

	private final static String GAME_NO_EXIST_MSG = new GameNoExistError().toJson();
	private final static String NO_AUTH_MSG = new NoAuthError().toJson();
	private final static String INT_ERROR_MSG = new IntServerError().toJson();
	private final static String INVALID_MOVE_MSG = new InvalidMoveError().toJson();
	private final static String GAME_CLOSED_MSG = new GameClosedError().toJson();
	private final static String NOT_TURN_MSG = new NotYourTurnError().toJson();
	private final static String NOT_PLAYING_ERROR = new NotPlayingError().toJson();

	private AuthDAO authDAO;
	private GameDAO gameDAO;
	private UserDAO userDAO;

	private final static Gson GSON = new GsonBuilder()
		.registerTypeAdapter(ChessGame.class, new ChessGame.ChessGameDeserializer())
		.registerTypeAdapter(ChessPiece.class, new ChessPiece.ChessPieceDeserializer())
		.create();

	public WebSocketHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
		this.authDAO = authDAO;
		this.userDAO = userDAO;
		this.gameDAO = gameDAO;
	}

	private boolean isAuthenticated(String authToken) {
		try {
			return this.authDAO.authExists(authToken);
		} catch (DataAccessException ex) {
			return false;
		}
	}

	@Override
	public void handleConnect(WsConnectContext ctx) {
		ctx.enableAutomaticPings();
	}

	@Override
	public void handleMessage(WsMessageContext ctx) {
		UserGameCommand cmd = GSON.fromJson(ctx.message(), UserGameCommand.class);

		Session session = ctx.session;

		String authToken = cmd.getAuthToken();

		if (!this.isAuthenticated(authToken)) {
			// ctx.closeSession();
			return;
		}
		
		try {
			switch (cmd.getCommandType()) {
				case CONNECT -> connect(ctx);
				case MAKE_MOVE -> makeMove(ctx);
				case LEAVE -> leave(ctx);
				case RESIGN -> resign(ctx);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void handleClose(WsCloseContext ctx) {
	}

	// 
	// ENDPOINTS
	//

	private void connect(WsMessageContext ctx) throws IOException {

		// Extract relevant info from context
		UserGameCommand cmd = GSON.fromJson(ctx.message(), UserGameCommand.class);
		String authToken = cmd.getAuthToken();
		int gameID = cmd.getGameID();
		Session session = ctx.session;
		
		// Get the auth data for the username
		AuthData authData;
		try {
			authData = this.authDAO.getAuth(authToken);
		} catch (AuthenticationException ex) {
			session.getRemote().sendString(NO_AUTH_MSG);
			return;
		} catch (DataAccessException ex) {
			session.getRemote().sendString(INT_ERROR_MSG);
			ex.printStackTrace();
			return;
		}
		// Get the game data and verify that it exists
		GameData gameData;
		try {
			gameData = this.gameDAO.getGame(String.valueOf(gameID));
		} catch (DataAccessException ex) {
			session.getRemote().sendString(GAME_NO_EXIST_MSG);
			return;
		}

		this.connections.add(gameID, session);

		String username = authData.username();

		String teamColor;
		if (username.equals(gameData.whiteUsername())) {
			teamColor = TeamColor.WHITE.toString();
		} else if (username.equals(gameData.blackUsername())) {
			teamColor = TeamColor.BLACK.toString();
		} else {
			teamColor = "observer";
		}


		ServerMessage notification = new PlayerJoinNotification(username, teamColor); 

		this.connections.broadcast(gameID, session, notification);
	}

	private void makeMove(WsMessageContext ctx) throws IOException {

		// Extract relevant info from the context
		MakeMoveCommand cmd = GSON.fromJson(ctx.message(), MakeMoveCommand.class);
		String authToken = cmd.getAuthToken();
		int gameID = cmd.getGameID();
		ChessMove move = cmd.getMove();


		Session session = ctx.session;
		// Get the auth data for the username
		AuthData authData;
		try {
			authData = this.authDAO.getAuth(authToken);
		} catch (AuthenticationException ex) {
			session.getRemote().sendString(NO_AUTH_MSG);
			return;
		} catch (DataAccessException ex) {
			session.getRemote().sendString(INT_ERROR_MSG);
			ex.printStackTrace();
			return;
		}

		// Get the game data and verify that it exists
		GameData gameData;
		try {
			gameData = this.gameDAO.getGame(String.valueOf(gameID));
		} catch (DataAccessException ex) {
			session.getRemote().sendString(GAME_NO_EXIST_MSG);
			return;
		}

		// Verify that it is the player's turn who is trying to make the move
		String username = authData.username();
		TeamColor activeTeam = gameData.game().getTeamTurn();

		String activeUser;
		switch (activeTeam) {
			case WHITE:
				activeUser = gameData.whiteUsername();
				break;
			case BLACK:
				activeUser = gameData.blackUsername();
				break;
			default:
				session.getRemote().sendString(INT_ERROR_MSG);
				return;
		}


		// make sure that the activeUser matches the username
		if (!activeUser.equals(username)) {
			session.getRemote().sendString(NOT_TURN_MSG);
			return;
		}

		// Verify that the game hasn't ended allready
		if (!this.connections.isGameActive(gameID)) {
			session.getRemote().sendString(GAME_CLOSED_MSG);
			return;
		}

		// Make the move
		try {
			gameData.game().makeMove(move);
		} catch (InvalidMoveException ex) {
			session.getRemote().sendString(INVALID_MOVE_MSG);
			return;
		}

		// Update the game in the database
		try {
			this.gameDAO.updateGame(Integer.toString(gameID), gameData);
		} catch (DataAccessException ex) {
			session.getRemote().sendString(INT_ERROR_MSG);
			return;
		}

		// Send out notifications to all connected players
		ServerMessage moveNotification = new PlayerMoveNotification(username, move); 
		this.connections.broadcast(gameID, session, moveNotification);
		// Tell all connected players to redraw their screen
		moveNotification = new RedrawBoardMessage();
		this.connections.broadcastAll(gameID, moveNotification);

		// check to see if there is a player in check/checkmate/stalemate
		ChessGame game = gameData.game();
		ServerMessage msg = null;
		boolean gameOver = false;

		for (ChessGame.TeamColor color : TeamColor.values()) {
			if (game.isInCheckmate(color)) {
				msg = new CheckmateNotification(username);
				gameOver = true;
				break;
			} else if (game.isInCheck(color)) {
				msg = new CheckNotification(username);
				break;
			} else if (game.isInStalemate(color)) {
				msg = new StalemateNotification(username);
				gameOver = true;
				break;
			}

		}

		if (msg != null) {
			this.connections.broadcastAll(gameID, msg);
		}

		if (gameOver) {
			ServerMessage gameOverMsg = new GameOverMessage();
			this.connections.broadcastAll(gameID, gameOverMsg);
			this.connections.setGameInactive(gameID);
		}
	}

	private void leave(WsMessageContext ctx) throws IOException {
		UserGameCommand cmd = GSON.fromJson(ctx.message(), UserGameCommand.class);

		int gameID = cmd.getGameID();
		String authToken = cmd.getAuthToken();
		Session session = ctx.session;

		this.connections.remove(gameID, session);

		AuthData auth;
		try {
			auth = this.authDAO.getAuth(authToken);
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return;
		} catch (AuthenticationException ex) {
			ex.printStackTrace();
			return;
		}

		String username = auth.username();


		ServerMessage notification = new PlayerLeaveNotification(username);

		this.connections.broadcast(gameID, session, notification);
	}

	private void resign(WsMessageContext ctx) throws IOException {

		// Extract necessary info from the context object
		UserGameCommand cmd = GSON.fromJson(ctx.message(), UserGameCommand.class);
		int gameID = cmd.getGameID();
		String authToken = cmd.getAuthToken();
		Session session = ctx.session;
		
		// Verify authData
		AuthData auth;
		try {
			auth = this.authDAO.getAuth(authToken);
		} catch (DataAccessException ex) {
			session.getRemote().sendString(INT_ERROR_MSG);
			return;
		} catch (AuthenticationException ex) {
			session.getRemote().sendString(NO_AUTH_MSG);
			return;
		}

		// get the game data
		GameData gameData;
		try {
			gameData = this.gameDAO.getGame(Integer.toString(gameID));
		} catch (DataAccessException ex) {
			session.getRemote().sendString(INT_ERROR_MSG);
			return;
		}

		// verify that the player resigning is actually playing in the game
		String username = auth.username();
		String blackUsername = gameData.blackUsername();
		String whiteUsername = gameData.whiteUsername();

		if (!username.equals(blackUsername) && 
				!username.equals(whiteUsername)) {
			session.getRemote().sendString(NOT_PLAYING_ERROR);
			return;
		}

		this.connections.setGameInactive(gameID);

		ServerMessage notification = new PlayerResignNotification(username);

		this.connections.broadcast(gameID, session, notification);

		ServerMessage gameOverMsg = new GameOverMessage();

		this.connections.broadcastAll(gameID, gameOverMsg);
	}
}
