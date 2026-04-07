package websocket.messages;

import chess.*;

import com.google.gson.Gson;

public class RedrawBoardMessage extends ServerMessage {
	private static final ServerMessageType MSG_TYPE = ServerMessageType.LOAD_GAME;
	
	public RedrawBoardMessage() {
		super(MSG_TYPE);
	}
}

