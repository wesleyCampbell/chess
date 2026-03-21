package appstate;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
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
	protected static final String HELP_MSG = """
		List of available commands: """;

	protected static final String PROMPT_MSG = "[%s] >>> ";

	protected static final String HELP_PROMPT = """
		Enter `help` for list of available commands. """;

	protected static final String INVALID_CMD_MSG = """
		\tInvalid command `%s`.\s""" + HELP_PROMPT;

	protected static final String ERROR_MSG = "Command failed!";

	protected Client app;
	protected HashMap<String, Command> commands;
	protected final String welcome_msg;
	protected final String prompt_msg;

	protected BaseState(Client app, HashMap<String, Command> commands, String welcome_msg, String prompt_header) {
		this.app = app;
		this.commands = commands;
		this.welcome_msg = welcome_msg;
		this.prompt_msg = String.format(PROMPT_MSG, prompt_header);
	}

	public void clearScreen() {
		System.out.print(ERASE_SCREEN);
		System.out.flush();
	}

	public void displayWelcomeScreen() {
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
			String cmdStr = cmd.getCommandStr();
			if (commands.get(cmdStr) == null) {
				commands.put(cmdStr, cmd);
			} else {
				String msg = String.format("No two commands can share the same command: `%s`", cmdStr);
				throw new RuntimeException(msg);
			}
		}

		return commands;
	}

	public List<String> getUserCommand() {
		List<String> cmd = new ArrayList<>();

		String input = this.getUserInput();

		for (String value : input.trim().split("\\s+")) {
			cmd.add(value);
		}

		return cmd;
	}

	public String commandPrompt() {
		System.out.print(this.prompt_msg);
		// String cmd = this.getUserInput();
		List<String> cmd = this.getUserCommand();

		// If the command doesn't exist, display a helper message
		Command command = this.commands.get(cmd.get(0));
		if (command == null) {
			System.out.println(String.format(INVALID_CMD_MSG, cmd.get(0)));
		} else {
			boolean success = command.executeCommand(cmd.subList(1, cmd.size()));
			if (!success) {
			}	
		}

		return cmd.toString();
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
