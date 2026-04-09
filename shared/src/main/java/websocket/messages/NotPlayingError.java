package websocket.messages;

public class NotPlayingError extends Error {
	private static final String MSG = "Error: You are not playing";

	public NotPlayingError() {
		super(MSG);
	}
}
