package websocket.messages.notification;

public class StalemateNotification extends Notification {
	private static final String MSG_TEMPLATE = """
		Player %s is in stalemate! Game over.""";

	private static String formatMsg(String username) {
		return String.format(MSG_TEMPLATE, username);
	}

	public StalemateNotification(String username) {
		super(formatMsg(username));
	}
}
