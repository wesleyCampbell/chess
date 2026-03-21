package command;

import java.util.List;

import appstate.*;

import client.Client;

public class LoginCommand extends CommandBase {
	private static final String COMMAND_STR = "login";
	private static final String DESC_STR = """
		Login to your account to access full features.""";
	private static final String[] PARAMETERS = {
		"username",
		"password"
	};

	public LoginCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMETERS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (parameters.size() != PARAMETERS.length) {
				this.printUsage();
				return false;
		}
		String username = parameters.get(0);
		String password = parameters.get(1);

		System.out.println(String.format("Logging in as %s with password %s", username, password));

		this.app.setUserData(username, "THIS IS A VALID AUTH TOKEN HEHE");
		this.app.changeAppState(new LoginState(this.app));

		return true;
	}
}
