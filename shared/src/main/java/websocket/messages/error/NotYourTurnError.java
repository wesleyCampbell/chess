package websocket.messages.error;

public class NotYourTurnError extends Error {
	private static final String MSG = "error: Not your turn!";

	public NotYourTurnError() {
		super(MSG);
	}
}
