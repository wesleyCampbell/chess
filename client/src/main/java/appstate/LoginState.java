package appstate;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import command.*;

import static ui.EscapeSequences.*;

import client.Client;

public class LoginState extends BaseState {
	private static final String WELCOME_MSG = """
		Welcome back, %s.\s""" + HELP_PROMPT;
	private static final String PROMPT_HEADER = "LOGGED IN";
	private static final String PROMPT_COLOR = SET_TEXT_COLOR_BLUE;

	private static final List<Function<Client, Command>> COMMAND_LIST = List.of(
		HelpCommand::new,
		LogoutCommand::new,
		CreateGameCommand::new,
		ListGameCommand::new,
		JoinGameCommand::new,
		ObserveGameCommand::new
	);

	private static String generateWelcomeMsg(Client app) {
		String outStr = String.format(WELCOME_MSG, app.getUsername());
		return outStr;
	}

	public LoginState(Client app) {
		super(app, BaseState.initCommands(COMMAND_LIST, app), generateWelcomeMsg(app), PROMPT_HEADER, PROMPT_COLOR);
	}
}
