package command;

import appstate.*;

import client.*;
import client.exception.*;

import java.util.List;

import java.io.IOException;

public class MakeMoveCommand extends CommandBase {
	private static final String COMMAND_STR = "make-move";
	private static final String DESC_STR = """
		Make a chess move.""";
	private static final String[] PARAMS = {
		"move",
	};

	public MakeMoveCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		System.out.println("Making move");

		// Verify that the correct parameters have been passed in
		if (!this.verifyParameters(parameters, PARAMS.length) {
			return false;
		}

		String moveStr = parameters.get(0);

		ChessMove move = this.parseMove(moveStr);

		// Try to send the move over the web socket
		try {
			int gameID = Integer.parseInt(this.app.getActiveGame().game().gameID());
			this.app.getWebSocket().makeMove(this.app.getAuthToken(), gameID, move);
		} catch (IOException ex) {
			System.out.println(SERVER_ERROR_MSG);
			return false;
		}

		// Draw the new chess board

		this.app.printActiveGame();

		return true;
	}
}
