package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;

import util.Debugger;

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

		String clearPassword = request.password();
		String hashPassword = userData.password();


		String password = "password";
		String newPass = this.userDAO.encryptPassword(password);


		// Compare passwords
		if (!this.userDAO.checkEncryptPassword(clearPassword, hashPassword)) {
			throw new DataAccessException("User or Password incorrect");
		}

		AuthData authData = this.createNewAuthData(this.authDAO, userData.username());

		LoginResult result = new LoginResult(userData.username(), authData.authToken());
		return result;
	}
}
	
