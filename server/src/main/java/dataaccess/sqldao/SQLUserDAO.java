package dataaccess.sqldao;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;

import org.mindrot.jbcrypt.BCrypt;

import model.UserData;
import util.Debugger;

import java.sql.*;
import java.util.ArrayList;

public class SQLUserDAO extends SQLDatabaseDAO implements UserDAO {
	// 
	// =========================== GLOBALS ============================
	//
	
	private static final String DB_NAME = "users";

	private static final String DB_INIT_STATEMENT = String.format("""
		CREATE TABLE IF NOT EXISTS %s (
			`username` varchar(256) NOT NULL,
			`password` varchar(256) NOT NULL,
			`email` varchar(256) NOT NULL,
			PRIMARY KEY (`username`)
			) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
		""", DB_NAME);

	private static final String DB_INSERT_USER_STATEMENT = String.format("""
			INSERT INTO %s (username, password, email) VALUES (?, ?, ?)
			""", DB_NAME);

	private static final String DB_SELECT_USER_STATEMENT = String.format("""
			SELECT username, password, email FROM %s WHERE username=?
			""", DB_NAME);

	private static final String DB_CLEAR_DATA_STATEMENT = String.format("""
			TRUNCATE TABLE %s
			""", DB_NAME);

	//
	// ========================== CONSTRUCTORS ==========================
	//
	
	public SQLUserDAO() throws DataAccessException {
		super(DB_INIT_STATEMENT);
	}
	
	//
	// =========================== DATA ACCESS =========================== 
	// 
	
	/**
	 * Reads the UserData stored in a SQL ResultSet and returns it in its
	 * correct data representation.
	 *
	 * @param rs The SQL ResultSet returned from executing a query
	 *
	 * @return The UserData object represented
	 */
	private UserData readUser(ResultSet rs) throws SQLException {
		String username = rs.getString("username");
		String password = rs.getString("password");
		String email = rs.getString("email");

		return new UserData(username, password, email);
	}
	
	/**
	 * Fetches a UserData Object from the database
	 *
	 * @param username The username of the user to fetch
	 *
	 * @return The requested UserData object
	 */
	public UserData getUser(String username) throws DataAccessException {
		ArrayList<UserData> users = this.executeQuery(DB_SELECT_USER_STATEMENT, rs -> this.readUser(rs), username);

		// There should only ever be one user with a given username
		if (users.size() != 1) {
			throw new DataAccessException("More than one user with given username!");
		}

		return users.get(0);
	}

	/**
	 * Runs a hashing method on a password.
	 *
	 * @param password The password to encrypt
	 *
	 * @return The encrypted password
	 */
	public String encryptPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

	/** 
	 * Checks to see if a given plain text password matches a hashed one
	 *
	 * @param clearPassword The plain text password
	 * @param encryptPassword The hashed password
	 *
	 * @return true if equal, false otherwise
	 */
	public boolean checkEncryptPassword(String clearPassword, String encryptPassword) {
		   return BCrypt.checkpw(clearPassword, encryptPassword);
	}

	/**
	 * Inserts the data from a UserData object into the database
	 *
	 * @param userData The UserData object to store
	 */
	public void createUser(UserData userData) throws AlreadyTakenException, DataAccessException {
		int rowsAffected;
		rowsAffected = this.executeUpdate(DB_INSERT_USER_STATEMENT,
					userData.username(),
					userData.password(),
					userData.email());

		// if (rowsAffected == 0) {
		// 	throw new AlreadyTakenException("");
		// }
	}

	/**
	 * Clears all user data from the database.
	 */
	public void clearAllUserData() throws DataAccessException {
		this.executeStatement(DB_CLEAR_DATA_STATEMENT);
	}
}
