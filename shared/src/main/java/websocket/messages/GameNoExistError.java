package websocket.messages;

import com.google.gson.Gson;

public class GameNoExistError extends Error {
	private static final String MSG = "Error: requested game doesn't exist";

	public GameNoExistError() {
		super(MSG);
	}
}
