package dataaccess.sqldao;

import dataaccess.DataAccessException;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLDatabaseManager {
	private static final String databaseName;
	private static final String user;
	private static final String password;
	private static final String connectionURL;

	/*
	 * Loads the database information from db.properties, which it will find
	 * somewhere in the classpath
	 */
	static {
		try (InputStream in = SQLDatabaseManager.class.getClassLoader().getResourceAsStream("db.properties")) {
			// Load the properties from the config file
			Properties properties = new Properties();	
			properties.load(in);

			databaseName = properties.getProperty("db.name");
			user = properties.getProperty("db.user");
			password = properties.getProperty("db.password");

			String host = properties.getProperty("db.host");
			String port = properties.getProperty("db.port");
			//var port = Integer.parseInt(properties.getProperty("db.port"));
			connectionURL = String.format("jdbc:mysql://%s:%s", host, port);
		}
		catch (Exception ex) {
			throw new RuntimeException("Unable to read db.properties: " + ex.getMessage());
		}
	}

	/*
	 * Creates a database if it does not exist already
	 */
	public static void createDatabase() throws DataAccessException {
		try {
			String statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
			Connection conn = DriverManager.getConnection(connectionURL, user, password);
			try (PreparedStatement preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
				preparedStatement.executeUpdate();
			}
		} catch (SQLException ex) {
			throw new DataAccessException(ex.getMessage());
		}
	}

	/**
	 * Will make and return a connection to a database.
	 */
	public static Connection getConnection() throws DataAccessException {
		try {
			Connection conn = DriverManager.getConnection(connectionURL, user, password);
			conn.setCatalog(databaseName);
			return conn;
		} catch (SQLException ex) {
			throw new DataAccessException(ex.getMessage()); 
		}
	}
}
