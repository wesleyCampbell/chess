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

		System.out.println("Redrawing the chess board, baby");

		ActiveGame game = this.app.getActiveGame();

		if (game == null) {
			System.out.println("Game null. ");
			return false;
		}

		// TODO: This probably needs to dynamically refresh. I don't yet
		// know how the web socket will change this, though

		this.app.printBoard(game);	

		return false;
	}
}
