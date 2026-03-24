package client;

import chess.*;

public class ClientMain {
	private static final String SERVER_DOMAIN = "localhost";
	private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
		Client app = new Client(SERVER_DOMAIN, SERVER_PORT);

		app.run();
    }
}
