package command;

import client.Client;

public class LoginCommand extends CommandBase {
	private static final String COMMAND_STR = "login";

	public LoginCommand(Client app) {
		super(COMMAND_STR, app);
	}

	public String getCommandStr() {
		return this.commandStr;
	}

	public boolean executeCommand() {
		return false;
	}
}
