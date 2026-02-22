package dataAccess.memoryDAO;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {
	public AuthData getAuth(String authToken) throws DataAccessException{

		throw new DataAccessException("NOT IMPLEMENTED YET!!");
	}

	public void removeAuth(AuthData authData) throws DataAccessException {

	}

	public void clearAllAuthData() throws DataAccessException{

	}
}
