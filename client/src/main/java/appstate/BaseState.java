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

	protected static final String PROMPT_BG_COLOR = SET_BG_COLOR_DARK_GREY;

	protected static final String PROMPT_MSG = new StringBuilder()
		.append(PROMPT_BG_COLOR)
		.append("%s")
		.append("[%s]")
		.append(RESET_BG_COLOR)
		.append(RESET_TEXT_COLOR)
		.append(" >>> ")
		.toString();


	protected static final String HELP_PROMPT = new StringBuilder()
		.append("Enter `")
		.append(SET_TEXT_COLOR_WHITE)
		.append("help")
		.append(RESET_TEXT_COLOR)
		.append("` for a list of available commands. ")
		.toString();

	protected static final String INVALID_CMD_MSG = new StringBuilder()
		.append("\n\tInvalid command `")
		.append(SET_TEXT_COLOR_WHITE)
		.append("%s")
		.append(RESET_TEXT_COLOR)
		.append("`. ")
		.append(HELP_PROMPT)
		.append("\n")
		.toString();

	protected static final String ERROR_MSG = "Command failed!";

	protected static final String WELCOME_BORDER = "=-".repeat(40);

	protected Client app;
	protected HashMap<String, Command> commands;
	protected final String welcomeMsg;
	protected final String promptMsg;

	protected BaseState(Client app, HashMap<String, Command> commands, String welcomeMsg, String promptHeader, String promptColor) {
		this.app = app;
		this.commands = commands;
		this.welcomeMsg = welcomeMsg;
		this.promptMsg = String.format(PROMPT_MSG, promptColor, promptHeader);
	}

	public void clearScreen() {
		System.out.print(ERASE_SCREEN);
		System.out.flush();
	}

	public void displayWelcomeScreen() {
		System.out.println(WELCOME_BORDER);
		System.out.println("\n\t" + this.welcomeMsg + "\n");
		System.out.println(WELCOME_BORDER + "\n");
	}

	public void printCommandHelp() {
		System.out.println("");
		System.out.println(HELP_MSG);
		for (String val : this.commands.keySet()) {
			Command cmd = this.commands.get(val);
			
			System.out.println(String.format("%s    %s:%s %s",
						SET_TEXT_COLOR_WHITE, cmd.getCommandStr(), 
						RESET_TEXT_COLOR, cmd.getDescription()));
		}
		System.out.println("");
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
		System.out.print(this.promptMsg);
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
