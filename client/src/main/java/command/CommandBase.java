package command;

import client.Client;

public abstract class CommandBase implements Command {
	protected String commandStr;
	protected Client app;
	protected String description;
	
	protected CommandBase(String commandStr, String description, Client app) {
		this.commandStr = commandStr;
		this.description = description;
		this.app = app;
	}
}
