package command;

import appstate.*;

import client.*;
import client.exception.*;

import java.util.List;

public class MakeMoveCommand extends CommandBase {
	private static final String COMMAND_STR = "make-move";
	private static final String DESC_STR = """
		Make a chess move.""";
	private static final String[] PARAMS = {
		"move",
	};

	public MakeMoveCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		System.out.println("Making move");
		return false;
	}
}
