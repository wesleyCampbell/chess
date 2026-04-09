package websocket.commands;

import com.google.gson.Gson;

public class ConnectCommand extends UserGameCommand {
	private final static CommandType CMD_TYPE = CommandType.CONNECT;

	public ConnectCommand(String authToken, Integer gameID) {
		super(CMD_TYPE, authToken, gameID);
	}
}
