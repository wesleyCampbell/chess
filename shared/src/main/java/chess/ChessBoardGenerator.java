package chess;

import chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor;

/**
 * A class used for generating new boards based off of string configurations.
 */
public class ChessBoardGenerator {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	public static final String[] STANDARD_BOARD_STATE = {
		"RNBQKBNR",
		"PPPPPPPP",
		"--------",
		"--------",
		"--------",
		"--------",
		"PPPPPPPP",
		"RNBQKBNR"
	};

	//
	// ======================== MEMBER ATTRIBUTES =======================
	//
	
	private String[] standardState;

	//
	// ======================== CONSTRUCTORS =======================
	//
	//
	
	/**
	 * Default constructor. Sets the standard state to the predefined standard.
	 */
	public ChessBoardGenerator() {
		this(ChessBoardGenerator.STANDARD_BOARD_STATE);

	}

	/**
	 * Constructor that allows custom standard state.
	 *
	 * @param standardState String array portraying a board state. For reference, 
	 * see ChessBoardGenerator.STANDARD_BOARD_STATE
	 */
	public ChessBoardGenerator(String[] standardState) {
		this.standardState = standardState;
	}

	//
	// ======================== MEMBER METHODS =======================
	//

	/**
	 * Generates a completely empty board of provided dimentions.
	 *
	 * @param rowNum the number of rows the board is to have.
	 * @param colNum The number of columns the board is to have.
	 *
	 * @return A 2D array of ChessPieces, all initialized to null.
	 */
	public ChessPiece[][] generateEmptyBoard(int rowNum, int colNum) {
		return new ChessPiece[rowNum][colNum];
	}

	/**
	 * Generates a new board based on an input state blueprint
	 *
	 * @param state An array of strings outlining the new board state
	 *
	 * @return The new ChessBoard array
	 */
	public ChessPiece[][] generateBoard(String[] state) {
		int rowNum = state.length;
		int colNum = state[0].length();

		ChessPiece[][] newBoard = new ChessPiece[rowNum][colNum];

		TeamColor pieceColor = TeamColor.WHITE;
		for (int row = 0; row < rowNum; row++) {
			// first half of board is white, second is black
			if (row > rowNum / 2) {
				pieceColor = TeamColor.BLACK;
			}

			for (int col = 0; col < colNum; col++) {
				char pieceSymbol = state[row].charAt(col);
				PieceType newPieceType = ChessPiece.resolveChessType(pieceSymbol);

				// If the new piece is null, then we need an empty square.
				if (newPieceType == null) { 
					continue;
				}

				ChessPiece newPiece = ChessPiece.makeNewPiece(pieceColor, newPieceType);
				newBoard[row][col] = newPiece;
			}
		}

		return newBoard;
	}

	/**
	 * Generates and returns a board complying with the configured standard board state.
	 */
	public ChessPiece[][] generateStandardBoard() {
		return this.generateBoard(this.standardState);
	}



}
