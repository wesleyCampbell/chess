package websocket.messages.error;

public class NotPlayingError extends Error {
	private static final String MSG = "error: You are not playing";

	public NotPlayingError() {
		super(MSG);
	}
}
