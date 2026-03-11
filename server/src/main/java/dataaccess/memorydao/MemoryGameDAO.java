package dataaccess.memorydao;

import java.util.Collection;
import java.util.HashMap;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.GameData;

public class MemoryGameDAO implements GameDAO {
	//
	// ====================== CONSTRUCTORS =======================
	//
	
	private HashMap<String, GameData> db;
	private int nextOpenID; 
	
	public MemoryGameDAO() {
		this.db = new HashMap<>();
		this.nextOpenID = 1;
	}

	//
	// ======================== DATA ACCESS ===============================
	//
	
	public Collection<GameData> getAllGames() throws DataAccessException {
		return this.db.values();
	}

	private String getNextGameID() {
		this.nextOpenID++;
		return String.valueOf(this.nextOpenID - 1);
	}

	public GameData createGame(String gameName) throws DataAccessException {
		String gameID = this.getNextGameID();
		ChessGame game = new ChessGame();

		GameData data = new GameData(gameID, "", "", gameName, game);

		this.db.put(gameID, data);

		return data;
	}

	public void updateGame(String gameID, GameData newGameData) throws DataAccessException {
		// Will throw an error if the game doesn't exist. 
		// We'll let it propogate up the stack.
		GameData data = this.getGame(gameID);	
		this.removeGame(data);

		this.db.put(gameID, newGameData);
	}

	public GameData getGame(String gameID) throws DataAccessException {
		GameData data = this.db.get(gameID);

		// If the game doesn't exist, throw an error
		if (data == null) {
			throw new DataAccessException("Game doesn't exist");
		}

		return data;
	}

	public void removeGame(GameData gameData) throws DataAccessException {
		// Check to see if the game actually exists
		if (!this.db.containsKey(gameData.gameID())) {
			throw new DataAccessException("Game data doesn't exist");
		}

		this.db.remove(gameData.gameID());
	}

	public void clearAllGameData() throws DataAccessException {
		this.db.clear();
	}
}
