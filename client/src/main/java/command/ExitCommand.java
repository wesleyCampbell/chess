package command;

import java.util.List;

import client.Client;

public class ExitCommand extends CommandBase {
	private static final String COMMAND_STR = "exit";
	private static final String DESC_STR = """
		Exit the application.""";
	private static final String[] PARAMS = {};

	public ExitCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (parameters.size() != PARAMS.length) {
			this.printUsage();
			return false;
		}
		app.exit();
		return true;
	}
}
