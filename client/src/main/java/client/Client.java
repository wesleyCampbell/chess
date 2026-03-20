package client;

import appstate.*;
import command.*;

public class Client {
	private static final String EXIT_MSG = "Exiting program...";

	private BaseState appState;
	private boolean running;
	
	public Client() {
		appState = new PreLoginState(this);	
		this.running = true;
	}

	public void run() {
		appState.displayWelcomeScreen();
		String input = "";
		while (this.running) {
			input = appState.commandPrompt();
		}

		System.out.println(EXIT_MSG);
	}

	public void exit() {
		running = false;
	}

	public BaseState getAppState() {
		return this.appState;
	}
}
