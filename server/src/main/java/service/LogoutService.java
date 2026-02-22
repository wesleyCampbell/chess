package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;

public class LogoutService {
	//
	// ======================= PUBLIC STATIC CLASSES ========================
	//
	
	public static record LogoutRequest(String authToken) {}
	public static record LogoutResponse() {}

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
	
	public LogoutResponse logout(LogoutRequest request) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET!!");
	}


}
