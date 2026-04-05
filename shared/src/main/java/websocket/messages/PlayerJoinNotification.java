package websocket.messages;

import com.google.gson.Gson;

public class PlayerJoinNotification extends ServerMessage {
	private static final String MSG_TEMPLATE = """
		Player %s joined as %s""";

	private String msg;

	public PlayerJoinNotification(String user, String teamColor) {
		super(ServerMessageType.NOTIFICATION);
		this.msg = String.format(MSG_TEMPLATE, user, teamColor);
	}

	@Override
	public String toJson() {
		return new Gson().toJson(this);
	}
}
