package dataaccess.memorydao;

import java.util.HashMap;

import dataaccess.AuthDAO;
import dataaccess.AuthenticationException;
import dataaccess.DataAccessException;
import dataaccess.AlreadyTakenException;
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

	public AuthData getAuth(String authToken) throws DataAccessException, AuthenticationException {
		AuthData data = this.db.get(authToken);

		// If the data doesn't exist, throw an error
		if (data == null) {
			throw new AuthenticationException("Auth data doesn't exist");
		}
		
		return data;
	}

	public boolean authExists(String authToken) throws DataAccessException {
		return this.db.containsKey(authToken);
	}

	public void createAuth(AuthData authData) throws AlreadyTakenException, DataAccessException {
		// Check to see if the data already exists
		if (this.db.containsKey(authData.authToken())) {
			throw new AlreadyTakenException("Authentication data already exists");
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
