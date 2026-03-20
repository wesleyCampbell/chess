package client;

import appstate.*;
import command.*;

public class Client {
	private static final String EXIT_MSG = "Exiting program...";

	private BaseState appState;
	private boolean running;
	private String username;
	
	public Client() {
		appState = new PreLoginState(this);	
		this.running = true;
		this.username = null;
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

	public void changeAppState(BaseState appState) {
		this.appState = appState;
		appState.displayWelcomeScreen();
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return this.username;
	}
}
