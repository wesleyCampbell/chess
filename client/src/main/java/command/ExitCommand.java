package command;

import client.Client;

public class ExitCommand extends CommandBase {
	private static final String COMMAND_STR = "exit";
	private static final String DESC_STR = """
		Exit the application.""";

	public ExitCommand(Client app) {
		super(COMMAND_STR, DESC_STR, app);
	}

	public boolean executeCommand() {
		app.exit();
		return true;
	}
}
