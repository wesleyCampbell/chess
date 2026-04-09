package websocket.commands;

public class LeaveCommand extends UserGameCommand {
	private static final CommandType CMD_TYPE = CommandType.LEAVE;

	public LeaveCommand(String authToken, int gameID) {
		super(CMD_TYPE, authToken, gameID);
	}
}
