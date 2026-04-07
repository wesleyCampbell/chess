package websocket.messages;

import chess.ChessMove;

import com.google.gson.Gson;

public class InvalidMoveError extends ServerMessage {
	private final static ServerMessageType MSG_TYPE = ServerMessageType.NOTIFICATION;

	private final static String MSG = "Error: Invalid move!";

	private String msg;

	public InvalidMoveError() {
		super(MSG_TYPE);
		this.msg = MSG;
	}

	@Override
	public String toJson() {
		return new Gson().toJson(this);
	}
}
