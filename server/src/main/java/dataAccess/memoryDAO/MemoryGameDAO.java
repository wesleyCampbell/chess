package dataAccess.memoryDAO;

import java.util.Collection;

import dataAccess.GameDAO;
import dataAccess.DataAccessException;
import model.GameData;

public class MemoryGameDAO implements GameDAO {
	public Collection<GameData> getAllGames() throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");
	}

	public GameData createGame(String gameName) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");
	}

	public GameData getGame(String gameID) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");
	}

	public void removeGame(GameData gameData) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");
	}

	public void clearAllGameData() throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");
	}
}
