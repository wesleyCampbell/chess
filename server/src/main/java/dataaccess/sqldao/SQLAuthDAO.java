package dataaccess.sqldao;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class SQLAuthDAO implements AuthDAO {
	
	//
	// ======================= CONSTRUCTORS ======================
	//
	
	public SQLAuthDAO() {

	}

	//
	// ======================= DATA ACCESS ========================
	//

	public AuthData getAuth(String authToken) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");
	}

	public void createAuth(AuthData authData) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");
	}

	public void removeAuth(AuthData authData) throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");
	}

	public void clearAllAuthData() throws DataAccessException {
		throw new DataAccessException("NOT IMPLEMENTED YET");
	}
}
