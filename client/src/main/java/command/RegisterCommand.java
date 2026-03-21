package command;

import appstate.*;

import java.util.List;

import client.Client;

public class RegisterCommand extends CommandBase {
	private static final String COMMAND_STR = "register";
	private static final String DESC_STR = """
		Make a new account.""";
	private static final String[] PARAMS = {
		"username",
		"password",
		"email"
	};


	public RegisterCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (parameters.size() != PARAMS.length) {
			this.printUsage();
			return false;
		}

		String username = parameters.get(0);
		String password = parameters.get(1);
		String email = parameters.get(2);

		System.out.println(String.format(
					"Registering %s with password: %s, email %s",
					username, password, email));

		return true;
	}
}
