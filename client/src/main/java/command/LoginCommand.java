package command;

import client.Client;

public class LoginCommand extends CommandBase {
	private static final String COMMAND_STR = "login";
	private static final String DESC_STR = """
		Login to your account to access full features
		""";


	public LoginCommand(Client app) {
		super(COMMAND_STR, DESC_STR, app);
	}

	public String getCommandStr() {
		return this.commandStr;
	}

	public String getDescription() {
		return this.description;
	}

	public boolean executeCommand() {
		return false;
	}
}
