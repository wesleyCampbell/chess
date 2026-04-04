package client;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.*;

import chess.ChessGame.TeamColor;
import dataaccess.sqldao.*;
import model.AuthData;
import model.GameData;
import server.Server;

import service.ClearDataService;
import service.ClearDataService.ClearDataRequest;
import util.Debugger;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {
	private static final String SERVER_ADDR = "localhost";

    private static Server server;
	private static ServerFacade facade;
	
	private static ClearDataService clearData;

	private static String username1 = "joe";
	private static String password1 = "JoesPassword";

	private static String username2 = "bob";
	private static String password2 = "BobPassword";

	private static String authToken1 = "";
	private static String authToken2 = "";
	private static List<String> gameNames = new ArrayList<>();

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
		facade = new ServerFacade(SERVER_ADDR, port);

		clearData = Assertions.assertDoesNotThrow(() -> new ClearDataService(
					new SQLAuthDAO(), new SQLGameDAO(), new SQLUserDAO()));

		// Clears the database so that we don't have any unexpected errors
		Assertions.assertDoesNotThrow(() -> clearData.clearData(new ClearDataRequest()));

    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

	@Test
	@Order(1)
	public void registerPosTest() {
		AuthData data = Assertions.assertDoesNotThrow(() -> facade.register(
					username1, password1, "email"));

		authToken1 = data.authToken();

		data = Assertions.assertDoesNotThrow(() -> facade.register(
					username2, password2, "email"));

		authToken2 = data.authToken();
	}

	@Test
	@Order(2)
	public void registerNegTest() {
		Assertions.assertThrows(
				client.exception.DataAccessException.class,
				() -> facade.register(username1, password1, "email"));
		Assertions.assertThrows(
				client.exception.DataAccessException.class,
				() -> facade.register(username2, password2, "email"));

	}

	@Test
	@Order(3)
	public void logoutPosTest() {
		Assertions.assertDoesNotThrow(
				() -> facade.logout(authToken1));
		Assertions.assertDoesNotThrow(
				() -> facade.logout(authToken2));
	}

	@Test
	@Order(4)
	public void logoutNegTests() {
		Assertions.assertThrows(
				client.exception.AuthenticationException.class,
				() -> facade.logout(authToken1));
		Assertions.assertThrows(
				client.exception.AuthenticationException.class,
				() -> facade.logout(authToken2));
		Assertions.assertThrows(
				client.exception.AuthenticationException.class,
				() -> facade.logout("BAD AUTH TOKEN"));

	}

	@Test
	@Order(5)
	public void loginPosTest() {
		AuthData data = Assertions.assertDoesNotThrow(
				() -> facade.login(username1, password1));
		authToken1 = data.authToken();

		data = Assertions.assertDoesNotThrow(
				() -> facade.login(username2, password2));

		authToken2 = data.authToken();
	}

	@Test 
	@Order(6)
	public void loginNegTest() {
		Assertions.assertThrows(
				client.exception.DataAccessException.class,
				() -> facade.login("Bad Username", password1));
		Assertions.assertThrows(
				client.exception.DataAccessException.class,
				() -> facade.login("Bad Username2", password2));
	}

	@Test
	@Order(7)
	public void createGamePosTest() {
		String game1 = Assertions.assertDoesNotThrow(
				() -> facade.createGame(authToken1, "game1"));
		gameNames.add(game1);

		String game2 = Assertions.assertDoesNotThrow(
				() -> facade.createGame(authToken2, "game2"));
		gameNames.add(game2);
	}

	@Test
	@Order(8)
	public void createGameNegTests() {
		Assertions.assertThrows(
				client.exception.AuthenticationException.class, 
				() -> facade.createGame("Bad auth token", "Game 2"));
	}

	@Test
	@Order(9)
	public void listGamesPosTest() {
		Collection<GameData> games = Assertions.assertDoesNotThrow(
				() -> facade.listGames(authToken1));

		List<String> dbGameNames = new ArrayList<>();
		for (GameData game : games) {
			dbGameNames.add(game.gameID());
		}


		Assertions.assertTrue(dbGameNames.containsAll(gameNames));
		Assertions.assertTrue(gameNames.containsAll(dbGameNames));
	}

	@Test
	@Order(10)
	public void listGameNegTests() {
		Assertions.assertThrows(
				client.exception.AuthenticationException.class,
				() -> facade.listGames("Bad auth token"));
	}

	@Test
	@Order(11)
	public void joinGamePosTest() {
		Assertions.assertDoesNotThrow(
				() -> facade.joinGame(authToken1, gameNames.get(0), TeamColor.WHITE)); 
		Assertions.assertDoesNotThrow(
				() -> facade.joinGame(authToken2, gameNames.get(1), TeamColor.BLACK)); 
	}

	@Test
	@Order(12)
	public void joinGameNegTests() {
		Assertions.assertThrows(
				client.exception.AuthenticationException.class, 
				() -> facade.joinGame("BAD", gameNames.get(0), TeamColor.WHITE));

		Assertions.assertThrows(
				client.exception.AlreadyTakenException.class,
				() -> facade.joinGame(authToken1, gameNames.get(1), TeamColor.BLACK));
		Assertions.assertThrows(
				client.exception.AlreadyTakenException.class,
				() -> facade.joinGame(authToken2, gameNames.get(0), TeamColor.WHITE));
	}

}
