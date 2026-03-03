package dataaccess.memorydao;

import dataaccess.UserDAO;

import java.util.HashMap;

import dataaccess.DataAccessException;
import model.UserData;

public class MemoryUserDAO implements UserDAO {
	//
	// =========================== CONSTRUCTORS =========================== 
	// 
	
	private HashMap<String, UserData> db;

	public MemoryUserDAO() {
		this.db = new HashMap<>();
	}
	
	//
	// =========================== DATA ACCESS =========================== 
	// 

	public UserData getUser(String username) throws DataAccessException {
		if (!this.db.containsKey(username)) {
			throw new DataAccessException("User doesn't exist");
		}
		return this.db.get(username);
	}

	public void createUser(UserData userData) throws DataAccessException {
		if (this.db.containsKey(userData.username())) {
			throw new DataAccessException("User already exists!");
		}

		this.db.put(userData.username(), userData);
	}

	public void clearAllUserData() throws DataAccessException {
		this.db.clear();
	}
}
