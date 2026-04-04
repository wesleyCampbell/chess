package command;

import java.util.List;

import client.Client;

public class HelpCommand extends CommandBase {
	private static final String COMMAND_STR = "help";
	private static final String DESC_STR = """
		Displays this help message.""";
	private static final String[] PARAMS = {};

	public HelpCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		app.getAppState().printCommandHelp();
		return true;
	}
}

