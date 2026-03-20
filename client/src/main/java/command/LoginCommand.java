package command;

import appstate.*;

import client.Client;

public class LoginCommand extends CommandBase {
	private static final String COMMAND_STR = "login";
	private static final String DESC_STR = """
		Login to your account to access full features.""";

	private static final String USERNAME_PROMPT = "\tUsername:\s";
	private static final String PASSWORD_PROMPT = "\tPassword:\s";

	public LoginCommand(Client app) {
		super(COMMAND_STR, DESC_STR, app);
	}

	public boolean executeCommand() {
		System.out.print(USERNAME_PROMPT);
		String username = this.app.getAppState().getUserInput();
		System.out.print(PASSWORD_PROMPT);
		String password = this.app.getAppState().getPassword();

		System.out.println(String.format("Logging in as %s with password %s", username, password));

		this.app.setUsername(username);
		this.app.changeAppState(new LoginState(this.app));

		return true;
	}
}
