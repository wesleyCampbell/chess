package service;

import dataaccess.AuthDAO;
import dataaccess.AuthenticationException;
import dataaccess.DataAccessException;

import model.AuthData;

public class LogoutService extends AuthenticableService {
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
		AuthData data;
		try {
			data = this.authDAO.getAuth(request.authToken());
			this.authDAO.removeAuth(data);

			return new LogoutResult();
		} catch (DataAccessException ex) {
			throw new AuthenticationException("User not authenticated");
		}
	}


}
