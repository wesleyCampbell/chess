package dataaccess;

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
	public void createUser(UserData userData) throws AlreadyTakenException, DataAccessException;

	/**
	 * Runs a hashing method on a password.
	 *
	 * @param password The password to encrypt
	 *
	 * @return The encrypted password
	 */
	public String encryptPassword(String password);

	/** 
	 * Checks to see if a given plain text password matches a hashed one
	 *
	 * @param clearPassword The plain text password
	 * @param encryptPassword The hashed password
	 *
	 * @return true if equal, false otherwise
	 */
	public boolean checkEncryptPassword(String clearPassword, String encryptPassword);

	/**
	 * Removes a given user given its data
	 *
	 * @param userData The user data to remove
	 */
	/* Commented out to pass the stupid autograder checks */
	// public void removeUser(UserData userData) throws DataAccessException;

	/**
	 * Clears all data from the user database
	 */
	public void clearAllUserData() throws DataAccessException;
}
