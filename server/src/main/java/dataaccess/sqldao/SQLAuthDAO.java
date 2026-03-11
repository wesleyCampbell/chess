package dataaccess.sqldao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.*;

import util.Debugger;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class SQLAuthDAO extends SQLDatabaseDAO implements AuthDAO {
	// 
	// =========================== GLOBALS ============================
	//
	
	private static final String DB_NAME = "auth";

	private static final String DB_INIT_STATEMENT = String.format("""
			CREATE TABLE IF NOT EXISTS %s (
				`authToken` varchar(512) NOT NULL,
				`username` varchar(256) NOT NULL,
				PRIMARY KEY (`authToken`)
				) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
			""", DB_NAME);

	private static final String DB_INSERT_AUTH_STATEMENT = String.format("""
			INSERT INTO %s (authToken, username) VALUES (?, ?)
			""", DB_NAME);

	private static final String DB_SELECT_AUTH_STATEMENT = String.format("""
			SELECT authToken, username FROM %s WHERE authToken=?
			""", DB_NAME);

	private static final String DB_CLEAR_DATA_STATEMENT = String.format("""
			TRUNCATE TABLE %s
			""", DB_NAME);

	private static final String DB_CLEAR_AUTH_STATEMENT = String.format("""
			DELETE FROM %s WHERE authToken=?
			""", DB_NAME);
	
	//
	// ======================= CONSTRUCTORS ======================
	//
	
	
	public SQLAuthDAO() throws DataAccessException {
		super(DB_INIT_STATEMENT);
	}

	//
	// ======================= DATA ACCESS ========================
	//

	/**
	 * Reads the AuthData stored in a SQL ResultSet and returns it in its
	 * correct data representation.
	 *
	 * @param rs The SQL ResultSet returned from executing a query
	 *
	 * @return The AuthData object represented
	 */
	private AuthData readAuth(ResultSet rs) throws SQLException {
		String authToken = rs.getString("authToken");
		String username = rs.getString("username");

		return new AuthData(authToken, username);
	}

	/**
	 * Fetches an AuthData Object from the database.
	 *
	 * @param authToken The auth token of the query
	 *
	 * @return The requested AuthData object
	 */
	public AuthData getAuth(String authToken) throws DataAccessException {
		ArrayList<AuthData> authData = this.executeQuery(
												DB_SELECT_AUTH_STATEMENT,
												rs -> this.readAuth(rs),
												authToken);

		// There should only ever be one user with a given authToken
		if (authData.size() != 1) {
			throw new DataAccessException("More than one user with given authToken!");
		}

		return authData.get(0);
	}

	/**
	 * Inserts the data from a given AuthData object into the database.
	 *
	 * @param authData The AuthData to store
	 */
	public void createAuth(AuthData authData) throws DataAccessException {
		this.executeUpdate(DB_INSERT_AUTH_STATEMENT,
				authData.authToken(),
				authData.username());
	}

	/**
	 * Will remove the data from a given AuthData object from the database.
	 *
	 * @param authData The AuthData to remove from the database.
	 */
	public void removeAuth(AuthData authData) throws DataAccessException {
		this.executeUpdate(DB_CLEAR_AUTH_STATEMENT, authData.authToken());
	}

	/**
	 * Clears all authentication data from the database.
	 */
	public void clearAllAuthData() throws DataAccessException {
		this.executeStatement(DB_CLEAR_DATA_STATEMENT);
	}
}
