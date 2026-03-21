package command;

import java.util.List;

import client.Client;

public class JoinGameCommand extends CommandBase {
	private static final String COMMAND_STR = "join";
	private static final String DESC_STR = """
		Join a chess game in the game database.""";
	private static final String[] PARAMS = {
		"ID",
		"[WHITE|BLACK]"
	};

	public JoinGameCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (parameters.size() != PARAMS.length) {
			this.printUsage();
			return false;
		}
		
		String gameID = parameters.get(0);
		String teamColor = parameters.get(1);

		System.out.println(String.format(
					"Joining game %s as %s",
					gameID, teamColor));

		return true;
	}
}

