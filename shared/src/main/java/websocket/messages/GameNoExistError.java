package websocket.messages;

import com.google.gson.Gson;

public class GameNoExistError extends ServerMessage {
	private static final String MSG = "Error: requested game doesn't exist";

	public GameNoExistError() {
		super(MSG);
	}
}
