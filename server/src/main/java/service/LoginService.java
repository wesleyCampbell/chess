package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;

public class LoginService {
	//
	// ====================== PUBLIC STATIC CLASSES ====================== 
	//
	
	public static record LoginRequest(String username, String password) {}
	public static record LoginResponse(String username, String authToken) {}
	
	//
	// ====================== CONSTRUCTORS ====================== 
	//
	
	private AuthDAO authDAO;

	public LoginService(AuthDAO authDAO) {
		this.authDAO = authDAO;
	}
	
	//
	// ====================== MEMBER METHODS ====================== 
	//

	public LoginResponse login(LoginRequest request) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");
	}
}
	
