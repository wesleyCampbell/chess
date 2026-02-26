package service;

import java.util.UUID;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;

public abstract class AuthenticableService {
	/**
	 * Generates a new, random authentication token
	 *
	 * @return The auth token
	 */

	private final static int MAX_GEN_ATTEMPT_NUM = 512;

	protected String generateNewAuthToken() {
		return UUID.randomUUID().toString();	
	}

	/**
	 * Given a data access class and a new username, this method
	 * will return a AuthData object with a unique authToken
	 *
	 * @param dao The data access interface for the auth database
	 * @param username The username to include in the AuthDAta
	 *
	 * @return The Authentication Data object
	 */
	protected AuthData createNewAuthData(AuthDAO dao, String username) throws DataAccessException {
		AuthData data;
		
		// Keep generating new authTokens until we find one that is unique
		int attemptNum = 0;
		while (attemptNum < MAX_GEN_ATTEMPT_NUM) {
			data = new AuthData(generateNewAuthToken(), username);

			// If successfull, won't throw an error and we return the data
			try {
				dao.createAuth(data);
				return data;
			} 
			catch (DataAccessException ex) {
				attemptNum++;
				continue;
			}
		}

		throw new DataAccessException("Auth Token generation failed");
	}
}
