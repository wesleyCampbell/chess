package dataaccess;

import chess.*;
import dataaccess.sqldao.*;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.UUID;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class DatabaseDAoTests {
	protected static GameData gameData;

	protected static String username1;
	protected static String password1;
	protected static String username2;
	protected static String password2;
	protected static String authToken1; 
	protected static String authToken2; 

	protected static String gameName;
	protected static String gameID;

	/**
	 * Sets up some default user, game, and auth data to test on.
	 * Also tests insertion for all DAOs.
	 */
	@BeforeAll
	public static void initAll() {
		AuthDAO authDAO;
		GameDAO gameDAO;
		UserDAO userDAO;

		try {
			authDAO = new SQLAuthDAO();
			gameDAO = new SQLGameDAO();
			userDAO = new SQLUserDAO();
		} catch (DataAccessException ex) {
			throw new RuntimeException("Couldn't connect to DAOS: " + ex.getMessage(), ex);
		}

		// Clear old data
		Assertions.assertDoesNotThrow(() -> authDAO.clearAllAuthData());
		Assertions.assertDoesNotThrow(() -> userDAO.clearAllUserData());
		Assertions.assertDoesNotThrow(() -> gameDAO.clearAllGameData());

		// Create users
		username1 = "joe";
		username2 = "samwise gamgee";
		password1 = "joepassword";
		password2 = "sampassword";

		UserData userData1 = new UserData(username1, password1, "email1");
		UserData userData2 = new UserData(username2, password2, "email2");
		
		Assertions.assertDoesNotThrow(() -> userDAO.createUser(userData1));
		Assertions.assertDoesNotThrow(() -> userDAO.createUser(userData2));

		// Create auth data
		authToken1 = UUID.randomUUID().toString();
		authToken2 = UUID.randomUUID().toString();

		AuthData authData1 = new AuthData(authToken1, username1);
		AuthData authData2 = new AuthData(authToken2, username2);

		Assertions.assertDoesNotThrow(() -> authDAO.createAuth(authData1));
		Assertions.assertDoesNotThrow(() -> authDAO.createAuth(authData2));

		// Create game data
		String gameName = "myFunGame";

		gameData = Assertions.assertDoesNotThrow(() -> gameDAO.createGame(gameName));
	}
}
