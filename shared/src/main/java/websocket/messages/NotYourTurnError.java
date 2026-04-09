package websocket.messages;

public class NotYourTurnError extends Error {
	private static final String MSG = "Error: Not your turn!";

	public NotYourTurnError() {
		super(MSG);
	}
}
