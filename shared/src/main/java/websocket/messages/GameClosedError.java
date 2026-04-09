package websocket.messages;

import com.google.gson.Gson;

public class GameClosedError extends ServerMessage {
	private static final ServerMessageType MSG_TYPE = ServerMessageType.ERROR;

	private static final String MSG = "Error: Game has already ended";

	private String msg;

	public GameClosedError() {
		super(MSG_TYPE);
		this.msg = MSG;
	}

	@Override 
	public String toJson() {
		return new Gson().toJson(this);
	}
}
