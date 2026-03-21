package command;

import java.util.List;

import client.Client;

public class ObserveGameCommand extends CommandBase {
	private static final String COMMAND_STR = "observe";
	private static final String DESC_STR = """
		Watch a game from the chess game database.""";
	private static final String[] PARAMS = {
		"ID"
	};

	public ObserveGameCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (parameters.size() != PARAMS.length) {
			this.printUsage();
			return false;
		}
		
		String gameID = parameters.get(0);

		System.out.println(String.format(
					"Observing game %s...", gameID));
		return true;
	}
}

