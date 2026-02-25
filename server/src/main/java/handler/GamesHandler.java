package handler;

import service.CreateGameService;
import service.CreateGameService.CreateGameRequest;
import service.CreateGameService.CreateGameResult;

import service.JoinGameService;
import service.JoinGameService.JoinGameRequest;
import service.JoinGameService.JoinGameResult;

import service.ListGameService;
import service.ListGameService.ListGameRequest;
import service.ListGameService.ListGameResult;

import dataAccess.*;
import io.javalin.http.Context;

public class GamesHandler extends Handler {

	//
	// =============================== CONSTRUCTORS ============================
	//
	
	private CreateGameService createGameService;
	private JoinGameService joinGameService;
	private ListGameService listGameService;

	public GamesHandler(AuthDAO authDAO, GameDAO gameDAO) {
		this.createGameService = new CreateGameService(authDAO, gameDAO);
		this.joinGameService = new JoinGameService(authDAO, gameDAO);
		this.listGameService = new ListGameService(authDAO, gameDAO);
	}

	//
	// ================================ MEMBER METHODS ==========================
	//
	
	/** Takes a JSON createGame request and parses it into the data needed by the CreateGameService.
	 *  Then makes the service request and returns a JSON version of the result.
	 *
	 *  @param requestStr The JSON string. It must be in the format '{"authToken": "", "gameName": ""}'.
	 *  	The easiest way to do this is to simply make a CreateGameService.CreateGameRequest object and parse it as GSON.
	 */
	public String createGameRequest(String requestStr) {
		// Deserialize the request into a format understood by the service
		CreateGameRequest request = fromJson(requestStr, CreateGameRequest.class);

		CreateGameResult result;
		try {
			result = this.createGameService.createGame(request);
		} 
		// User is not authenticated
		catch (AuthenticationException ex) {
			return this.unauthorizedHTTPMsg;
		}

		// Return the expected result
		return toJsonWithHTTPCode(result, HTTP_CODE_OK);
	}

	public void createGameRequest(Context ctx) {

	}

	/**
	 * Takes a JSON joinGame request and parses it into the data needed by the JoinGameService.
	 * Then makes the service request and returns a JSON serialization of the result.
	 *
	 * @param requestStr The JSON string. It must be in the format '"authToken": "", "playerColor": "", "gameID": ""}'. 
	 * 		The easiest way to do this is to simply make a JoinGameService.JoinGameRequest object and parse it as GSON.
	 *
	 * @return The String JSON result
	 */
	public String joinGameRequest(String requestStr) {
		// Deserializes the request into a format understood by the service
		JoinGameRequest	request = fromJson(requestStr, JoinGameRequest.class); 

		JoinGameResult result;
		try {
			result = this.joinGameService.joinGame(request);
		} 
		// In this context, a DataAccessException means that the team color is already taken
		catch (DataAccessException ex) {
			HTTPCodeRequest outMsg = new HTTPCodeRequest(HTTP_CODE_TAKEN, "Error: team color already taken");
			return toJson(outMsg);
		} 
		// The user isn't authenticated
		catch (AuthenticationException ex) {
			return this.unauthorizedHTTPMsg;
		}

		return this.successHTTPMsg;
	}

	public void joinGameRequest(Context ctx) {

	}

	/**
	 * Takes a JSON listGame request and parses it into the data needed by the ListGamesService.
	 * Then makes the service request and returns a JSON serialization of the result.
	 *
	 * @param requestStr The JSON request string. It must be in the format '{"authToken":""}'.
	 * 			The easiest way to do this is simply make a ListGameRequest object and parse it as json.
	 *
	 * @return The String JSON result
	 */
	public String listGameRequest(String requestStr) {
		ListGameRequest request = fromJson(requestStr, ListGameRequest.class);

		ListGameResult result;
		try {
			result = this.listGameService.listGames(request);
		} 
		// The user isn't authenticated
		catch (AuthenticationException ex) {
			return unauthorizedHTTPMsg;
		}

		// Return the expected result and HTTP code
		return toJsonWithHTTPCode(result, HTTP_CODE_OK);
	}

	public void listGameRequest(Context ctx) {

	}
}
