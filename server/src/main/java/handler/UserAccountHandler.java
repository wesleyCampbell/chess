package handler;

import service.RegisterService;
import service.RegisterService.RegisterRequest;
import service.RegisterService.RegisterResult;

import dataAccess.*;
import io.javalin.http.Context;

public class UserAccountHandler extends Handler {
	//
	// ======================== CONSTRUCTORS ====================
	//
	
	private RegisterService registerService;

	public UserAccountHandler(AuthDAO authDAO, UserDAO userDAO) {
		this.registerService = new RegisterService(authDAO, userDAO);
	}
	
	//
	// ======================== MEMBER METHODS ====================
	//
	
	public String registerRequest(String requestStr) {
		// Deserialize the request into its correct form
		RegisterRequest request = fromJson(requestStr, RegisterRequest.class);

		RegisterResult result;
		try {
			result = this.registerService.register(request);
		}
		// In this context, a DataAccessException represents the username already being taken.
		catch (DataAccessException ex) {
			return this.takenHTTPMsg;
		}

		// Return the expected result + HTTP code
		return toJsonWithHTTPCode(result, HTTP_CODE_OK);
	}

	public void registerRequest(Context ctx) {

	}
}
