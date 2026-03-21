package command;

import java.util.List;

import appstate.PreLoginState;
import client.Client;

public class LogoutCommand extends CommandBase {
	private static final String COMMAND_STR = "logout";
	private static final String DESC_STR = """
		Logout from your user session.""";
	private static final String[] PARAMS = {};

	public LogoutCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (parameters.size() != PARAMS.length) {
			this.printUsage();
			return false;
		}

		System.out.println("Logging out...");

		this.app.clearUserData();
		this.app.changeAppState(new PreLoginState(app));

		return true;
	}
}
