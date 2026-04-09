package websocket.messages;

import com.google.gson.Gson;

public class Error extends ServerMessage {
	private static final ServerMessageType MSG_TYPE = ServerMessageType.ERROR;

	private String msg;

	public Error(String msg) {
		super(MSG_TYPE);
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}

	@Override
	public String toJson() {
		return new Gson().toJson(this);
	}
}
