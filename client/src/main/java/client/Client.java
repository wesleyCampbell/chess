package client;

import java.util.List;

import static ui.EscapeSequences.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import appstate.*;
import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;
import client.exception.AuthenticationException;
import client.exception.DataAccessException;
import command.*;

import model.*;
import util.Debugger;

public class Client {
	private static final String EXIT_MSG = "\n\tGoodbye! Exiting program...\n";

	private static final String COL_BOARD_BORDER = SET_BG_COLOR_DARK_GREY;
	private static final String COL_BORDER_TEXT = SET_TEXT_COLOR_LIGHT_GREY;

	private static final String COL_WHITE_SQUARE = SET_BG_COLOR_OFF_WHITE;
	private static final String COL_BLACK_SQUARE = SET_BG_COLOR_DARK_GREEN;

	private static final String COL_WHITE_PIECE = SET_TEXT_COLOR_WHITE;
	private static final String COL_BLACK_PIECE = SET_TEXT_COLOR_BLACK;

	private BaseState appState;
	private boolean running;

	private AuthData userData;

	private ServerFacade server;

	private List<GameData> gamesCache;
	
	public Client(String serverDomain, int serverPort) {
		appState = new PreLoginState(this);	
		this.running = true;

		this.userData = null;

		this.server = new ServerFacade(serverDomain, serverPort);

		this.gamesCache = null;
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

	/**
	 * Helper function to print the column headers on a chessboard
	 * in the terminal
	 *
	 * @param start The index to start at
	 * @param end The index to end at
	 */
	private void printColHeaders(int start, int end) {
		int inc;
		if (start > end) {
			inc = -1;
		}
		else {
			inc = 1;
		}

		StringBuilder row = new StringBuilder();
		row.append(COL_BOARD_BORDER);
		row.append(COL_BORDER_TEXT);
		row.append(EMPTY);

		for (int i = start; i != end + inc; i += inc) {
			row.append(" ");
			row.append(ChessBoard.COL_VALUES.get(i));
			row.append(" ");
		}

		row.append(EMPTY);

		row.append(RESET_BG_COLOR);
		row.append(RESET_TEXT_COLOR);
		
		System.out.println(row.toString());
	}

	private TeamColor toggleTeamColor(TeamColor color) {
		switch (color) {
			case WHITE:
				return TeamColor.BLACK;
			case BLACK:
				return TeamColor.WHITE;
			default:
				return null;
		}
	}

	/**
	 * Helper function for printBoard().
	 * Prints a border square.
	 *
	 * @param value The string value to put
	 */
	private void printBorderSquare(String value) {
		StringBuilder square = new StringBuilder();
		square.append(COL_BOARD_BORDER);
		square.append(COL_BORDER_TEXT);
		square.append(" ");
		square.append(value);
		square.append(" ");
		square.append(RESET_BG_COLOR);
		square.append(RESET_TEXT_COLOR);
		System.out.print(square.toString());
	}

	/**
	 * Helper function to printBoard()
	 *
	 * based on a team color will print that pieces background color
	 *
	 * @param boardColor The color of the square
	 * @param pieceType The character to print
	 * @parm pieceColor The color of the piece (if applicable);
	 */
	private void printBoardSquare(TeamColor boardColor, PieceType pieceType, TeamColor pieceColor) {
		StringBuilder square = new StringBuilder();
		switch (boardColor) {
			case WHITE:
				square.append(COL_WHITE_SQUARE);
				break;
			case BLACK:
				square.append(COL_BLACK_SQUARE);
				break;
		}

		int pieceColorIndex;
		if (pieceColor != null) {
			switch (pieceColor) {
				case WHITE:
					square.append(COL_WHITE_PIECE);
					pieceColorIndex = 0;
					break;
				case BLACK:
					square.append(COL_BLACK_PIECE);
					pieceColorIndex = 1;
					break;
				default:
					pieceColorIndex = 0;
					break;
			}
		} else {
			pieceColorIndex = 0;
		}

		// black pieces look better haha
		pieceColorIndex = 1;
		if (pieceType != null) {
			switch (pieceType) {
				case PAWN:
					square.append(PAWNS[pieceColorIndex]);
					break;
				case KNIGHT:
					square.append(KNIGHTS[pieceColorIndex]);
					break;
				case BISHOP:
					square.append(BISHOPS[pieceColorIndex]);
					break;
				case ROOK:
					square.append(ROOKS[pieceColorIndex]);
					break;
				case QUEEN:
					square.append(QUEENS[pieceColorIndex]);
					break;
				case KING:
					square.append(KINGS[pieceColorIndex]);
					break;
				default:
					square.append(EMPTY);
					break;
			}
		} else {
			square.append(EMPTY);
		}

		square.append(RESET_BG_COLOR);
		square.append(RESET_TEXT_COLOR);

		System.out.print(square.toString());
	}

	/**
	 * Prints a chess board onto the terminal screen.
	 * Quick and dirty to meet deadlines.
	 *
	 * @param board The chess board to print
	 * @param orientation The team to put on the bottom of the screen
	 */
	public void printBoard(ChessBoard board, TeamColor orientation) {
		int start, end, inc;
		if (orientation == TeamColor.WHITE) {
			start = 8;
			end = 1;
			inc = -1;
		} else {
			start = 1;
			end = 8;
			inc = 1;
		}


		System.out.print(SET_TEXT_BOLD);

		System.out.print("\t");
		printColHeaders(end, start);

		TeamColor curColor = TeamColor.WHITE;

		for (int row = start; row != end + inc; row += inc) {
			// The board boarder with row value
			System.out.print("\t");
			printBorderSquare(Integer.toString(row));

			for (int col = end; col != start - inc; col -= inc) {
				ChessPiece piece = board.getPiece(new ChessPosition(row, col));							
				if (piece == null) {
					this.printBoardSquare(curColor, null, null);
				} else {
					this.printBoardSquare(curColor, piece.getPieceType(), piece.getTeamColor());
				}

				curColor = this.toggleTeamColor(curColor);
			}

			// The board boarder with row value
			printBorderSquare(Integer.toString(row));

			curColor = this.toggleTeamColor(curColor);

			System.out.println("");
		}

		System.out.print("\t");
		printColHeaders(end, start);

		System.out.print(RESET_TEXT_BOLD_FAINT);
	}
}
