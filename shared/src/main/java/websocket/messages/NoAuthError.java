package websocket.messages;

import com.google.gson.Gson;

public class NoAuthError extends ServerMessage {
	private static final String MSG = "Error: Not authorized";

	public NoAuthError() {
		super(MSG);
	}
}
