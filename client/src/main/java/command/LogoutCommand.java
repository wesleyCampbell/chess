package command;

import java.util.List;

import appstate.PreLoginState;
import client.Client;
import client.exception.AuthenticationException;
import client.exception.DataAccessException;

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

		try {
			this.app.getServer().logout(this.app.getAuthToken());
		} catch (AuthenticationException ex) {
			System.out.println(NOT_AUTH_MSG);
			return false;
		} catch (DataAccessException ex) {
			System.out.println(SERVER_ERROR_MSG);
			return false;
		}

		this.app.clearUserData();
		this.app.changeAppState(new PreLoginState(app));

		return true;
	}
}
