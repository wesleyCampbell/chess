package client;

import appstate.*;
import command.*;

import model.AuthData;

public class Client {
	private static final String EXIT_MSG = "Exiting program...";

	private BaseState appState;
	private boolean running;

	private AuthData userData;
	
	public Client() {
		appState = new PreLoginState(this);	
		this.running = true;

		this.userData = null;
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

	public void setUserData(String username, String authToken) {
		this.userData = new AuthData(authToken, username);
	}

	public void clearUserData() {
		this.userData = null;
	}

	public String getUsername() {
		if (this.userData != null) {
			return this.userData.username();
		} 
		return "";
	}
}
