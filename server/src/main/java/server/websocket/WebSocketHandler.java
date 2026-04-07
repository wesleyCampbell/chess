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
		Debugger.debug("Websocket connected...");
		ctx.enableAutomaticPings();
	}

	@Override
	public void handleMessage(WsMessageContext ctx) {
		Debugger.debug("Message recieved", 1);
		UserGameCommand cmd = GSON.fromJson(ctx.message(), UserGameCommand.class);
		Debugger.debug(String.format("Message: %s", cmd), 1);

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
				case RESIGN -> resign();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void handleClose(WsCloseContext ctx) {
		Debugger.debug("Websocket closing...");
	}

	// 
	// ENDPOINTS
	//

	private void connect(WsMessageContext ctx) throws IOException {
		Debugger.debug("Connecting to gameSocket...");

		// Extract relevant info from context
		UserGameCommand cmd = GSON.fromJson(ctx.message(), UserGameCommand.class);
		String authToken = cmd.getAuthToken();
		int gameID = cmd.getGameID();
		Session session = ctx.session;
		
		Debugger.debug("Getting auth data", 3);
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
		Debugger.debug("Getting gameData", 3);
		GameData gameData;
		try {
			gameData = this.gameDAO.getGame(String.valueOf(gameID));
		} catch (DataAccessException ex) {
			session.getRemote().sendString(GAME_NO_EXIST_MSG);
			return;
		}

		this.connections.add(gameID, session);

		String username = authData.username();
		Debugger.debug(String.format("username is %s", username), 3);

		String teamColor;
		if (username.equals(gameData.whiteUsername())) {
			teamColor = TeamColor.WHITE.toString();
		} else if (username.equals(gameData.blackUsername())) {
			teamColor = TeamColor.BLACK.toString();
		} else {
			teamColor = "observer";
		}

		Debugger.debug(String.format("team color is %s", teamColor), 3);

		ServerMessage notification = new PlayerJoinNotification(username, teamColor); 

		this.connections.broadcast(gameID, session, notification);
	}

	private void makeMove(WsMessageContext ctx) throws IOException {
		Debugger.debug("Making move");

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
		String username = authData.username();
		ServerMessage moveNotification = new PlayerMoveNotification(username, move); 
		this.connections.broadcast(gameID, session, moveNotification);
		// Tell all connected players to redraw their screen
		moveNotification = new RedrawBoardMessage();
		this.connections.broadcast(gameID, session, moveNotification);
	}

	private void leave(WsMessageContext ctx) throws IOException {
		UserGameCommand cmd = GSON.fromJson(ctx.message(), UserGameCommand.class);

		int gameID = cmd.getGameID();
		String authToken = cmd.getAuthToken();
		Session session = ctx.session;

		Debugger.debug("leaving gameSocket");
		this.connections.remove(gameID, session);

		AuthData auth;
		try {
			auth = this.authDAO.getAuth(authToken);
		} catch (DataAccessException ex) {
			Debugger.debug("dataaccess exception...");
			ex.printStackTrace();
			return;
		} catch (AuthenticationException ex) {
			Debugger.debug("auth exception");
			ex.printStackTrace();
			return;
		}

		String username = auth.username();

		Debugger.debug(String.format("authData: %s", auth), 1);

		ServerMessage notification = new PlayerLeaveNotification(username);

		this.connections.broadcast(gameID, session, notification);
	}

	private void resign() {
		Debugger.debug("resigning from game");
	}

}
