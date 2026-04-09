package command;

import appstate.*;

import client.*;
import client.exception.*;

import java.util.List;
import java.util.Map;

import java.io.IOException;

public class ResignCommand extends CommandBase {
	private static final String COMMAND_STR = "resign";
	private static final String DESC_STR = """
		Resign from the game.""";
	private static final String[] PARAMS = {
	};
	
	private static final String VERIFY_MSG = "\n\tAre you sure you want to resign? [yn]: ";
	private static final String INCORRECT_INPUT_MSG = "\n\tIncorrect input. Please try again.";

	private static final Map<String, Boolean> VERIFY_RESPONSES = Map.of(
		"y", true,
		"n", false,
		"yes", true,
		"no", false,
		"ye", true
			);
	

	public ResignCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (!this.verifyParameters(parameters, PARAMS.length)) {
			return false;
		}

		Boolean resign;
		while (true) {
			System.out.print(VERIFY_MSG);
			// poll user input 
			String resp = this.app.getAppState().getUserInput();
			resign = VERIFY_RESPONSES.get(resp);
			if (resign == null) {
				System.out.println(INCORRECT_INPUT_MSG);
				continue;
			}

			break;
		}

		// If the user does not want to resign, do nothing
		if (!resign) {
			return false;
		}

		// If the user does want to resign, send the request to the websocket.
		try {
			int gameID = Integer.parseInt(this.app.getActiveGame().game().gameID());
			this.app.getWebSocket().resign(this.app.getAuthToken(), gameID);
		} catch (IOException ex) {
			System.out.println(SERVER_ERROR_MSG);
			return false;
		}

		// now switch to an observe game state.
		this.app.changeAppState(new ObserveGameState(this.app, this.app.getActiveGame()));

		return true;
	}
}
