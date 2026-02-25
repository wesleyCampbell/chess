package handler;

import service.LoginService;
import service.LoginService.LoginRequest;
import service.LoginService.LoginResult;

import service.LogoutService;
import service.LogoutService.LogoutRequest;
import service.LogoutService.LogoutResult;

import dataAccess.*;
import io.javalin.http.Context;

/**
 * Translates HTTP JSON requests into a format that the services can understand
 */
public class LoginCtlHandler extends Handler {
	//
	// ========================== CONSTRUCTORS ===================
	//
	
	private LoginService loginService;
	private LogoutService logoutService;

	/**
	 * Constructor for LoginCtlHandler.
	 *
	 * @param authDAO Reference to the authentication database access object
	 */
	public LoginCtlHandler(AuthDAO authDAO) {
		this.loginService = new LoginService(authDAO);
		this.logoutService = new LogoutService(authDAO);
	}

	//
	// ========================== MEMBER METHODS ===================
	//
	
	/**
	 * Given a JSON login request, will deserialize it into a format the LoginService can understand,
	 * and will make the request, returning a JSON serialization of the result.
	 *
	 * @param requestStr The JSON login request. Must be in format '{"username": "", "password": ""}'
	 *
	 * @return JSON loginResponse string 
	 */
	String loginRequest(String requestStr) {
		// Deserialize the request
		LoginRequest request = fromJson(requestStr, LoginRequest.class);

		LoginResult result;
		try {
			result = this.loginService.login(request);
		} 
		// In this context, a DataAccessException means the password doesn't match the username.
		catch (DataAccessException ex) {
			return unauthorizedHTTPMsg;
		}

		// Return expected result with HTTP code attached
		return toJsonWithHTTPCode(result, HTTP_CODE_OK);
	}

	public void loginRequest(Context ctx) {

	}

	/**
	 * Given a JSON logout request, will deserialize it into a format the LogoutService can understand
	 * and will make the request, returning a JSON serialization of the result.
	 *
	 * @param requestStr The JSON logout request. Must be in format '{"authToken": ""}'.
	 *
	 * @return JSON LogoutResponse string 
	 */
	String logoutRequest(String requestStr) {
		// Deserialize the request
		LogoutRequest request = fromJson(requestStr, LogoutRequest.class);
		
		LogoutResult result;
		try {
			result = this.logoutService.logout(request);
		} 
		// The user's authentication is incorrect
		catch (AuthenticationException ex) {
			return this.unauthorizedHTTPMsg;
		}

		// Return expected result with associated HTTP code
		return toJsonWithHTTPCode(result, HTTP_CODE_OK);
	}

	public void logoutRequest(Context ctx) {

	}
}	


