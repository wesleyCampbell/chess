package command;

import java.util.List;

import appstate.*;

import client.Client;
import client.exception.AuthenticationException;
import client.exception.DataAccessException;

import model.*;

public class LoginCommand extends CommandBase {
	private static final String COMMAND_STR = "login";
	private static final String DESC_STR = """
		Login to your account to access full features.""";
	private static final String PASSWORD_INCORRECT_MSG = """
		Incorrect password. Please try again.""";

	private static final String[] PARAMETERS = {
		"username",
		"password"
	};

	public LoginCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMETERS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (!this.verifyParameters(parameters, PARAMETERS.length)) {
			return false;
		}
		//if (parameters.size() != PARAMETERS.length) {
		//		this.printUsage();
		//		return false;
		//}
		String username = parameters.get(0);
		String password = parameters.get(1);

		System.out.println(String.format("\n\tLogging in as user %s...", username));

		AuthData authData;
		try {
			authData = this.app.getServer().login(username, password);
		} catch (AuthenticationException ex) {
			System.out.println(PASSWORD_INCORRECT_MSG);
			return false;
		} catch (DataAccessException ex) {
			System.out.println(SERVER_ERROR_MSG);
			return false;
		}

		System.out.print("\n\n");

		this.app.setUserData(username, authData.authToken());
		this.app.changeAppState(new LoginState(app));

		return true;
	}
}
