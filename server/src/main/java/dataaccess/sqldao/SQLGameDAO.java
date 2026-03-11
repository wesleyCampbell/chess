package dataaccess.sqldao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

public class SQLGameDAO extends SQLDatabaseDAO implements GameDAO {
	// 
	// =========================== GLOBALS ============================
	//
	
	private static final String DB_NAME = "games";

	private static final String DB_INIT_STATEMENT = String.format("""
			CREATE TABLE IF NOT EXISTS %s (
				`gameId` int NOT NULL AUTO_INCREMENT,
				`whiteUsername` varchar(256) NOT NULL,
				`blackUsername` varchar(256) NOT NULL,
				`gameName` varchar(256) NOT NULL,
				`game` varchar(1024) NOT NULL,
				PRIMARY KEY (`gameID`),
				INDEX(whiteUsername),
				INDEX(blackUsername)
				) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
			""", DB_NAME);

	private static final String DB_INSERT_GAME_STATEMENT = String.format("""
			INSERT INTO %s (whiteUsername, blackUsername, gameName, game) 
			VALUES (?, ?, ?, ?)
			""", DB_NAME);
	
	private static final String DB_SELECT_GAME_STATEMENT = String.format("""
			SELECT * FROM %s WHERE gameID=?
			""", DB_NAME);

	private static final String DB_SELECT_ALL_GAMES_STATEMENT = String.format("""
			SELECT * FROM %s
			""", DB_NAME);

	private static final String DB_UPDATE_GAME_STATEMENT = String.format("""
			UPDATE %s SET whiteUsername = ?,
			blackUsername = ?,
			gameName = ?,
			game = ? WHERE gameID=?
			""", DB_NAME);

	private static final String DB_CLEAR_DATA_STATEMENT = String.format("""
			TRUNCATE TABLE %s
			""", DB_NAME);

	private static final String DB_CLEAR_GAME_STATEMENT = String.format("""
			DELETE FROM %s WHERE gameID=?
			""", DB_NAME);

	//
	// ====================== CONSTRUCTORS =======================
	//
	
	public SQLGameDAO() throws DataAccessException {
		super(DB_INIT_STATEMENT);
	}

	//
	// ======================== DATA ACCESS ===============================
	//
	//
	
	/**
	 * Reads the GameData stored in a SQL ResultSet and returns it in its correct data representation.
	 *
	 * @param rs The SQL ResultSet returned from executing a query.
	 *
	 * @return The GameData object represented.
	 */
	private GameData readGame(ResultSet rs) throws SQLException {
		String gameID = rs.getString("gameID");
		String whiteUsername = rs.getString("whiteUsername");
		String blackUsername = rs.getString("blackUsername");
		String gameName = rs.getString("gameName");
		String gameJson = rs.getString("game");
		ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);

		return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
	}

	/**
	 * Fetches the game with the given gameID from the database, if it exists.
	 *
	 * @param gameID The id of the game to fetch
	 *
	 * @param return The GameData of the requested game
	 */
	public GameData getGame(int gameID) throws DataAccessException {
		ArrayList<GameData> games = this.executeQuery(DB_SELECT_GAME_STATEMENT,
														rs -> this.readGame(rs),
														gameID);

		if (games.size() != 1) {
			throw new DataAccessException("More than one game with given gameID!");
		}

		return games.get(0);
	}


	/**
	 * Fetches the game with the given gameID from the database, if it exists.
	 *
	 * @param gameID The id of the game to fetch
	 *
	 * @param return The GameData of the requested game
	 */
	public GameData getGame(String gameID) throws DataAccessException {
		return this.getGame(Integer.parseInt(gameID));
	}
	
	/**
	 * Fetches all games in the database
	 *
	 * @return A Collection of all games in database
	 */
	public Collection<GameData> getAllGames() throws DataAccessException {
		return this.executeQuery(DB_SELECT_ALL_GAMES_STATEMENT,
									rs -> this.readGame(rs));
	}

	/**
	 * Takes a game name and will create a new, empty game with a new gameID.
	 *
	 * @param gameName The name of the new Chess Game
	 *
	 * @param The GameData of the new game
	 */
	public GameData createGame(String gameName) throws DataAccessException {
		int gameID = this.executeUpdate(DB_INSERT_GAME_STATEMENT,
						"",
						"",
						gameName,
						new Gson().toJson(new ChessGame())
						);

		GameData outData = this.getGame(gameID);
		return outData;
	}

	/**
	 * Will update the entry of a game with new data, if it exists.
	 *
	 * @param gameID The id of the game to update
	 * @param newGameData The updated game data
	 */
	public void updateGame(String gameID, GameData newGameData) throws DataAccessException {
		String whiteUsername = newGameData.whiteUsername();
		String blackUsername = newGameData.blackUsername();
		String gameName = newGameData.gameName();
		String gameJSON = new Gson().toJson(newGameData.game());

		this.executeUpdate(DB_UPDATE_GAME_STATEMENT,
				whiteUsername,
				blackUsername,
				gameName,
				gameJSON,
				Integer.parseInt(gameID));  // WHERE clause
	}


	/**
	 * Clears all game data from the database.
	 */
	public void clearAllGameData() throws DataAccessException {
		this.executeStatement(DB_CLEAR_DATA_STATEMENT);
	}

	/**
	 * Will remove the data from a given GameData object from the database
	 *
	 * @param gameData The GameData to remove from the database.
	 */
	public void removeGame(GameData gameData) throws DataAccessException {
		this.executeUpdate(DB_CLEAR_GAME_STATEMENT, gameData.gameID());
	}
}
