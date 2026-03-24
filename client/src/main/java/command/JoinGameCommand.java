package command;

import java.util.ArrayList;
import java.util.List;

import chess.ChessGame.TeamColor;
import client.Client;
import client.exception.*;
import model.GameData;
import command.exception.*;

public class JoinGameCommand extends CommandBase {
	public static record JoinGameRequest(String gameID, TeamColor playerColor) {}

	private static final String COMMAND_STR = "join";
	private static final String DESC_STR = """
		Join a chess game in the game database.""";
	private static final String[] PARAMS = {
		"ID",
		"[WHITE|BLACK]"
	};

	private static final String GAME_NO_EXIST_MSG = """
		Game ID '%s' does not exist! Please run `list-games` to see all current games.""";
	private static final String INVALID_TEAM_COLOR_MSG = """
		Team Color '%s' is invalid. Only 'Black' and 'White' are allowed.""";
	private static final String ALREADY_TAKEN_MSG = """
		Team Color '%s' is already taken. Please choose another game or color.""";

	public JoinGameCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (parameters.size() != PARAMS.length) {
			this.printUsage();
			return false;
		}
		
		int gameIdLocal = Integer.parseInt(parameters.get(0)) - 1;  // Input is 1-based
		String teamColorStr = parameters.get(1);

		// Verify that the teamColor is valid
		TeamColor teamColor;
		try {
			teamColor = this.parseTeamColor(teamColorStr);
		} catch (InvalidParameterException ex) {
			System.out.println(String.format(INVALID_TEAM_COLOR_MSG, teamColorStr));
			return false;
		}

		// Check to see if we already have a game cache.
		List<GameData> games = this.app.getGamesCache();
		if (games == null) {
			// If we don't we have to download it.
			try {
				games = this.app.generateGamesCache();
			} catch (AuthenticationException ex) {
				System.out.println(NOT_AUTH_MSG);
				return false;
			} catch (DataAccessException ex) {
				System.out.println(SERVER_ERROR_MSG);
				return false;
			}
		}

		// Get the specific game that we want
		GameData game;
		try {
			game = games.get(gameIdLocal);
		} catch (IndexOutOfBoundsException ex) {
			System.out.println(String.format(GAME_NO_EXIST_MSG, gameIdLocal));
			return false;
		}

		System.out.println(String.format(
					"Joining game %s as %s",
					game.gameName(), teamColor));

		try {
			this.app.getServer().joinGame(this.app.getAuthToken(), game.gameID(), teamColor);
		} catch (AuthenticationException ex) {
			System.out.println(NOT_AUTH_MSG);
			return false;
		} catch (AlreadyTakenException ex) {
			System.out.println(String.format(ALREADY_TAKEN_MSG, teamColor));
			return false;
		} catch (DataAccessException ex) {
			System.out.println(SERVER_ERROR_MSG);
			return false;
		}

		return true;
	}

	public TeamColor parseTeamColor(String color) throws InvalidParameterException {
		switch (color.toLowerCase()) {
			case "white":
				return TeamColor.WHITE;
			case "black":
				return TeamColor.BLACK;
			default:
				throw new InvalidParameterException(String.format("Team Color %s is invalid", color));
		}
	}
}

