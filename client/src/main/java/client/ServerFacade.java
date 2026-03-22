package client;

import java.util.Collection;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse.BodyHandlers;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.util.Locale;

import com.google.gson.Gson;

import chess.ChessGame.TeamColor;
import model.*;
import util.Debugger;
import client.exception.*;

import command.ListGameCommand.ListGameResult;

public class ServerFacade {

	private static final HttpClient httpClient = HttpClient.newHttpClient();
	private static final Gson gson = new Gson();

	private static final String SERVER_DOMAIN = "localhost";
	private static final int SERVER_PORT = 8080;
	private static final String SERVER_ADDR = String.format("http://%s:%d", SERVER_DOMAIN, SERVER_PORT);

	private static final int HTTP_CODE_OK = 200;
	private static final int HTTP_CODE_BAD_REQUEST = 400;
	private static final int HTTP_CODE_UNAUTH = 401;
	private static final int HTTP_CODE_TAKEN = 403;
	private static final int HTTP_CODE_NOT_FOUND = 404;
	private static final int HTTP_CODE_INT_ERROR = 500;

	private static final String POST = "POST";
	private static final String GET = "GET";
	private static final String PUT = "PUT";
	private static final String DELETE = "DELETE";

	private static final String USER_END_PNT = "/user";
	private static final String GAME_END_PNT = "/game";
	private static final String SESSION_END_PNT = "/session";

	private BodyPublisher requestBodyPublisher(String body) throws IOException {
		if (body == null) {
			return BodyPublishers.noBody();
		} else {
			return BodyPublishers.ofString(body);
		}
	}

	private HttpResponse<String> sendHttpRequest(String url, String method, String body, String authToken) throws DataAccessException {
		try {
			HttpRequest request = HttpRequest.newBuilder(URI.create(url))
				.method(method, requestBodyPublisher(body))
				.header("Authorization", authToken)
				.build();

			return httpClient.send(request, BodyHandlers.ofString());
		} catch (IOException ex) {
			throw new DataAccessException("Internal Error");
		} catch (InterruptedException ex) {
			throw new ConnectionException("Server connection error");
		}
	}

	private HttpResponse<String> sendHttpRequest(String url, String method, String body) throws DataAccessException {
		return this.sendHttpRequest(url, method, body, "");
	}

	private <T> T readHttpResponse(HttpResponse<String> response, Class<T> type) throws DataAccessException {
		int httpCode = response.statusCode();
		switch (httpCode) {
			case HTTP_CODE_OK:
				break;
			case HTTP_CODE_UNAUTH:
				throw new AuthenticationException("Not authenticated");
			case HTTP_CODE_TAKEN:
				throw new AlreadyTakenException("Username already taken");
			case HTTP_CODE_NOT_FOUND:
			case HTTP_CODE_BAD_REQUEST:  // fall-through
			case HTTP_CODE_INT_ERROR:
				throw new ConnectionException("Server connection error");
		}

		if (type == null) return null;

		T body = gson.fromJson(response.body(), type);
		return body;
	}

	public AuthData register(String username, String password, String email) throws DataAccessException {
		String urlStr = SERVER_ADDR + USER_END_PNT;

		String userData = gson.toJson(new UserData(username, password, email));

		// Make the HTTP request
		HttpResponse<String> response = this.sendHttpRequest(urlStr, POST, userData);

		// Read the HTTP response
		AuthData authData = readHttpResponse(response, AuthData.class);
		return authData;
	}

	public AuthData login(String username, String password) throws DataAccessException {
		String urlStr = SERVER_ADDR + SESSION_END_PNT;
		String userData = gson.toJson(new UserData(username, password, ""));

		HttpResponse<String> response = this.sendHttpRequest(urlStr, POST, userData);

		AuthData authData = readHttpResponse(response, AuthData.class);

		return authData;

	}
	
	public void logout(String authToken) throws DataAccessException {
		String urlStr = SERVER_ADDR + SESSION_END_PNT;
		String body = gson.toJson("");

		HttpResponse<String> response = this.sendHttpRequest(urlStr, DELETE, body, authToken);

		readHttpResponse(response, null);
	}
	
	public String createGame(String authToken, String gameName) throws DataAccessException {
		String urlStr = SERVER_ADDR + GAME_END_PNT;
		String gameData = gson.toJson(new GameData(null, null, null, gameName, null));

		HttpResponse<String> response = this.sendHttpRequest(urlStr, POST, gameData, authToken);
		GameData game = this.readHttpResponse(response, GameData.class);

		System.out.println(game);

		return game.gameID();
	}
	
	public Collection<GameData> listGames(String authToken) throws DataAccessException {
		String urlStr = SERVER_ADDR + GAME_END_PNT;
		String body = "";

		HttpResponse<String> response = this.sendHttpRequest(urlStr, GET, body, authToken);

		ListGameResult games = this.readHttpResponse(response, ListGameResult.class);

		return games.games();
	}

	// public boolean joinGame(String authToken, String gameID, TeamColor teamColor) {
	//
	// }
	//
	// public GameData getGame(String authToken, String gameID) {
	//
	// }
}
