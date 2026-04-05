package websocket.messages;

import com.google.gson.Gson;

public class GameNoExistError extends ServerMessage {
	private static final ServerMessageType MSG_TYPE = ServerMessageType.ERROR;

	private static final String MSG = "Error: requested game doesn't exist";

	private String msg;

	public GameNoExistError() {
		super(MSG_TYPE);
		this.msg = MSG;
	}

	@Override
	public String toJson() {
		return new Gson().toJson(this);
	}
}
