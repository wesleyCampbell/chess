package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import dataAccess.DataAccessException;

public class RegisterService {

	//
	// ====================== PUBLIC STATIC CLASSES ============================
	//

	public record RegisterRequest(String username, String password, String email) {} 
	public record RegisterResult(String username, String authToken) {}
	
	//
	// ====================== CONSTRUCTORS ============================
	//
	
	private AuthDAO authDAO;
	private UserDAO userDAO;

	public RegisterService(AuthDAO authDAO, UserDAO userDAO) {
		this.authDAO = authDAO;
		this.userDAO = userDAO;
	}

	//
	// ====================== MEMBER METHODS ============================
	//
	
	public RegisterResult register(RegisterRequest request) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET!!");
	}
}
