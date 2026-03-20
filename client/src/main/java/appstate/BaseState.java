package appstate;

import java.util.HashMap;
import java.util.List;
import java.util.Collection;
import java.util.function.Function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Console;

import command.*;

import static ui.EscapeSequences.*;

import client.Client;

public abstract class BaseState implements AppState {
	private static final String HELP_MSG = """
		List of available commands: """;

	private static final String PROMPT_MSG = """
		Enter Command Here:\s""";

	private static final String HELP_PROMPT = """
		Enter `help` for list of available commands. """;

	private static final String INVALID_CMD_MSG = """
		Invalid command `%s`. """ + HELP_PROMPT;

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
	}

	public void printCommandHelp() {
		System.out.println(HELP_MSG);
		for (String val : this.commands.keySet()) {
			Command cmd = this.commands.get(val);
			
			System.out.println(String.format("    %s: %s", cmd.getCommandStr(), cmd.getDescription()));
		}
	}

	protected static HashMap<String, Command> initCommands(List<Function<Client, Command>> cmdList, Client app) {
		HashMap<String, Command> commands = new HashMap<>();
		for (Function<Client, Command> func : cmdList) {
			Command cmd = func.apply(app);
			if (commands.get(cmd.getCommandStr()) == null) {
				commands.put(cmd.getCommandStr(), cmd);
			} else {
				throw new RuntimeException("No two commands can share same command");
			}
		}

		return commands;
	}

	public String commandPrompt() {
		System.out.print(PROMPT_MSG);
		String cmd = this.getUserInput();

		// If the command doesn't exist, display a helper message
		Command command = this.commands.get(cmd);
		if (command == null) {
			System.out.println(String.format(INVALID_CMD_MSG, cmd));
		} else {
			command.executeCommand();
		}

		return cmd;
	}

	/**
	 * Reads user input from the console.
	 *
	 * @return The user input
	 */
	public String getUserInput() {
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

		String input;
		try {
			input = r.readLine();
		} catch (IOException ex) {
			return "";
		}

		return input;
	}

	/**
	 * Reads a password from the console, hiding the input
	 *
	 * @return The password
	 */
	public String getPassword() {
		Console console = System.console();

		if (console == null) {
			throw new RuntimeException("No console available!");
		}

		char[] password = console.readPassword();
		return new String(password);
	}
}
