package command;

import client.Client;

public abstract class CommandBase implements Command {
	private static final String USAGE_STR = "\tUsage: %s ";

	
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
			b.append("<");
			b.append(parameter);
			b.append("> ");
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
