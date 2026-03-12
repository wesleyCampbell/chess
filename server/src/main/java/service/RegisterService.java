package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import dataaccess.AlreadyTakenException;

import model.UserData;
import model.AuthData;

public class RegisterService extends AuthenticableService {

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
	
	public RegisterResult register(RegisterRequest request) throws AlreadyTakenException, DataAccessException {
		String encrPasword = this.userDAO.encryptPassword(request.password());
		UserData userData = new UserData(request.username(), encrPasword, request.email());

		// this will throw an error if the username is already taken
		this.userDAO.createUser(userData);
		
		AuthData authData = this.createNewAuthData(authDAO, userData.username());

		return new RegisterResult(request.username(), authData.authToken());
	}
}
