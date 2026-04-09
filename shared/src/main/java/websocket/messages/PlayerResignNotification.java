package websocket.messages;

import com.google.gson.Gson;

public class PlayerResignNotification extends ServerMessage {
	private static final ServerMessageType MSG_TYPE = ServerMessageType.NOTIFICATION;

	private static final String MSG_TEMPLATE = """
		Player %s has resigned!""";

	private String msg;

	public PlayerResignNotification(String username) {
		super(MSG_TYPE);
		this.msg = String.format(MSG_TEMPLATE, username);
	}	

	@Override
	public String toJson() {
		return new Gson().toJson(this);
	}
}
