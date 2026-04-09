package websocket.messages.notification;

public class CheckmateNotification extends Notification {
	private static final String MSG_TEMPLATE = """
		Player %s is in checkmate! Game over.""";

	private static String formatMsg(String username) {
		return String.format(MSG_TEMPLATE, username);
	}
	
	public CheckmateNotification(String username) {
		super(formatMsg(username));
	}
}
