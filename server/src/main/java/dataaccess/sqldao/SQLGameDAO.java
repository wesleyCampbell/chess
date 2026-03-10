package dataaccess.sqldao;

import java.util.Collection;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

public class SQLGameDAO implements GameDAO {
	//
	// ====================== CONSTRUCTORS =======================
	//
	
	public SQLGameDAO() {

	}

	//
	// ======================== DATA ACCESS ===============================
	//
	//
	
	public Collection<GameData> getAllGames() {
		return null;
	}

	private String getNextGameID() {
		return "NOT IMPLEMENTED YET";
	}

	public GameData createGame(String gameName) {
		return null;
	}

	public void updateGame(String gameID, GameData newGameData) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET!");
	}

	public GameData getGame(String gameID) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET!");
	}

	public void clearAllGameData() throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET!");
	}
}
