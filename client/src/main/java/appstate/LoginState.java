package appstate;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import command.*;

import client.Client;

public class LoginState extends BaseState {
	private static final String WELCOME_MSG = """
		Welcome back, %s.\s""" + HELP_PROMPT;

	private static final List<Function<Client, Command>> COMMAND_LIST = List.of(
		HelpCommand::new,
		ExitCommand::new
	);

	private static String generateWelcomeMsg(Client app) {
		String outStr = String.format(WELCOME_MSG, app.getUsername());
		return outStr;
	}

	public LoginState(Client app) {
		super(app, BaseState.initCommands(COMMAND_LIST, app), generateWelcomeMsg(app));
	}
}
