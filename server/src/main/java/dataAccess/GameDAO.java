package dataAccess;

import model.GameData;

import java.util.Collection;

/**
 * The interface for providing access to game data
 */
public interface GameDAO {
	/**
	 * Will return a collection of all games in the database
	 *
	 * @return A Collection containing all data from games.
	 */
	public Collection<GameData> getAllGames() throws DataAccessException;	

	/**
	 * Creates a game with given data.
	 *
	 * @param gameName The name of the game
	 *
	 * @return The data object for the game
	 */
	public GameData createGame(String gameName) throws DataAccessException;

	/**
	 * Gets a given game, given its id
	 *
	 * @param gameID The identifier of the game
	 *
	 * @return The game data object
	 */
	public GameData getGame(String gameID) throws DataAccessException;

	/**
	 * Removes a game from the database
	 *
	 * @param gameData The data to remove
	 */
	public void removeGame(GameData gameData) throws DataAccessException;

	/**
	 * Clears all data from the game database
	 */
	public void clearAllGameData() throws DataAccessException;
}
