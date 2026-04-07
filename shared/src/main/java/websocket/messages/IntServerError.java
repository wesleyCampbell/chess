package websocket.messages;

import com.google.gson.Gson;

public class IntServerError extends ServerMessage {
	private static final ServerMessageType MSG_TYPE = ServerMessageType.ERROR;
	private static final String MSG = "Error: Internal server error.";

	private String msg;

	public IntServerError() {
		super(MSG_TYPE);
		this.msg = MSG;
	}

	@Override
	public String toJson() {
		return new Gson().toJson(this);
	}
}
