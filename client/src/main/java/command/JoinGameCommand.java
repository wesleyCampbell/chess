package command;

import static ui.EscapeSequences.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import chess.ChessGame.TeamColor;
import client.Client;
import client.exception.*;
import model.GameData;
import command.exception.*;

import appstate.*;

public class JoinGameCommand extends CommandBase {
	public static record JoinGameRequest(String gameID, TeamColor playerColor) {}

	private static final String COMMAND_STR = "join";
	private static final String DESC_STR = """
		Join a chess game in the game database.""";
	private static final String[] PARAMS = {
		"ID",
		"[WHITE|BLACK]"
	};

	private static final String GAME_NO_EXIST_MSG = new StringBuilder()
		.append("\n\tGame ID ")
		.append(SET_TEXT_COLOR_YELLOW)
		.append("%s")
		.append(RESET_TEXT_COLOR)
		.append(" does not exist! Please run `")
		.append(SET_TEXT_COLOR_WHITE)
		.append("list-games")
		.append(RESET_TEXT_COLOR)
		.append("` to see all current games.\n")
		.toString();

	private static final String INVALID_TEAM_COLOR_MSG = new StringBuilder()
		.append("\tTeam Color ")
		.append(SET_TEXT_COLOR_LIGHT_GREY)
		.append("%s")
		.append(RESET_TEXT_COLOR)
		.append(" is invalid. Only ")
		.append(SET_TEXT_COLOR_LIGHT_GREY)
		.append("Black")
		.append(RESET_TEXT_COLOR)
		.append(" and ")
		.append(SET_TEXT_COLOR_LIGHT_GREY)
		.append("White")
		.append(RESET_TEXT_COLOR)
		.append(" are allowed.\n")
		.toString();

	protected static final String ALREADY_TAKEN_MSG = new StringBuilder()
		.append("\tTeam Color ")
		.append(SET_TEXT_COLOR_LIGHT_GREY)
		.append("%s")
		.append(RESET_TEXT_COLOR)
		.append(" is already taken. Please choose another game or color.\n")
		.toString();

	private static final String JOINING_GAME_MSG = new StringBuilder()
		.append("\n\t")
		.append("Joining game ")
		.append(SET_TEXT_COLOR_YELLOW)
		.append("%s")
		.append(RESET_TEXT_COLOR)
		.append(" as ")
		.append(SET_TEXT_COLOR_LIGHT_GREY)
		.append("%s")
		.append(RESET_TEXT_COLOR)
		.append("...")
		.toString();

	private static final String JOINED_GAME_MSG = new StringBuilder()
		.append("\t")
		.append("Succesfully joined game ")
		.append(SET_TEXT_COLOR_YELLOW)
		.append("%s")
		.append(RESET_TEXT_COLOR)
		.append("!\n")
		.toString();

	public JoinGameCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (parameters.size() != PARAMS.length) {
			this.printUsage();
			return false;
		}
		
		int gameIdLocal;
		try {
			gameIdLocal = Integer.parseInt(parameters.get(0)) - 1;  // Input is 1-based
		} catch (NumberFormatException ex) {
			System.out.println(String.format(GAME_NO_EXIST_MSG, parameters.get(0)));
			return false;
		}

		String teamColorStr = parameters.get(1);

		// Verify that the teamColor is valid
		TeamColor teamColor;
		try {
			teamColor = this.parseTeamColor(teamColorStr);
		} catch (InvalidParameterException ex) {
			System.out.println("");
			System.out.println(String.format(INVALID_TEAM_COLOR_MSG, teamColorStr));
			return false;
		}

		// Check to see if we already have a game cache.
		List<GameData> games;
		try {
			games = this.app.getGamesCache();
		} catch (AuthenticationException ex) {
			System.out.println(NOT_AUTH_MSG);
			return false;
		} catch (DataAccessException ex) {
			System.out.println(SERVER_ERROR_MSG);
			return false;
		}

		// Get the specific game that we want
		GameData game;
		try {
			game = games.get(gameIdLocal);
		} catch (IndexOutOfBoundsException ex) {
			System.out.println(String.format(GAME_NO_EXIST_MSG, gameIdLocal+1));
			return false;
		}

		System.out.println(String.format(JOINING_GAME_MSG, game.gameName(), teamColor));

		try {
			String authToken = this.app.getAuthToken();
			String gameID = game.gameID();

			this.app.getServer().joinGame(this.app.getAuthToken(), game.gameID(), teamColor);
			this.app.getWebSocket().connect(authToken, Integer.parseInt(gameID));
		} catch (AuthenticationException ex) {
			System.out.println(NOT_AUTH_MSG);
			return false;
		} catch (AlreadyTakenException ex) {
			System.out.println(String.format(ALREADY_TAKEN_MSG, teamColor));
			return false;
		} catch (DataAccessException | IOException ex) {
			System.out.println(SERVER_ERROR_MSG);
			return false;
		}

		System.out.println(String.format(JOINED_GAME_MSG, game.gameName()));

		this.app.setActiveGame(game, teamColor);
		this.app.changeAppState(new GameplayState(this.app, game, teamColor));

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

