package service;

import dataaccess.DataAccessException;
import util.Debugger;
import dataaccess.*;

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

	/**
	 * Clears all data from all databases.
	 *
	 * @return A result object storing necessary data
	 */
	public ClearDataResult clearData(ClearDataRequest request) throws DataAccessException {
		Debugger.debug("Inside clearData()", 1);
		this.authDAO.clearAllAuthData();
		this.gameDAO.clearAllGameData();
		this.userDAO.clearAllUserData();

		return new ClearDataResult();
	}
}
