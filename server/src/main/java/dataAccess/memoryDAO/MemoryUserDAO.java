package dataAccess.memoryDAO;

import dataAccess.UserDAO;
import dataAccess.DataAccessException;
import model.UserData;

public class MemoryUserDAO implements UserDAO {
	public UserData getUser(String username) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");
	}

	public void createUser(UserData userData) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");

	}

	public void removeUser(UserData userData) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");
	}

	public void clearAllUserData() throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");
	}
}
