package appstate;

import client.Client;

public abstract class BaseState implements AppState {
	private Client app;

	protected BaseState(Client app) {
		this.app = app;
	}
}
