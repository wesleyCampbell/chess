package appstate;

import java.util.HashMap;

import command.*;

import client.Client;

public class LoginState extends BaseState {
	public LoginState(Client app) {
		super(app, new HashMap<String, Command>(), "");
	}
}
