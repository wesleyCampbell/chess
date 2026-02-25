package handler;

import service.ClearDataService;
import service.ClearDataService.ClearDataRequest;
import service.ClearDataService.ClearDataResult;

import com.google.gson.Gson;

import dataAccess.*;
import io.javalin.http.Context;

/**
 * Handles all database data related HTTP requests.\
 */
public class DatabaseHandler extends Handler {

	//
	// =========================== CONSTRUCTORS ========================
	//
	
	private ClearDataService clearDataService;

	/**
	 * Constructor for DatabaseHandler. Takes in all of the necessary data access objects that relate to database manipulation.
	 *
	 * @param authDAO The data access object for the authentication data
	 * @param gameDAO The data access object for the game data
	 * @param userDAO The data access object for the user data
	 */
	public DatabaseHandler(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
		this.clearDataService = new ClearDataService(authDAO, gameDAO, userDAO);
	}

	//
	// =========================== MEMBER METHODS ========================
	//
	
	public String clearDataRequest(String parameters) {
		ClearDataRequest request = new ClearDataRequest();
		ClearDataResult result;
		try {
			result = this.clearDataService.clearData(request);
		} catch (DataAccessException ex)  {
				
		}

		return this.successHTTPMsg;
	}

	public void clearDataRequest(Context cxt) {

	}
}
