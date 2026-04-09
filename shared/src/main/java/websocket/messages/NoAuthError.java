package websocket.messages;

import com.google.gson.Gson;

public class NoAuthError extends Error {
	private static final String MSG = "Error: Not authorized";

	public NoAuthError() {
		super(MSG);
	}
}
