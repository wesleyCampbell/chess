package websocket.messages.error;

import com.google.gson.Gson;

public class GameClosedError extends Error {
	private static final String MSG = "error: Game has already ended";

	public GameClosedError() {
		super(MSG);
	}
}
