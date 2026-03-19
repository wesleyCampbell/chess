package appstate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.List;

import client.Client;

import command.*;

public class PreLoginState extends BaseState {
	private static final String WELCOME_MSG = """
		Welcome to the Chess CLI! Please log in or make an account.
		""";

	private static final List<Function<Client, Command>> COMMAND_LIST = List.of(
		LoginCommand::new
	);
	
	public PreLoginState(Client app) {
		super(app, BaseState.initCommands(COMMAND_LIST, app), WELCOME_MSG);
	}
}
