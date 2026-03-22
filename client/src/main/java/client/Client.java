package client;

import java.util.List;
import java.util.ArrayList;

import appstate.*;
import command.*;

import model.*;

public class Client {
	private static final String EXIT_MSG = "Exiting program...";

	private BaseState appState;
	private boolean running;

	private AuthData userData;

	private ServerFacade server;

	private List<GameData> gamesCache;
	
	public Client() {
		appState = new PreLoginState(this);	
		this.running = true;

		this.userData = null;

		this.server = new ServerFacade();

		this.gamesCache = new ArrayList<>();
	}

	public void run() {
		appState.clearScreen();
		appState.displayWelcomeScreen();
		while (this.running) {
			appState.commandPrompt();
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

	public String getAuthToken() {
		if (this.userData != null) {
			return this.userData.authToken();
		} 
		return "";
	}

	public ServerFacade getServer() {
		return this.server;
	}

	public List<GameData> getGamesCache() {
		return this.gamesCache;
	}

	public void updateGamesCache(List<GameData> newCache) {
		this.gamesCache = newCache;
	}
}
