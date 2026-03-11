package dataaccess.sqldao;

import dataaccess.DataAccessException;

import chess.ChessGame;

import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

public abstract class SQLDatabaseDAO {
	//
	// =========================== CONSTRUCTORS =========================== 
	// 
	public SQLDatabaseDAO() throws DataAccessException {
		this.initializeDatabase();
	}

	//
	// =========================== DATABASE MANIPULATION =========================== 
	// 
	
	private final String[] initStatements = {
		"""
		CREATE TABLE IF NOT EXISTS user (
			`id` int NOT NULL AUTO_INCREMENT,
			`username` varchar NOT NULL,
			`password` varchar NOT NULL,
			`email` varchar NOT NULL,
			PRIMARY KEY (`username`)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
		"""
	};
	
	private void initializeDatabase() throws DataAccessException {
			
	}

	private int executeUpdate(String statement, Object... params) throws DataAccessException {
		try (Connection conn = SQLDatabaseManager.getConnection()) {
			try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
				for (int i = 0; i < params.length; i++) {
					Object param = params[i];
					switch (param) {
						case String s -> ps.setString(i + 1, s);
						case Integer n -> ps.setInt(i + 1, n);
						case ChessGame g -> ps.setString(i + 1, g.toString());
						default -> throw new DataAccessException(
								"Unsupported database type: " + param.getClass());
					}
				}
				ps.executeUpdate();

				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					return rs.getInt(1);
				}

				return 0;
			}
		} catch (SQLException ex) {
			throw new DataAccessException(ex.getMessage());
		}
	}
}
