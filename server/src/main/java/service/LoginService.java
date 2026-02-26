package service;

import static util.Debugger.debug;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import dataAccess.DataAccessException;

import model.UserData;
import model.AuthData;

public class LoginService extends AuthenticableService {
	//
	// ====================== PUBLIC STATIC CLASSES ====================== 
	//
	
	public static record LoginRequest(String username, String password) {}
	public static record LoginResult(String username, String authToken) {}
	
	//
	// ====================== CONSTRUCTORS ====================== 
	//
	
	private AuthDAO authDAO;
	private UserDAO userDAO;

	public LoginService(AuthDAO authDAO, UserDAO userDAO) {
		this.authDAO = authDAO;
		this.userDAO = userDAO;
	}
	
	//
	// ====================== MEMBER METHODS ====================== 
	//

	public LoginResult login(LoginRequest request) throws DataAccessException {
		// Will throw error if username is invalid
		UserData userData = this.userDAO.getUser(request.username());

		// Compare passwords
		if (!request.password().equals(userData.password())) {
			throw new DataAccessException("User or Password incorrect");
		}

		AuthData authData = this.createNewAuthData(this.authDAO, userData.username());

		LoginResult result = new LoginResult(userData.username(), authData.authToken());

		return result;
	}
}
	
