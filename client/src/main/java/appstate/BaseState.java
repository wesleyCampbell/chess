package appstate;

import java.util.HashMap;
import java.util.List;
import java.util.Collection;
import java.util.function.Function;

import command.*;

import static ui.EscapeSequences.*;

import client.Client;

public abstract class BaseState implements AppState {
	private static final String HELP_MSG = """
		List of available commands: 
		""";

	private static final String PROMPT_MSG = """
		Enter Command Here: 
		""";

	private static final String HELP_PROMPT = """
		Enter `help` for list of available commands.
		""";

	protected Client app;
	protected HashMap<String, Command> commands;
	protected final String welcome_msg;

	protected BaseState(Client app, HashMap<String, Command> commands, String welcome_msg) {
		this.app = app;
		this.commands = commands;
		this.welcome_msg = welcome_msg;
	}

	public void clearScreen() {
		System.out.print(ERASE_SCREEN);
		System.out.flush();
	}

	public void displayWelcomeScreen() {
		this.clearScreen();
		System.out.println(this.welcome_msg);
		System.out.println(HELP_PROMPT);
		System.out.println(PROMPT_MSG + SET_TEXT_BLINKING + " " + RESET_TEXT_BLINKING);
	}

	public void printCommandHelp() {
		System.out.println(HELP_MSG);
		for (String val : this.commands.keySet()) {
			Command cmd = this.commands.get(val);
			
			System.out.print(String.format("    %s: %s", cmd.getCommandStr(), cmd.getDescription()));
		}
	}

	protected static HashMap<String, Command> initCommands(List<Function<Client, Command>> cmdList, Client app) {
		HashMap<String, Command> commands = new HashMap<>();
		for (Function<Client, Command> func : cmdList) {
			Command cmd = func.apply(app);
			commands.put(cmd.getCommandStr(), cmd);
		}

		return commands;
	}
}
