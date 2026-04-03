package client;

import model.*;

import static ui.EscapeSequences.*;

import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

public class GameBoardPrinter {
	private static final String COL_BOARD_BORDER = SET_BG_COLOR_DARK_GREY;
	private static final String COL_BORDER_TEXT = SET_TEXT_COLOR_LIGHT_GREY;

	private static final String COL_WHITE_SQUARE = SET_BG_COLOR_OFF_WHITE;
	private static final String COL_BLACK_SQUARE = SET_BG_COLOR_DARK_GREEN;

	private static final String COL_WHITE_PIECE = SET_TEXT_COLOR_WHITE;
	private static final String COL_BLACK_PIECE = SET_TEXT_COLOR_BLACK;

	private static final String MOVE_SQUARE_END_HIGHLIGHT_BG_COLOR = SET_BG_COLOR_GREEN;
	private static final String MOVE_SQUARE_START_HIGHLIGHT_BG_COLOR = SET_BG_COLOR_YELLOW;

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
	 * Helper function to printBoard()
	 *
	 * based on a team color will print that pieces background color
	 *
	 * @param boardColor The color of the square
	 * @param pieceType The character to print
	 * @parm pieceColor The color of the piece (if applicable);
	 * @param highLightColor If not null, represents a colored background.
	 */
	private void printBoardSquare(TeamColor boardColor, PieceType pieceType, TeamColor pieceColor, String highlightColor) {
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

		if (highlightColor != null) {
			square.append(highlightColor);
		}
		square.append(RESET_BG_COLOR);
		square.append(RESET_TEXT_COLOR);

		System.out.print(square.toString());
	}

	/**
	 * Given a chess square and a board, will calculate and return all the valid move 
	 * squares.
	 *
	 * @param board The chess board to read
	 * @param square The square from which to calculate moves
	 *
	 * @return A list containing all valid moves
	 */
	private Set<ChessPosition> calculateMoveSquares(ChessGame game, ChessPosition square) {
		Set<ChessPosition> moveSquares = new HashSet<>();

		// If the square is null, there are no moves to calculate
		if (square != null) {
			Collection<ChessMove> validMoves = game.validMoves(square);
			moveSquares.addAll(ChessMove.extractEndPositions(validMoves));
		}

		return moveSquares;
	}


	/**
	 * Prints a chess board onto the terminal screen.
	 * Quick and dirty to meet deadlines.
	 *
	 * @param game The chess game to print
	 * @param orientation The team to put on the bottom of the screen
	 * @param square If not null, will highlight the valid moves from this square.
	 */
	public void printBoard(ChessGame game, TeamColor orientation, ChessPosition square) {
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

		ChessBoard board = game.getBoard();

		// calculate the valid moves
		Set<ChessPosition> moveSquares = this.calculateMoveSquares(game, square);

		System.out.print(SET_TEXT_BOLD);

		System.out.print("\t");
		printColHeaders(end, start);

		TeamColor curColor = TeamColor.WHITE;

		for (int row = start; row != end + inc; row += inc) {
			// The board boarder with row value
			System.out.print("\t");
			printBorderSquare(Integer.toString(row));


			for (int col = end; col != start - inc; col -= inc) {
				ChessPosition curPos = new ChessPosition(row, col);
				ChessPiece piece = board.getPiece(curPos);							

				String highlightColor = null;	
				if (moveSquares.contains(curPos)) {
					highlightColor = MOVE_SQUARE_END_HIGHLIGHT_BG_COLOR;
				} else if (curPos.equals(square)) {
					highlightColor = MOVE_SQUARE_START_HIGHLIGHT_BG_COLOR;	
				}


				if (piece == null) {
					this.printBoardSquare(curColor, null, null, highlightColor);
				} else {
					this.printBoardSquare(curColor, piece.getPieceType(), piece.getTeamColor(), highlightColor);
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
