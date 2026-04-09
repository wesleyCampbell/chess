package websocket.messages;

import com.google.gson.Gson;

public class Notification extends ServerMessage {
	private static final ServerMessageType MSG_TYPE = ServerMessageType.NOTIFICATION;

	private String message;

	public Notification(String message) {
		super(MSG_TYPE);
		this.message = message;
	}

	public String getMsg() {
		return this.message;
	}

	@Override 
	public String toJson() {
		return new Gson().toJson(this);
	}
}
