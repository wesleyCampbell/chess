package client;

import java.util.List;

import static ui.EscapeSequences.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import appstate.*;

import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

import client.exception.AuthenticationException;
import client.exception.DataAccessException;
import client.websocket.*;

import websocket.messages.*;
import websocket.messages.Error;
import websocket.commands.*;

import command.*;

import com.google.gson.Gson;

import model.*;
import util.Debugger;

public class Client implements NotificationHandler {
	private static final String EXIT_MSG = "\n\tGoodbye! Exiting program...\n";

	private static final String ERROR_TEXT_COLOR = SET_TEXT_COLOR_RED + SET_TEXT_BOLD;
	private static final String ERROR_HEADER_BG_COLOR = SET_BG_COLOR_RED;
	private static final String ERROR_HEADER_TEXT_COLOR = SET_TEXT_COLOR_WHITE;

	private static final String NOTIFICATION_TEXT_COLOR = SET_TEXT_COLOR_CYAN;
	private static final String NOTIFICATION_HEADER_BG_COLOR = SET_BG_COLOR_CYAN;
	private static final String NOTIFICATION_HEADER_TEXT_COLOR = SET_TEXT_COLOR_WHITE;

	private static final String GAME_TURN_MSG = new StringBuilder()
		.append("\tIt is currently ")
		.append(SET_BG_COLOR_WHITE)
		.append(SET_TEXT_COLOR_DARK_GREY)
		.append("%s")
		.append(RESET_TEXT_COLOR)
		.append(RESET_BG_COLOR)
		.append("'s turn.\n")
		.toString();

	private static final Gson GSON = new Gson();

	private BaseState appState;
	private boolean running;

	private AuthData userData;

	private ServerFacade server;
	private WebSocketFacade ws;

	private List<GameData> gamesCache;

	public static record ActiveGame(GameData game, TeamColor team) {};
	private ActiveGame activeGame;
	private GameBoardPrinter boardPrinter;
	
	public Client(String serverDomain, int serverPort) {
		appState = new PreLoginState(this);	
		this.running = true;

		this.userData = null;

		this.server = new ServerFacade(serverDomain, serverPort);
		this.ws = new WebSocketFacade(serverDomain, serverPort, this);

		this.gamesCache = null;
		this.activeGame = null;

		this.boardPrinter = new GameBoardPrinter();
	}

	public void run() {
		appState.clearScreen();
		appState.displayWelcomeScreen();
		while (this.running) {
			appState.commandPrompt();
		}

		System.out.println(EXIT_MSG);
	}

	public void exit() {
		running = false;
	}

	public BaseState getAppState() {
		return this.appState;
	}

	public void changeAppState(BaseState appState) {
		this.appState = appState;
		appState.displayWelcomeScreen();
	}

	public void setUserData(String username, String authToken) {
		this.userData = new AuthData(authToken, username);
	}

	public void clearUserData() {
		this.userData = null;
	}

	public String getUsername() {
		if (this.userData != null) {
			return this.userData.username();
		} 
		return "";
	}

	public String getAuthToken() {
		if (this.userData != null) {
			return this.userData.authToken();
		} 
		return "";
	}

	public ServerFacade getServer() {
		return this.server;
	}

	public WebSocketFacade getWebSocket() {
		return this.ws;
	}

	public List<GameData> getGamesCache() throws DataAccessException {
		if (this.gamesCache == null) {
			return this.generateGamesCache();
		}

		return this.gamesCache;
	}

	public void updateGamesCache(List<GameData> newCache) {
		this.gamesCache = newCache;
	}

	public List<GameData> generateGamesCache() throws DataAccessException {
		// Get the games from the server
		List<GameData> games = new ArrayList<>(this.server.listGames(this.getAuthToken()));

		// Sort the games based on their gameID
		games.sort(Comparator.comparingInt(GameData::getGameID));

		this.updateGamesCache(games);
		return games;
	}

	public ActiveGame getActiveGame() {
		return this.activeGame;
	}

	public void setActiveGame(GameData game, TeamColor team) {
		this.activeGame = new ActiveGame(game, team);
	}

	public void updateActiveGame() throws DataAccessException {
		// if the game is null, just return
		if (this.activeGame == null) {
			return;
		}

		List<GameData> allGames = this.generateGamesCache();

		for (GameData game : allGames) {
			// Look for the game that matches the current game
			if (game.gameID().equals(this.activeGame.game().gameID())) {
				this.activeGame = new ActiveGame(game, this.activeGame.team());	
			}
		}
	}

	public void resetActiveGame() {
		this.activeGame = null;
	}

	/**
	 * Prints a chess board onto the terminal screen.
	 *
	 * @param game The active game data struct
	 */
	public void printBoard(ActiveGame game) {
		this.printBoard(game.game().game(), game.team());
	}

	/**
	 * Prints a chess board onto the terminal screen.
	 *
	 * @param board The chess board to print
	 * @param orientation The orientation to print the game in
	 */
	public void printBoard(ChessGame game, TeamColor orientation) {
		this.boardPrinter.printBoard(game, orientation, null);	
	}

	/**
	 * Prints a chess board onto the terminal screen highlighting the valid
	 * moves from a given chess square
	 *
	 * @param board The chess board to print
	 * @param orientation The orientation to print the game in
	 * @param square The square to calculate and display the moves from
	 */
	public void printBoardValidMoves(ChessGame game, TeamColor orientation, ChessPosition square) {
		this.boardPrinter.printBoard(game, orientation, square);
	}

	/**
	 * Prints a chess board onto the terminal screen highlighting the valid
	 * moves from a given chess square
	 *
	 * @param game The game to print
	 * @param square The square to calculate and display the moves from
	 */
	public void printBoardValidMoves(ActiveGame game, ChessPosition square) {
		this.printBoardValidMoves(game.game().game(), game.team(), square);
	}

	public void manageMsg(ServerMessage msg, String origMsg) {
		switch (msg.getServerMessageType()) {
			case LOAD_GAME -> printActiveGame();
			case ERROR -> printServerError(origMsg);
			case NOTIFICATION -> printServerNotification(origMsg);
		}
	}

	public void printActiveGame(boolean printCmdHeader) {
		try {
			this.updateActiveGame();
		} catch (DataAccessException ex) {}

		if (this.activeGame != null) {
			System.out.println(ERASE_LINE);
			System.out.println(String.format(GAME_TURN_MSG, this.activeGame.game().game().getTeamTurn()));
			this.printBoard(this.activeGame);
			if (printCmdHeader) {
				this.appState.printPrompt();
			}
		}
	}

	public void printActiveGame() {
		this.printActiveGame(true);
	} 

	public void printServerError(String errorStr) {
		Error err = GSON.fromJson(errorStr, Error.class);

		StringBuilder out = new StringBuilder();

		out.append("\n\t");

		out.append(ERROR_HEADER_BG_COLOR);
		out.append(ERROR_HEADER_TEXT_COLOR);
		out.append("[ERROR]");
		out.append(RESET_BG_COLOR);

		out.append(": ");

		out.append(ERROR_TEXT_COLOR);
		out.append(err.getMsg());

		// reset text to default coloring
		out.append(RESET_TEXT_COLOR);
		out.append(RESET_TEXT_BOLD_FAINT);

		System.out.println(out.toString());

		this.appState.printPrompt();
	}

	public void printServerNotification(String notificationStr) {
		Notification notification = GSON.fromJson(notificationStr, Notification.class);

		StringBuilder output = new StringBuilder();

		output.append("\n\t");

		// colors
		output.append(NOTIFICATION_HEADER_BG_COLOR);
		output.append(NOTIFICATION_HEADER_TEXT_COLOR);

		output.append("[NOTIFICATION]");
		output.append(RESET_BG_COLOR);

		output.append(": ");

		output.append(NOTIFICATION_TEXT_COLOR);

		output.append(notification.getMsg());

		output.append(RESET_TEXT_COLOR);

		System.out.println(output.toString());

		this.appState.printPrompt();

	}
}
