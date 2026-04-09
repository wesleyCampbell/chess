package command;

import static ui.EscapeSequences.*;

import java.util.List;

import client.Client;
import client.exception.AuthenticationException;
import client.exception.DataAccessException;

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

	private String formatNameDisplay(String gameName) {
		StringBuilder b = new StringBuilder();
		b.append(SET_TEXT_COLOR_YELLOW)
			.append(gameName)
			.append(RESET_TEXT_COLOR);

		return b.toString();
	}

	public boolean executeCommand(List<String> parameters) {
		if (parameters.size() != PARAMS.length) {
			this.printUsage();
			return false;
		}

		String gameName = parameters.get(0);
		String gameNameDisplay = this.formatNameDisplay(gameName);

		System.out.println(String.format("\n\tCreating game %s...\n", gameNameDisplay));

		String gameID;
		try {
			gameID = this.app.getServer().createGame(this.app.getAuthToken(), gameName);
		} catch (AuthenticationException ex) {
			System.out.println(NOT_AUTH_MSG);
			return false;
		} catch (DataAccessException ex) {
			System.out.println(SERVER_ERROR_MSG);
			return false;
		}

		try {
			this.app.generateGamesCache();
		} catch (Exception ex) {}

		System.out.println(String.format("\tGame %s successfully created!\n", gameNameDisplay));

		return true;
	}
}

