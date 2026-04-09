package websocket.messages.error;

import com.google.gson.Gson;

public class IntServerError extends Error {
	private static final String MSG = "error: Internal server error.";

	public IntServerError() {
		super(MSG);
	}
}
