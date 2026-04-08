package websocket.messages;

import com.google.gson.Gson;

public class PlayerJoinNotification extends Notification {
	private static final String MSG_TEMPLATE = """
		Player %s joined as %s""";

	private static String formatMsg(String user, String teamColor) {
		return String.format(MSG_TEMPLATE, user, teamColor);
	}

	public PlayerJoinNotification(String user, String teamColor) {
		super(formatMsg(user, teamColor));
	}
}
