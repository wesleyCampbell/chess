package command;

import java.util.List;

import appstate.*;

import client.Client;

public class LeaveGameCommand extends CommandBase {
	private static final String COMMAND_STR = "leave-game";
	private static final String DESC_STR = """
		Leave the game to return to the main menu.""";

	private static final String[] PARAMETERS = {};

	private static final String LEAVE_MSG = """
		\tLeaving game...\n""";

	private String gameName;

	public LeaveGameCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMETERS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (!this.verifyParameters(parameters, PARAMETERS.length)) {
			return false;
		}

		System.out.println(LEAVE_MSG);

		this.app.resetActiveGame();
		this.app.changeAppState(new LoginState(this.app));

		return true;
	}
}
