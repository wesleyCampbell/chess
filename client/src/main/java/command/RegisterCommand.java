package command;

import appstate.*;

import java.util.List;

import client.Client;
import util.Debugger;

import client.exception.*;

import model.*;

public class RegisterCommand extends CommandBase {
	private static final String COMMAND_STR = "register";
	private static final String DESC_STR = """
		Make a new account.""";
	private static final String[] PARAMS = {
		"username",
		"password",
		"email"
	};


	public RegisterCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (parameters.size() != PARAMS.length) {
			this.printUsage();
			return false;
		}

		String username = parameters.get(0);
		String password = parameters.get(1);
		String email = parameters.get(2);

		AuthData authData;
		try {
			authData = this.app.getServer().register(username, password, email);
		} catch(ConnectionException ex) {
			System.out.println("\n\tServer is offline... please try again later.\n");
			return false;
		} catch(AlreadyTakenException ex) {
			System.out.println("\n\tUsername is already taken.... please try a new one.\n");
			return false;
		} catch (DataAccessException ex) {
			System.out.println("\n\tInternal Server error... Please try again later.\n");
			return false;
		}

		this.app.setUserData(authData.username(), authData.authToken());
		this.app.changeAppState(new LoginState(app));

		return true;
	}
}
