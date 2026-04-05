package server.websocket;

import io.javalin.websocket.*;
import util.Debugger;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import chess.ChessGame;
import chess.ChessPiece;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

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
		UserGameCommand cmd = GSON.fromJson(ctx.message(), UserGameCommand.class);
		
		switch (cmd.getCommandType()) {
			case CONNECT -> connect();
			case MAKE_MOVE -> makeMove();
			case LEAVE -> leave();
			case RESIGN -> resign();
		}
	}

	@Override
	public void handleClose(WsCloseContext ctx) {
		Debugger.debug("Websocket closing...");
	}

	private void connect() {
		Debugger.debug("Connecting to gameSocket...");
	}

	private void makeMove() {
		Debugger.debug("Making move");
	}

	private void leave() {
		Debugger.debug("leaving gameSocket");
	}

	private void resign() {
		Debugger.debug("resigning from game");
	}

}
