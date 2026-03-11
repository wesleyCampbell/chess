package dataaccess.sqldao;

import dataaccess.DataAccessException;
import util.Debugger;
import chess.ChessGame;

import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import java.util.ArrayList;

public abstract class SQLDatabaseDAO {
	//
	// =========================== CONSTRUCTORS =========================== 
	// 

	protected SQLDatabaseDAO(final String initStatement) throws DataAccessException {
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
	protected void initializeDatabase(final String initStatement) throws DataAccessException {
		// Opens the SQL connection
		try (Connection conn = SQLDatabaseManager.getConnection()) {
			// Formats the SQL statement
			try (PreparedStatement ps = conn.prepareStatement(initStatement)) {
				ps.executeUpdate();
			}
		} catch (SQLException ex) {
			throw new DataAccessException(ex.getMessage());
		}

	}

	/**
	 * Will make an arbitrary SQL statement
	 *
	 * @param statement The SQL statement to make
	 */
	protected void executeStatement(final String statement) throws DataAccessException {
		// Opens the SQL connection
		try (Connection conn = SQLDatabaseManager.getConnection()) {
			// Formats the SQL statement
			try (PreparedStatement ps = conn.prepareStatement(statement)) {
				ps.execute();
			}
		} catch (SQLException ex) {
			throw new DataAccessException(ex.getMessage());
		}
	}

	/**
	 * Helper function that throws an unsupported data type message into an exception.
	 *
	 * @param type The unsupported data type
	 */
	private <T> void throwUnsupportedDBType(Class<T> type) throws DataAccessException {
		throw new DataAccessException("Unsupported database type: " + type.toString());
	}

	/*
	 * Allows a function to be passed into the executeQuery method.
	 * Maps a ResultSet result to a given object.
	 */
	@FunctionalInterface
	public interface RowMapper<T> {
		T mapRow(ResultSet rs) throws SQLException;
	}

	/** 
	 * Will execute a SQL query with arbritrary parameters
	 *
	 * @param statement The SQL statement
	 * @return mapper The function that maps the output of the ResultSet to a given object
	 * @param params Arbitrary object parameters
	 *
	 * @return An ArrayList of the desired objects
	 */
	protected <T> ArrayList<T> executeQuery(final String statement, RowMapper<T> mapper, Object... params) throws DataAccessException {
		ArrayList<T> results = new ArrayList<>();
		// Open the SQL connection
		try (Connection conn = SQLDatabaseManager.getConnection()) {
			// Format the SQL statement
			try (PreparedStatement ps = conn.prepareStatement(statement)) {
				for (int i = 0; i < params.length; i++) {
					Object param = params[i];
					switch (param) {
						case String s -> ps.setString(i + 1, s);
						case Integer n -> ps.setInt(i + 1, n);
						case ChessGame g -> ps.setString(i + 1, g.toString());
						default -> throwUnsupportedDBType(param.getClass());
					}
				}
				// Executes the query and puts each match in the results array
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						results.add(mapper.mapRow(rs));
					}
				}

				return results;
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
	protected int executeUpdate(final String statement, Object... params) throws DataAccessException {
		try (Connection conn = SQLDatabaseManager.getConnection()) {
			try (PreparedStatement ps = conn.prepareStatement(statement)) {
				for (int i = 0; i < params.length; i++) {
					Object param = params[i];
					switch (param) {
						case String s -> ps.setString(i + 1, s);
						case Integer n -> ps.setInt(i + 1, n);
						case ChessGame g -> ps.setString(i + 1, g.toString());
						default -> throwUnsupportedDBType(param.getClass());
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
