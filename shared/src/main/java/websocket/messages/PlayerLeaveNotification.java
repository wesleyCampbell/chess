package websocket.messages;

import com.google.gson.Gson;

public class PlayerLeaveNotification extends Notification {
	private final static String MSG_TEMPLATE = """
		Playser %s left the game.""";

	private static String formatMsg(String username) {
		return String.format(MSG_TEMPLATE, username);
	}

	public PlayerLeaveNotification(String username) {
		super(formatMsg(username));
	}
}
