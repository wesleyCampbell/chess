package command;

import java.util.List;

import appstate.*;

import client.Client;
import client.Client.ActiveGame;

public class RedrawChessBoardCommand extends CommandBase {
	private static final String COMMAND_STR = "redraw";
	private static final String DESC_STR = """
		Rerenders the chess board and displays it on the screen.""";

	private static final String[] PARAMETERS = {};

	public RedrawChessBoardCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMETERS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (!this.verifyParameters(parameters, PARAMETERS.length)) {
			return false;
		}

		this.app.printActiveGame(false);

		return false;
	}
}
