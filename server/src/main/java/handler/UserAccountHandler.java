package handler;

import service.RegisterService;
import service.RegisterService.RegisterRequest;
import service.RegisterService.RegisterResult;

import util.Debugger;

import java.util.Map;

import dataaccess.*;
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
	
	/**
	 * Given a HTTP request context, will translate it into a format
	 * that the Registration Service can understand.
	 *
	 * @param ctx The Javalin HTTP request context.
	 *
	 * @return True if registration successfull, false otherwise
	 */
	public boolean registerRequest(Context ctx) {
		// RegisterRequest request = fromJson(ctx.body(), RegisterRequest.class);
		RegisterRequest request = extractJsonRequest(ctx.body(), RegisterRequest.class);
		if (request == null) {
			ctx.status(HTTP_CODE_ERROR);
			ctx.result(this.errorHTTPMsg);
			return false;
		}

		ctx.contentType("application/json");

		RegisterResult result;
		try {
			result = this.registerService.register(request);
		} catch (DataAccessException ex) {
			ctx.status(HTTP_CODE_TAKEN);
			ctx.result(this.takenHTTPMsg);
			return false;
		}

		ctx.status(HTTP_CODE_OK);
		// ctx.result(toJson(Map.of(AUTH_REPLY_TOKEN, result.authToken())));
		ctx.result(toJson(result));
		return true;
	}
}
