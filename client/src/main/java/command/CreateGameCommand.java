package command;

import java.util.List;

import client.Client;

public class CreateGameCommand extends CommandBase {
	private static final String COMMAND_STR = "create";
	private static final String DESC_STR = """
		Creates a new chess game.""";
	private static final String[] PARAMS = {
		"name"
	};

	public CreateGameCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (parameters.size() != PARAMS.length) {
			this.printUsage();
			return false;
		}

		String gameName = parameters.get(0);

		System.out.println(String.format("Creating game %s...", gameName));

		return true;
	}
}

