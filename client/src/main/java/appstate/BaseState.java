package appstate;

import client.ClientMain;

public abstract class BaseState implements AppState {
	private ClientMain app;

	protected BaseState(ClientMain app) {
		this.app = app;
	}
}
