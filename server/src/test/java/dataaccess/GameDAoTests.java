package dataaccess;

import org.junit.jupiter.api.*;

import dataaccess.sqldao.SQLGameDAO;
import model.GameData;

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
}
