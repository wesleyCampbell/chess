package dataAccess;

import model.UserData;

/**
 * An interface outlining all methods needed to provide access to user data
 */
public interface UserDAO {
	/**
	 * Gets the user data of a specific user
	 *
	 * @param username The username of the user's data to collect
	 *
	 * @return The data object of the user
	 */
	public UserData getUser(String username) throws DataAccessException;

	/**
	 * Creates a user given its data
	 *
	 * @param userData The user data object to store
	 */
	public void createUser(UserData userData) throws DataAccessException;

	/**
	 * Removes a given user given its data
	 *
	 * @param userData The user data to remove
	 */
	public void removeUser(UserData userData) throws DataAccessException;

	/**
	 * Clears all data from the user database
	 */
	public void clearAllUserData() throws DataAccessException;
}
