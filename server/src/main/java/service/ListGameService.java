package service;

import java.util.Collection;

import dataAccess.AuthDAO;
import dataAccess.AuthenticationException;
import dataAccess.GameDAO;
import dataAccess.DataAccessException;

public class ListGameService {
	//
	// ================= PUBLIC STATIC CLASSES ==================
	//
	
	public static record ListGameRequest(String authToken) {}

	public static record ListGameResult() {}
	
	//
	// ================= CONSTRUCTORS ==================
	//
	
	private AuthDAO authDAO;
	private GameDAO gameDAO;

	public ListGameService(AuthDAO authDAO, GameDAO gameDAO) {
		this.authDAO = authDAO;
		this.gameDAO = gameDAO;
	}

	//
	// ================= MEMBER METHODS ==================
	//
	
	public ListGameResult listGames(ListGameRequest request) throws AuthenticationException {
		throw new AuthenticationException("NOT IMPLEMENTED YET");
	}
}
