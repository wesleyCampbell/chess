package handler;

import service.LoginService;
import service.LoginService.LoginRequest;
import service.LoginService.LoginResult;

import service.LogoutService;
import service.LogoutService.LogoutRequest;
import service.LogoutService.LogoutResult;
import util.Debugger;
import dataaccess.*;
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
	 * Given a HTTP request Context object, this deserialize it into a format that the LoginService
	 * can understand. It will then make the request, putting the result into the context.
	 *
	 * @param ctx The Javalin HTTP context object
	 *
	 * @param True if login is successfull, false otherwise
	 */
	public boolean loginRequest(Context ctx) {
		// LoginRequest request = fromJson(ctx.body(), LoginRequest.class);
		LoginRequest request = extractJsonRequest(ctx.body(), LoginRequest.class);
		if (request == null) {
			ctx.status(HTTP_CODE_ERROR);
			ctx.result(this.errorHTTPMsg);
			return false;
		}

		ctx.contentType("application/json");

		LoginResult result;
		try {
			result = this.loginService.login(request);
		} catch (AuthenticationException ex) {
			ctx.status(HTTP_CODE_UNAUTH);
			ctx.result(this.unauthorizedHTTPMsg);
			return false;
		} catch (DataAccessException ex) {
			ctx.status(HTTP_CODE_INT_ERROR);
			ctx.result(this.errorHTTPMsg);
			return false;
		}

		ctx.status(HTTP_CODE_OK);
		ctx.result(toJson(result));
		return true;
	}

	/**
	 * Given a HTTP context object, this method will extract the logout request
	 * and will pass it to the logoutService.
	 *
	 * @param ctx The Javalin HTTP context object
	 *
	 * @return True if logout successfull, false otherwise
	 */
	public boolean logoutRequest(Context ctx) {
		// Deserialzie the request
		// LogoutRequest request = fromJson(ctx.body(), LogoutRequest.class);
		String authToken = ctx.header(HTTP_HEADER_AUTH);
		LogoutRequest request = new LogoutRequest(authToken);

		ctx.contentType("application/json");

		LogoutResult result;
		try {
			result = this.logoutService.logout(request);
		} catch (AuthenticationException ex) {
			ctx.status(HTTP_CODE_UNAUTH);
			ctx.result(this.unauthorizedHTTPMsg);
			return false;
		} catch (DataAccessException ex) {
			ctx.status(HTTP_CODE_INT_ERROR);
			ctx.result(this.intErrorHTTPMsg);
			return false;
		}

		ctx.status(HTTP_CODE_OK);
		ctx.result(toJson(result));
		return true;
	}
}	


