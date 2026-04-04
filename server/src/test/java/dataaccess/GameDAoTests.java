package dataaccess;

import java.util.HashSet;
import java.util.Collection;

import org.junit.jupiter.api.*;

import dataaccess.sqldao.SQLGameDAO;

import model.GameData;

import util.Debugger;

import chess.*;

public class GameDAoTests extends DatabaseDAoTests { 
	private static GameDAO gameDAO;

	private static String newGameName;
	private static String newGameID;
	//
	// ================ TEST SETUP =================
	//
	
	@BeforeEach
	public void init() {
		gameDAO = Assertions.assertDoesNotThrow(() -> new SQLGameDAO());
	}
	
	//
	// ==================== TEST CASES ================
	//
	
	@Test
	@Order(1)
	public void gameDataPersistance() {
		GameData data = Assertions.assertDoesNotThrow(() -> gameDAO.getGame(gameID));
		
		Assertions.assertEquals(gameID, data.gameID());
		Assertions.assertEquals("", data.whiteUsername());
		Assertions.assertEquals("", data.blackUsername());
		Assertions.assertEquals(gameName, data.gameName());
	}

	@Test
	@Order(2)
	public void gameDataInsertion() {
		newGameName = "New Game";
		
		GameData data = Assertions.assertDoesNotThrow(() -> gameDAO.createGame(newGameName));
		newGameID = data.gameID();

		GameData newData = Assertions.assertDoesNotThrow(() -> gameDAO.getGame(newGameID));

		Assertions.assertTrue(data.equals(newData));
	}

	@Test
	@Order(3)
	public void gameDataDeletion() {
		GameData data = Assertions.assertDoesNotThrow(() -> gameDAO.getGame(newGameID));
		Assertions.assertDoesNotThrow(() -> gameDAO.removeGame(data));
		Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(newGameID));
	}

	@Test
	@Order(4)
	public void gameDataDeserialization() {
		gameData = Assertions.assertDoesNotThrow(() -> gameDAO.createGame(gameName));
		gameID = gameData.gameID();

		GameData data = Assertions.assertDoesNotThrow(() -> gameDAO.getGame(gameID));
		ChessGame game = data.game();

		// Assert that the board is in the initial state
		
		ChessBoard board = game.getBoard();
		ChessGame newGame = new ChessGame();

		Assertions.assertEquals(newGame.getBoard(), board);

		// Test making a move
		ChessPosition startPos = new ChessPosition(2, 1);
		ChessPosition endPos = new ChessPosition(3, 1);
		ChessMove pawnMove = new ChessMove(startPos, endPos, null);

		Assertions.assertDoesNotThrow(() -> game.makeMove(pawnMove));
		Assertions.assertDoesNotThrow(() -> newGame.makeMove(pawnMove));
		Assertions.assertEquals(newGame.getBoard(), board);

		// Test uploading the new game state


		Assertions.assertDoesNotThrow(() -> gameDAO.updateGame(gameID, data));

		// Test persistance of the game state

		GameData updatedData = Assertions.assertDoesNotThrow(() -> gameDAO.getGame(gameID));

		Assertions.assertEquals(newGame, updatedData.game());

	}

	@Test
	@Order(5)
	public void gameDataGetAll() {
		Collection<GameData> gameSet = Assertions.assertDoesNotThrow(() -> gameDAO.getAllGames());

		Assertions.assertNotNull(gameSet);

		Assertions.assertFalse(() -> gameSet.isEmpty());
	}

	@Test 
	@Order(6)
	public void gameDataClearAll() {
		GameData oldData = Assertions.assertDoesNotThrow(() -> gameDAO.getGame(gameID));
		Assertions.assertDoesNotThrow(() -> gameDAO.clearAllGameData());
		Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(gameID));
	}

	@Test
	@Order(7)
	public void gameDataGetIncorrect() {
		Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(gameID));
	}

	@Test
	@Order(8)
	public void gameDataRemoveIncorrect() {
		GameData spoofedData = new GameData("39", "", "", "foo", new ChessGame());
		Assertions.assertDoesNotThrow(() -> gameDAO.removeGame(spoofedData));
	}
}
