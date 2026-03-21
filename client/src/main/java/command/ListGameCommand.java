package command;

import java.util.List;

import client.Client;

public class ListGameCommand extends CommandBase {
	private static final String COMMAND_STR = "list-games";
	private static final String DESC_STR = """
		List all games in the database.""";
	private static final String[] PARAMS = {};

	public ListGameCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (parameters.size() != PARAMS.length) {
			this.printUsage();
			return false;
		}
		
		System.out.println("Listing all games");

		return true;
	}
}

