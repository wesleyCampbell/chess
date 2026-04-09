package websocket.messages.error;

import com.google.gson.Gson;

public class GameNoExistError extends Error {
	private static final String MSG = "error: requested game doesn't exist";

	public GameNoExistError() {
		super(MSG);
	}
}
