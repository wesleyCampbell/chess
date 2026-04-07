package websocket.messages;

import chess.*;

import com.google.gson.Gson;

public class PlayerMoveNotification extends ServerMessage {
	private static final ServerMessageType MSG_TYPE = ServerMessageType.NOTIFICATION;
	private static final String MSG_TEMPLATE = """
		Player %s played %s.""";
	
	private String msg;

	public PlayerMoveNotification(String user, ChessMove move) {
		super(MSG_TYPE);
		this.msg = String.format(MSG_TEMPLATE, user, move.toString());
	}

	@Override
	public String toJson() {
		return new Gson().toJson(this);
	}
}
