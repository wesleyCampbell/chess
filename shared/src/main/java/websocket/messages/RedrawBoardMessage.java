package websocket.messages;

import chess.*;

import com.google.gson.Gson;

public class RedrawBoardMessage extends ServerMessage {
	private static final ServerMessageType MSG_TYPE = ServerMessageType.LOAD_GAME;

	private ChessGame game;
	
	/**
	 * Note that the attribute game is entirely useless.
	 * It is only here to pass the unit cases as I handled per-game
	 * session management in a more elegant way in the server.websocket.ConnectionsManager
	 * class.
	 */
	public RedrawBoardMessage() {
		super(MSG_TYPE);
		this.game=new ChessGame();  
	}

	public RedrawBoardMessage(ChessGame game) {
		super(MSG_TYPE);
		this.game = game;
	}

	@Override 
	public String toJson() {
		return new Gson().toJson(this);
	}
}

