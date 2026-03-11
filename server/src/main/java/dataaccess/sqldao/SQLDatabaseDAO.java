package dataaccess.sqldao;

import dataaccess.DataAccessException;

import chess.ChessGame;

import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

public abstract class SQLDatabaseDAO {
	//
	// =========================== CONSTRUCTORS =========================== 
	// 
	public SQLDatabaseDAO(String initStatement) throws DataAccessException {
		this.initializeDatabase(initStatement);
	}

	//
	// =========================== DATABASE MANIPULATION =========================== 
	// 
	
	/**
	 * Will take a block string containing statements to init a database
	 * 
	 * @param initStatement The db create statements.
	 */
	private void initializeDatabase(String initStatement) throws DataAccessException {
		try (Connection conn = SQLDatabaseManager.getConnection()) {
			try (PreparedStatement ps = conn.prepareStatement(initStatement)) {
				ps.executeUpdate();
			}
		} catch (SQLException ex) {
			throw new DataAccessException(ex.getMessage());
		}

	}

	/**
	 * Will take a SQL statement and a set of object parameters and will format the statement and 
	 * submit it to the SQL database. 
	 *
	 * @param statement The SQL statement
	 * @params params Arbitrary objects to pass into the statement. Recognized data tyes include
	 * Strings, Integers, and ChessGames
	 *
	 * @return int status code
	 */
	private int executeUpdate(String statement, Object... params) throws DataAccessException {
		try (Connection conn = SQLDatabaseManager.getConnection()) {
			try (PreparedStatement ps = conn.prepareStatement(statement)) {
				for (int i = 0; i < params.length; i++) {
					Object param = params[i];
					switch (param) {
						case String s -> ps.setString(i + 1, s);
						case Integer n -> ps.setInt(i + 1, n);
						case ChessGame g -> ps.setString(i + 1, g.toString());
						default -> throw new DataAccessException("Unsupported database type: " + param.getClass());
					}
				}
				ps.executeUpdate();

				return 0;
			}
		} catch (SQLException ex) {
			throw new DataAccessException(ex.getMessage());
		}
	}
}
