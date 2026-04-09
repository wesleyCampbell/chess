package websocket.commands;

import com.google.gson.Gson;

public class ResignCommand extends UserGameCommand {
	private final static CommandType CMD_TYPE = CommandType.RESIGN;

	public ResignCommand(String authToken, Integer gameID) {
		super(CMD_TYPE, authToken, gameID);
	}
}
