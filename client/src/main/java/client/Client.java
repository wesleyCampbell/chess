package client;

import appstate.*;
import command.*;

public class Client {
	private BaseState appState;
	public Client() {
		appState = new PreLoginState(this);	
	}

	public void run() {
		appState.displayWelcomeScreen();
		appState.printCommandHelp();
	}
}
