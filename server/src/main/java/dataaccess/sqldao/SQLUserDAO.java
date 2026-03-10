package dataaccess.sqldao;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;

import model.UserData;

public class SQLUserDAO implements UserDAO {
	//
	// =========================== CONSTRUCTORS =========================== 
	// 
	public SQLUserDAO() {

	}

	//
	// =========================== DATA ACCESS =========================== 
	// 
	
	public UserData getUser(String username) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET!");
	}

	public void createUser(UserData userData) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET!");
	}

	public void clearAllUserData() throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET!");
	}
}
