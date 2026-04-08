package websocket.messages;

import com.google.gson.Gson;

public class PlayerResignNotification extends ServerMessage {
	private static final String MSG_TEMPLATE = """
		Player %s has resigned!""";

	private static String formatMsg(String username) {
		return String.format(MSG_TEMPLATE, username);
	}

	public PlayerResignNotification(String username) {
		super(formatMsg(username));
	}	
}
