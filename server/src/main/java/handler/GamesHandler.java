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

//import static util.Debugger.debug;
import static util.Debugger.debug;

import model.GameData;

import dataAccess.*;
import io.javalin.http.Context;
import java.util.Collection;
import java.util.HashSet;

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
	
	/**
	 * Translates a HTTP request context object into a createGameRequest that the CreateGameService
	 * can understand. Makes the reqeust and returns the result in the context result.
	 *
	 * @param ctx The javalin HTTP context
	 *
	 * @return True if create game was a success, false otherwise
	 */
	public boolean createGameRequest(Context ctx) {
		String authToken = ctx.header(HTTP_HEADER_AUTH);
		String jsonRequest = this.addAuthTokenJsonProperty(ctx.body(), authToken);

		// CreateGameRequest request = fromJson(jsonRequest, CreateGameRequest.class);
		CreateGameRequest request = extractJsonRequest(jsonRequest, CreateGameRequest.class);
		if (request == null) {
			ctx.status(HTTP_CODE_ERROR);
			ctx.result(this.errorHTTPMsg);
			return false;
		}

		ctx.contentType("application/json");
		debug(String.format("request: %s", request), 0);

		CreateGameResult result;
		try {
			result = this.createGameService.createGame(request);
		}
		catch (AuthenticationException ex) {
			ctx.status(HTTP_CODE_UNAUTH);
			ctx.result(this.unauthorizedHTTPMsg);
			return false;
		} 

		ctx.status(HTTP_CODE_OK);
		ctx.result(toJson(result));
		return true;
	}

	/**
	 * Takes a HTTP request to join a game and translates it into a request the JoinGameService can understand
	 *
	 * @param ctx: The Javalin HTTP context
	 *
	 * @return True if request is succesfull, false otherwise
	 */
	public boolean joinGameRequest(Context ctx) {
		String authToken = ctx.header(HTTP_HEADER_AUTH);

		String jsonRequest = this.addAuthTokenJsonProperty(ctx.body(), authToken);

		// JoinGameRequest request = fromJson(jsonRequest, JoinGameRequest.class);
		JoinGameRequest request = extractJsonRequest(jsonRequest, JoinGameRequest.class);
		if (request == null) {
			ctx.status(HTTP_CODE_ERROR);
			ctx.result(this.errorHTTPMsg);
			return false;
		}

		debug(String.format("request: %s", request), 0);

		ctx.contentType("application/json");

		JoinGameResult result;
		try {
			result = this.joinGameService.joinGame(request);
		} catch (AuthenticationException ex) {
			ctx.status(HTTP_CODE_UNAUTH);
			ctx.result(this.unauthorizedHTTPMsg);
			return false;
		} catch (DataAccessException ex) {
			ctx.status(HTTP_CODE_TAKEN);
			ctx.result(this.takenHTTPMsg);
			return false;
		}

		ctx.status(HTTP_CODE_OK);
		ctx.result(toJson(result));
		return true;
	}

	/** Takes a listGameResult and replaces all empty strings with null.
	 *
	 * @param result
	 */
	private ListGameResult formatGames(ListGameResult result) {
		Collection<GameData> newGames = new HashSet<>();

		for (GameData game : result.games()) {
			String blackUsername = game.blackUsername();
			String whiteUsername = game.whiteUsername();

			if (blackUsername.isEmpty()) {
				blackUsername = null;				
			}
			if (whiteUsername.isEmpty()) {
				whiteUsername = null;
			}

			GameData newGame;
			if (whiteUsername == null || blackUsername == null) {
				newGame = new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), game.game());
			} else {
				newGame = game;
			}

			newGames.add(newGame);
		}

		return new ListGameResult(newGames);	
	}

	/**
	 * Takes a HTTP json request and translates it into a format that the ListGamesService can understand. Makes the request and puts the response in a Javalin context result.
	 *
	 * @param ctx Javalin HTTP context
	 *
	 * @return True if listGame request successfull, false otherwise
	 */
	public boolean listGameRequest(Context ctx) {
		String authToken = ctx.header(HTTP_HEADER_AUTH);
		ListGameRequest request = new ListGameRequest(authToken);

		ctx.contentType("application/json");
		debug(String.format("request: %s", request));

		ListGameResult result;
		try {
			result = this.listGameService.listGames(request);
		} catch (AuthenticationException ex) {
			ctx.status(HTTP_CODE_UNAUTH);
			ctx.result(this.unauthorizedHTTPMsg);
			return false;
		}

		ctx.status(HTTP_CODE_OK);
		ctx.result(toJson(formatGames(result)));
		return true;
	}
}
