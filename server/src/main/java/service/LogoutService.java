package service;

import dataAccess.AuthDAO;
import dataAccess.AuthenticationException;

public class LogoutService {
	//
	// ======================= PUBLIC STATIC CLASSES ========================
	//
	
	public static record LogoutRequest(String authToken) {}
	public static record LogoutResult() {}

	//
	// ======================= CONSTRUCTORS ========================
	//
	
	private AuthDAO authDAO;

	public LogoutService(AuthDAO authDAO) {
		this.authDAO = authDAO;
	}

	//
	// ======================= MEMBER METHODS ========================
	//
	
	public LogoutResult logout(LogoutRequest request) throws AuthenticationException {
		throw new AuthenticationException("NOT IMPLEMENTED YET!!");
	}


}
