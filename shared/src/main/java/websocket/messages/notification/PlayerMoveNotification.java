package websocket.messages.notification;

import chess.*;

import com.google.gson.Gson;

public class PlayerMoveNotification extends Notification {
	private static final String MSG_TEMPLATE = """
		Player %s played %s.""";

	private static String formatMsg(String user, ChessMove move) {
		return String.format(MSG_TEMPLATE, user, move.toString());
	}

	public PlayerMoveNotification(String user, ChessMove move) {
		super(formatMsg(user, move));
	}
}
