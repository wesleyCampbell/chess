package command;

import java.util.List;

import client.Client;

import static ui.EscapeSequences.*;

public abstract class CommandBase implements Command {
	private static final String USAGE_STR = new StringBuilder()
		.append("\tUsage: ")
		.append(SET_TEXT_COLOR_WHITE)
		.append("%s ")
		.append(RESET_TEXT_COLOR)
		.toString();

	protected static final String SERVER_ERROR_MSG = """
		\tInternal server error. Please try again later.\n""";

	protected static final String NOT_AUTH_MSG = """
		\tNot authorized to access this command!\n""";
	
	protected String commandStr;
	protected Client app;
	protected String description;
	protected String usageStr;
	
	protected CommandBase(String commandStr, String description, String[] parameters, Client app) {
		this.commandStr = commandStr;
		this.description = description;
		this.app = app;


		this.usageStr = this.formatUsageStr(parameters);
	}

	private String formatUsageStr(String[] parameters) {
		StringBuilder b = new StringBuilder();
		b.append(String.format(USAGE_STR, commandStr));
		for (String parameter : parameters) {
			char[] delimeter = new char[2];
			if (parameter.charAt(0) == '[') {
				delimeter[0] = '[';
				delimeter[1] = ']';
			} else {
				delimeter[0] = '<';
				delimeter[1] = '>';
			}

			b.append(delimeter[0]);
			b.append(parameter);
			b.append(delimeter[1]);
			b.append(" ");
		}

		return b.toString();
	}

	public String getCommandStr() {
		return this.commandStr;
	}

	public String getDescription() {
		return this.description;
	}

	public void printUsage() {
		System.out.println("");
		System.out.println(this.usageStr);
		System.out.println("");
	}

	protected boolean verifyParameters(List<String> parameters, int paramNum) {
		if (parameters.size() != paramNum) {
			this.printUsage();
			return false;
		} 
		return true;
	}
}
