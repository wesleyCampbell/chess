package command;

import java.util.List;

import static ui.EscapeSequences.*;

import appstate.*;

import java.util.ArrayList;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import client.Client;
import client.exception.*;

import model.GameData;

import util.Debugger;

public class ObserveGameCommand extends CommandBase {
	private static final String COMMAND_STR = "observe";
	private static final String DESC_STR = """
		Watch a game from the chess game database.""";
	private static final String[] PARAMS = {
		"ID"
	};

	private static final String GAME_NO_EXIST_MSG = new StringBuilder()
		.append("\tGameID ")
		.append(SET_TEXT_COLOR_YELLOW)
		.append("%s")
		.append(RESET_TEXT_COLOR)
		.append(" is not valid! Please run `")
		.append(SET_TEXT_COLOR_WHITE)
		.append("list-games")
		.append(RESET_TEXT_COLOR)
		.append("` to see all current games.")
		.toString();

	private static final String INVALID_GAME_ID_MSG = GAME_NO_EXIST_MSG;

	private static final String OBSERVE_GAME_MSG = new StringBuilder()
		.append("\n\tFetching game ")
		.append(SET_TEXT_COLOR_YELLOW)
		.append("%d")
		.append(RESET_TEXT_COLOR)
		.append("...")
		.toString();

	private static final String GAME_MSG = new StringBuilder()
		.append("\n\tGame: ")
		.append(SET_TEXT_COLOR_YELLOW)
		.append("%s")
		.append(RESET_TEXT_COLOR)
		.append(": ")
		.append(SET_TEXT_COLOR_LIGHT_GREY)
		.append("White")
		.append(RESET_TEXT_COLOR)
		.append(" player: %s, ")
		.append(SET_TEXT_COLOR_LIGHT_GREY)
		.append("Black")
		.append(RESET_TEXT_COLOR)
		.append(" player: %s")
		.toString();

	public ObserveGameCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (parameters.size() != PARAMS.length) {
			this.printUsage();
			return false;
		}
		
		int gameIndex;
		try {
			gameIndex = Integer.parseInt(parameters.get(0)) - 1;
		} catch (NumberFormatException ex) {
			System.out.println("");
			System.out.println(String.format(INVALID_GAME_ID_MSG, parameters.get(0)));
			return false;
		}

		System.out.println(String.format(OBSERVE_GAME_MSG, gameIndex));

		// fetch the games cache
		ArrayList<GameData> games;
		try {
			games = new ArrayList<>(this.app.getGamesCache());
		} catch (AuthenticationException ex) {
			System.out.println(NOT_AUTH_MSG);
			return false;
		} catch (DataAccessException ex) {
			System.out.println(SERVER_ERROR_MSG);
			return false;
		}

		if (gameIndex < 0 || gameIndex >= games.size()) {
			System.out.println(String.format(GAME_NO_EXIST_MSG, gameIndex+1));
			return false;
		}

		GameData game = games.get(gameIndex);

		// Transition to the observeGameState
		this.app.changeAppState(new ObserveGameState(this.app, game, TeamColor.WHITE));
		this.app.setActiveGame(game, TeamColor.WHITE);

		String whiteUsername = game.whiteUsername() == null ? "<Empty>" : game.whiteUsername();
		String blackUsername = game.blackUsername() == null ? "<Empty>" : game.blackUsername();

		System.out.println(String.format(GAME_MSG, game.gameName(), whiteUsername, blackUsername));

		System.out.println(String.format("\tIt is %s's turn.\n", game.game().getTeamTurn()));
		this.app.printBoard(game.game(), TeamColor.WHITE);
		System.out.println("");

		return true;
	}
}

