package command;

import client.Client;

public abstract class CommandBase implements Command {
	private static final String USAGE_STR = "\tUsage: %s ";

	protected static final String SERVER_ERROR_MSG = """
		Internal server error. Please try again later.""";

	protected static final String NOT_AUTH_MSG = """
		Not authorized to access this command!""";
	
	protected String commandStr;
	protected Client app;
	protected String description;
	protected String usageStr;
	
	protected CommandBase(String commandStr, String description, String[] parameters, Client app) {
		this.commandStr = commandStr;
		this.description = description;
		this.app = app;

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

		this.usageStr = b.toString();
	}

	public String getCommandStr() {
		return this.commandStr;
	}

	public String getDescription() {
		return this.description;
	}

	public void printUsage() {
		System.out.println(this.usageStr);
	}
}
