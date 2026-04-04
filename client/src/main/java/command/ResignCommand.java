package command;

import appstate.*;

import client.*;
import client.exception.*;

import java.util.List;

public class ResignCommand extends CommandBase {
	private static final String COMMAND_STR = "resign";
	private static final String DESC_STR = """
		Resign from the game.""";
	private static final String[] PARAMS = {
	};

	public ResignCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		System.out.println("Resigning from the game...");
		return false;
	}
}

