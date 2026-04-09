package websocket.messages.notification;

public class CheckNotification extends Notification {
	private static final String MSG_TEMPLATE = """
		Player %s is in check!""";

	private static String formatMsg(String username) {
		return String.format(MSG_TEMPLATE, username);
	}

	public CheckNotification(String username) {
		super(formatMsg(username));
	} 
}
