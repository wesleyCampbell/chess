package chess;

import chess.ChessPiece.PieceType;
import chess.pieces.Barrier;
import chess.ChessGame.TeamColor;
import static util.Debugger.debug;

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
		"pppppppp",
		"rnbqkbnr"
	};

	public static final String[] STANDARD_EMPTY_STATE = {
		"--------",
		"--------",
		"--------",
		"--------",
		"--------",
		"--------",
		"--------",
		"--------"
	};
	
	//
	// ======================== STATIC METHODS =======================
	//
	
	/**
	 * Given a given set of dimentions, generates an empty board state
	 *
	 * @param rowNum The number of rows
	 * @param colNum the number of cols
	 *
	 * @return A string array representing the borad state
	 */
	public static String[] generateEmptyBoardState(int rowNum, int colNum) {
		String[] outState = new String[rowNum];

		// As all rows are the same, we can avoid O(n^2) and just build the row once
		StringBuilder rowStrBuilder = new StringBuilder();
		for (int col = 0; col < colNum; col++) {
			rowStrBuilder.append(SYMBOL_EMPTY_SQUARE);
		}

		// sets the row to each row slot
		String rowStr = rowStrBuilder.toString();
		for (int row = 0; row < rowNum; row++) {
			outState[row] = rowStr;
		}

		return outState;
	}
	
	public static ChessPiece resolveChessPiece(char pieceSymbol) {
		ChessPiece outPiece;

		switch (pieceSymbol) {
			case SYMBOL_EMPTY_SQUARE:
				return null;
			case SYMBOL_BARRIER:
				// TODO: make new piece for this resolution
				outPiece = new Barrier();
				break;
			case SYMBOL_VOID:
				// TODO: make new piece for this thing
				outPiece = null;
				break;
			default:
				outPiece = ChessPiece.resolveChessPiece(pieceSymbol);
		}

		return outPiece;
	}

	public static final char SYMBOL_EMPTY_SQUARE = '-';
	public static final char SYMBOL_BARRIER = 'X';
	public static final char SYMBOL_VOID = ' ';

	//
	// ======================== MEMBER ATTRIBUTES =======================
	//
	
	private String[] standardState;
	private String[] currentState;

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
		if (!verifyStateDims(standardState)) {
			standardState = STANDARD_BOARD_STATE;
		}

		this.standardState = standardState;
		int rowNum = standardState.length;
		int colNum = standardState[0].length();
		this.currentState = generateEmptyBoardState(rowNum, colNum);
	}

	/**
	 * Constructor that allows custom standard state and custom starting board state
	 *
	 * @param standardState The default state of a chessboard, for reseting purposes.
	 * @param currentState The current state of the chessboard.
	 */
	public ChessBoardGenerator(String[] standardState, String[] currentState) {
		this.standardState = standardState;
		this.currentState = currentState;
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
		String[] newState = new String[rowNum];

		for (int row = 0; row < rowNum; row++) {
			StringBuilder rowStr = new StringBuilder();

			for (int col = 0; col < colNum; col++) {
				rowStr.append(ChessBoardGenerator.SYMBOL_EMPTY_SQUARE);
			}

			newState[row] = rowStr.toString();
		}

		this.currentState = newState;
		return new ChessPiece[rowNum][colNum];
	}


	/**
	 * Verifies that the dimentions of a string state are valid
	 *
	 * The dimentions must match a rectangle.
	 *
	 * @param state The state to verify
	 *
	 * @return true if valid, false otherwise
	 */
	public boolean verifyStateDims(String[] state) {
		int rowNum = state.length;

		// A state must have at least one row
		if (rowNum == 0) {
			return false;
		}

		int colNum = state[0].length();

		// Check to see if all rows have the same number of columns
		for (int row = 1; row < rowNum; row++) {
			if (state[row].length() != colNum) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Generates a new board based on an input state blueprint
	 *
	 * @param state An array of strings outlining the new board state
	 *
	 * @return The new ChessBoard array
	 */
	public ChessPiece[][] generateBoard(String[] state) throws InvalidBoardStateException {
		// make sure the state is valid
		if (!verifyStateDims(state)) {
			throw new InvalidBoardStateException();
		}

		int rowNum = state.length;
		int colNum = state[0].length();

		ChessPiece[][] newBoard = new ChessPiece[rowNum][colNum];

		for (int row = 0; row < rowNum; row++) {
			// first half of board is white, second is black

			for (int col = 0; col < colNum; col++) {
				char pieceSymbol = state[row].charAt(col);

				ChessPiece newPiece = resolveChessPiece(pieceSymbol);

				//PieceType newPieceType = ChessPiece.resolveChessType(pieceSymbol);
				//
				//// TODO: do something better for this to support more teams than 2
				//TeamColor pieceColor;
				//if (Character.isUpperCase(pieceSymbol)) {
				//	pieceColor = TeamColor.WHITE;
				//} else {
				//	pieceColor = TeamColor.BLACK;
				//}
				//
				//// If the new piece is null, then we need an empty square.
				//if (newPieceType == null) { 
				//	continue;
				//}
				//
				//ChessPiece newPiece = ChessPiece.makeNewPiece(pieceColor, newPieceType);
				newBoard[row][col] = newPiece;
			}
		}

		this.currentState = state;
		return newBoard;
	}

	/**
	 * Copies the board state of another ChessBoard
	 *
	 * @param other The ChessBoard to copy
	 *
	 * @return A 2D array of Chess Pieces
	 */
	public ChessPiece[][] copyBoardState(ChessBoard other) {
		String[] otherBoardState = other.getBoardState();

		try {

			ChessPiece[][] outBoard = this.generateBoard(otherBoardState);
			return outBoard; 
		} catch (InvalidBoardStateException err) {
			return null;
		}

		//return this.generateBoard(otherBoardState);
	}

	/**
	 * Generates and returns a board complying with the configured standard board state.
	 */
	public ChessPiece[][] generateStandardBoard() {
		try {
		return this.generateBoard(this.standardState);
		} catch (InvalidBoardStateException err) {
			return null;
		}
	}

	/**
	 * Getter for the currentState
	 *
	 * @return String array representing the state of a chessboard.
	 */
	public String[] getState(ChessBoard board) {
		int rowNum = board.getBoardHeight();
		int colNum = board.getBoardWidth();

		String[] outState = new String[rowNum];

		StringBuilder rowStr = new StringBuilder();
		// Iterate through each square and append the cooresponding piece type into the array
		for (ChessBoard.IndexedPiece pieceInx : board) {
			char pieceSymbol;

			ChessPiece piece = pieceInx.piece();

			if (piece == null) {
				pieceSymbol = '-';
			} else {
				pieceSymbol = ChessPiece.resolveChessType(piece.getPieceType(), piece.getTeamColor());
			}

			rowStr.append(pieceSymbol);

			if (pieceInx.position().getColumn() == board.getBoardWidth()) {
				outState[pieceInx.position().getRow() - 1] = rowStr.toString();
				rowStr = new StringBuilder();
			}

		}

		return outState;
	}

}
