package dataAccess;

import dataAccess.DataAccessException;
import model.AuthData;

/**
 * Interface for Authentication Data Access
 */
public interface AuthDAO {
	/**
	 * Given an authentication token, will return the AuthData
	 * associated with it, if it exists. Otherwise, will return false.
	 *
	 * @param authToken The string authentication token
	 *
	 * @return The AuthData if exists, null otherwise
	 */
	public AuthData getAuth(String authToken) throws DataAccessException;

	/**
	 * Will return a given AuthData object from the database
	 *
	 * @param authData The data to remove
	 */
	public void removeAuth(AuthData authData) throws DataAccessException;

	/**
	 * Clears all AuthData from the database
	 */
	public void clearAllAuthData() throws DataAccessException;
}
