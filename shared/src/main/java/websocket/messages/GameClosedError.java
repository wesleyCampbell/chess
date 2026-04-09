package websocket.messages;

import com.google.gson.Gson;

public class GameClosedError extends Error {
	private static final String MSG = "Error: Game has already ended";

	public GameClosedError() {
		super(MSG);
	}
}
