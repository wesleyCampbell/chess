package dataAccess.memoryDAO;

import java.util.HashMap;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {

	//
	// ====================== CONSTRUCTORS =====================
	//
	
	private HashMap<String, AuthData> db;

	public MemoryAuthDAO() {
		this.db = new HashMap<>();
	}
	
	//
	// ====================== DATA ACCESS =====================
	//

	public AuthData getAuth(String authToken) throws DataAccessException{
		AuthData data = this.db.get(authToken);

		// If the data doesn't exist, throw an error
		if (data == null) {
			throw new DataAccessException("Auth data doesn't exist");
		}
		
		return data;
	}

	public void createAuth(AuthData authData) throws DataAccessException {
		// Check to see if the data already exists
		if (this.db.containsKey(authData.authToken())) {
			throw new DataAccessException("Authentication data already exists");
		}

		this.db.put(authData.authToken(), authData);
	}

	public void removeAuth(AuthData authData) throws DataAccessException {
		// Check to see if the data actually exists
		if (!this.db.containsKey(authData.authToken())) {
			throw new DataAccessException("Authentication data does not exist");
		}

		this.db.remove(authData.authToken());
	}

	public void clearAllAuthData() throws DataAccessException{
		this.db.clear();
	}
}
