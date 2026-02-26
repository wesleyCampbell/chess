package server;

import io.javalin.*;

import dataAccess.*;
import dataAccess.memoryDAO.*;
import handler.*;

public class Server {

    private final Javalin javalin;

	//
	// ================= DATABASE ACCESS INTERFACES ============ 
	//
	
	private final AuthDAO authDAO;
	private final GameDAO gameDAO;
	private final UserDAO userDAO;

	//
	// =================== HTTP REQUEST HANDLERS ====================
	//
	
	private final DatabaseHandler databaseHandler;
	private final GamesHandler gamesHandler;
	private final LoginCtlHandler loginCtlHandler;
	private final UserAccountHandler accountHandler;

	//
	// ======================== CONSTRUCTORS ========================
	//
	

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

		// Universal data access interfaces
		this.authDAO = new MemoryAuthDAO();
		this.gameDAO = new MemoryGameDAO();
		this.userDAO = new MemoryUserDAO();

		// Request handler initialization
		this.databaseHandler = new DatabaseHandler(authDAO, gameDAO, userDAO);
		this.gamesHandler = new GamesHandler(authDAO, gameDAO);
		this.loginCtlHandler = new LoginCtlHandler(authDAO, userDAO);
		this.accountHandler = new UserAccountHandler(this.authDAO, this.userDAO);
		
		// POST endpoints
		javalin.post("/user", this.accountHandler::registerRequest);
		javalin.post("/session", this.loginCtlHandler::loginRequest);
		// javalin.post("/game", this.gamesHandler::createGameRequest);

		// GET endpoints
		// javalin.get("/game", this.gamesHandler::listGameRequest);
		
		// DELETE endpoints
		// javalin.delete("/session", this.loginCtlHandler::logoutRequest);
		// javalin.delete("/db", this.databaseHandler::clearDataRequest);

		// PUT endpoints
		// javalin.put("/game", this.gamesHandler::joinGameRequest);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
