package server.websocket;

import io.javalin.websocket.*;
import util.Debugger;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import chess.ChessGame;
import chess.ChessPiece;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ServerMessageType;

/**
 * A class that will translate web socket requests into ones the server can understand
 */
public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
	private final ConnectionsManager connections = new ConnectionsManager();

	private final static Gson GSON = new GsonBuilder()
		.registerTypeAdapter(ChessGame.class, new ChessGame.ChessGameDeserializer())
		.registerTypeAdapter(ChessPiece.class, new ChessPiece.ChessPieceDeserializer())
		.create();

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
		int gameID = cmd.getGameID();
		
		try {
			switch (cmd.getCommandType()) {
				case CONNECT -> connect(authToken, gameID, session);
				case MAKE_MOVE -> makeMove();
				case LEAVE -> leave(authToken, gameID, session);
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

	private void connect(String authToken, int gameID, Session session) throws IOException {
		Debugger.debug("Connecting to gameSocket...");
		
		this.connections.add(gameID, session);
		ServerMessage notification = new ServerMessage(ServerMessageType.NOTIFICATION); 



		Debugger.debug(String.format("There are %d connections in game %d", connections.getConn(gameID).size(), gameID));

		this.connections.broadcast(gameID, session, notification);
	}

	private void makeMove() {
		Debugger.debug("Making move");
	}

	private void leave(String authToken, int gameID, Session session) throws IOException {
		Debugger.debug("leaving gameSocket");
		this.connections.remove(gameID, session);
		ServerMessage notification = new ServerMessage(ServerMessageType.NOTIFICATION);
		Debugger.debug(String.format("There are %d connections in game %d", connections.getConn(gameID).size(), gameID));
		this.connections.broadcast(gameID, session, notification);
	}

	private void resign() {
		Debugger.debug("resigning from game");
	}

}
