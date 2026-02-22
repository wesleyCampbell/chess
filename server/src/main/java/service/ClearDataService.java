package service;

import dataAccess.DataAccessException;
import dataAccess.*;

public class ClearDataService {

	//
	// =================== PUBLIC STATIC CLASSES =================== 
	//

	public static record ClearDataRequest() {}
	public static record ClearDataResult() {}
	
	//
	// =================== CONSTRUCTORS =================== 
	//
	
	private AuthDAO authDAO;
	private GameDAO gameDAO;
	private UserDAO userDAO;

	public ClearDataService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
		this.authDAO = authDAO;
		this.gameDAO = gameDAO;
		this.userDAO = userDAO;
	}

	//
	// =================== MEMBER METHODS =================== 
	//
	public ClearDataResult clearData(ClearDataRequest request) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");
	}
}
