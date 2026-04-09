package websocket.messages;

import com.google.gson.Gson;

public class Error extends ServerMessage {
	private static final ServerMessageType MSG_TYPE = ServerMessageType.ERROR;

	private String errorMessage;

	public Error(String errorMessage) {
		super(MSG_TYPE);
		this.errorMessage = errorMessage;
	}

	public String getMsg() {
		return this.errorMessage;
	}

	@Override
	public String toJson() {
		return new Gson().toJson(this);
	}
}
