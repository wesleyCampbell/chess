package command;

import client.Client;

public class HelpCommand extends CommandBase {
	private static final String COMMAND_STR = "help";
	private static final String DESC_STR = """
		Displays this help message.""";

	public HelpCommand(Client app) {
		super(COMMAND_STR, DESC_STR, app);
	}

	public boolean executeCommand() {
		app.getAppState().printCommandHelp();
		return true;
	}
}

