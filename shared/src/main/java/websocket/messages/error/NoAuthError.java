package websocket.messages.error;

import com.google.gson.Gson;

public class NoAuthError extends Error {
	private static final String MSG = "error: Not authorized";

	public NoAuthError() {
		super(MSG);
	}
}
