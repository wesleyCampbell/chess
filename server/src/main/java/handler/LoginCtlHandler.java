package handler;

import service.LoginService;
import service.LoginService.LoginRequest;
import service.LoginService.LoginResult;

import service.LogoutService;
import service.LogoutService.LogoutRequest;
import service.LogoutService.LogoutResult;

import static util.Debugger.debug;

import java.util.Map;

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
	public LoginCtlHandler(AuthDAO authDAO, UserDAO userDAO) {
		this.loginService = new LoginService(authDAO, userDAO);
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

	/**
	 * Given a HTTP request Context object, this deserialize it into a format that the LoginService
	 * can understand. It will then make the request, putting the result into the context.
	 *
	 * @param ctx The Javalin HTTP context object
	 *
	 * @param True if login is successfull, false otherwise
	 */
	public boolean loginRequest(Context ctx) {
		LoginRequest request = fromJson(ctx.body(), LoginRequest.class);

		debug(String.format("request: %s", request));

		ctx.contentType("application/json");

		LoginResult result;
		try {
			result = this.loginService.login(request);
		} catch (DataAccessException ex) {
			ctx.status(HTTP_CODE_UNAUTH);
			ctx.result(this.unauthorizedHTTPMsg);
			return false;
		}

		ctx.status(HTTP_CODE_OK);
		ctx.result(toJson(Map.of(AUTH_REPLY_TOKEN, result.authToken())));
		return true;
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


