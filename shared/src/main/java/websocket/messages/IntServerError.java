package websocket.messages;

import com.google.gson.Gson;

public class IntServerError extends ServerMessage {
	private static final String MSG = "Error: Internal server error.";

	public IntServerError() {
		super(MSG);
	}
}
