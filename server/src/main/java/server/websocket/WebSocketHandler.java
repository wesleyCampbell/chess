package server.websocket;

import io.javalin.websocket.*;
import model.*;
import util.Debugger;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessGame.TeamColor;
import dataaccess.*;
import websocket.commands.UserGameCommand;
import websocket.messages.GameNoExistError;
import websocket.messages.PlayerJoinNotification;
import websocket.messages.PlayerLeaveNotification;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ServerMessageType;

/**
 * A class that will translate web socket requests into ones the server can understand
 */
public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
	private final ConnectionsManager connections = new ConnectionsManager();

	private final static String GAME_NO_EXIST_MSG = new GameNoExistError().toJson();

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
				case MAKE_MOVE -> makeMove();
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
		} catch (DataAccessException|AuthenticationException ex) {
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
		if (username == gameData.whiteUsername()) {
			teamColor = TeamColor.WHITE.toString();
		} else if (username == gameData.blackUsername()) {
			teamColor = TeamColor.BLACK.toString();
		} else {
			teamColor = "observer";
		}

		Debugger.debug(String.format("team color is %s", teamColor), 3);

		ServerMessage notification = new PlayerJoinNotification(username, teamColor); 

		this.connections.broadcast(gameID, session, notification);
	}

	private void makeMove() {
		Debugger.debug("Making move");
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
