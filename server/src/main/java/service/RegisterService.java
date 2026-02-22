package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;

public class RegisterService {

	//
	// ====================== PUBLIC STATIC CLASSES ============================
	//

	public record RegisterRequest(String username, String password, String email) {} 
	public record RegisterResponse(String username, String authToken) {}
	
	//
	// ====================== CONSTRUCTORS ============================
	//
	
	private AuthDAO authDAO;

	public RegisterService(AuthDAO authDAO) {
		this.authDAO = authDAO;
	}

	//
	// ====================== MEMBER METHODS ============================
	//
	
	public RegisterResponse register(RegisterRequest request) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET!!");
	}
}
