package command;

import client.Client;

public abstract class CommandBase implements Command {
	protected String commandStr;
	protected Client app;
	
	protected CommandBase(String commandStr, Client app) {
		this.commandStr = commandStr;
		this.app = app;
	}
}
