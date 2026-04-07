package websocket.messages;

import com.google.gson.Gson;

public class NoAuthError extends ServerMessage {
	private static final ServerMessageType MSG_TYPE = ServerMessageType.ERROR;

	private static final String MSG = "Error: Not authorized";

	private String msg;

	public NoAuthError() {
		super(MSG_TYPE);
		this.msg = MSG;
	}

	@Override
	public String toJson() {
		return new Gson().toJson(this);
	}
}
