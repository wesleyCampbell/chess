package handler;

import service.ClearDataService;
import service.ClearDataService.ClearDataRequest;
import service.ClearDataService.ClearDataResult;

import com.google.gson.Gson;

import dataaccess.*;
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

	/**
	 * Translates an HTTP request to clear the database into one the service can understand
	 *
	 * @param ctx The Javalin HTTP context
	 *
	 * @return True if successfull, false if otherwise
	 */
	public boolean clearDataRequest(Context cxt) {
		ClearDataRequest request = new ClearDataRequest();

		cxt.contentType("application/json");

		ClearDataResult result;
		try {
			result = this.clearDataService.clearData(request);
		} catch (DataAccessException ex) {
			cxt.status(HTTP_CODE_ERROR);
			cxt.result(this.errorHTTPMsg);
			return false;
		}

		cxt.status(HTTP_CODE_OK);
		cxt.result(this.successHTTPMsg);
		return true;
	}
}
