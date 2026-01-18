package chess;

import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

/**
 * A representation of a chessboard that can hold and rearrange chess pieces.
 */
public class ChessBoard {
	//
	// ============================== STATIC ATTRIBUTES ==============================
	//
	
	private static int STANDARD_ROW_NUM = 8;
	private static int STANDARD_COL_NUM = 8;

	private static BoardState DEFAULT_BOARDSTATE = BoardState.DEFAULT;

	//
	// ============================== MEMBER ATTRIBUTES  ==============================
	//
	
	private int rowNum;
	private int colNum;
	private ChessPiece[][] board;
	
	//
	// ============================== CONSTRUCTORS ==============================
	//

	/**
	 * Default constructor. Sets the row and column numbers to standard values.
	 */
	public ChessBoard() {
		this(ChessBoard.STANDARD_ROW_NUM, ChessBoard.STANDARD_COL_NUM);
	}

	/**
	 * Constructor for ChessBoard.
	 *
	 * @param rowNum: The number of rows that the board will have
	 * @param colNum: The number of columns that the board will have
	 */
	public ChessBoard(int rowNum, int colNum) {
		this.rowNum = rowNum;
		this.colNum = colNum;
		this.board = new ChessPiece[rowNum][colNum];
	}

	//
	// ============================== MEMBER METHODS ==============================
	//

	/**
	 * Adds a chess piece to the board
	 *
	 * @param position: Where the piece will be added to
	 * @param piece: The piece to add
	 */
	public void addPiece(ChessPosition position, ChessPiece piece) {
		int row = position.getRow() - 1;
		int col = position.getColumn() - 1;
		this.board[row][col] = piece;
	}

	/**
	 * Gets a chess piece on the chessboard
	 *
	 * @param position: The position to look at on the board
	 * @return Either the piece at the position or null, if there is no piece
	 */
	public ChessPiece getPiece(ChessPosition position) {
		int row = position.getRow() - 1;
		int col = position.getColumn() - 1;

		return this.board[row][col];
	}

	/**
	 * Sets the board to a completely empty state
	 */
	public void clearBoard() {
		this.board = new ChessPiece[this.rowNum][this.colNum];
	}

	/**
	 * Sets the board to a previously configured board state
	 *
	 * @param newState: the new board set to use
	 */
	public void setBoard(BoardState newState) {
		// make sure the state is okay
		if (!BoardState.validateBoardState(newState)) {
			String err = "Inputed Board State is invalid!";
			throw new IllegalArgumentException(err);
		}

		for (int i = 0; i < this.rowNum; i++) {
			String row = newState.getState()[i];
			TeamColor color;

			// If we are on the top half of the board, the pieces are white
			// Black on the bottom half
			if (i < this.rowNum / 2) {
				color = TeamColor.WHITE;
			} else {
				color = TeamColor.BLACK;
			}
			
			// Iterate through the entire row
			for (int j = 0; j < row.length(); j++) {
				char symbol = row.charAt(j);
				PieceType type = ChessPiece.resolveChessPiece(symbol);

				// If the symbol isn't recognized, make it an empty square
				if (type == null) {
					board[i][j] = null;
					continue;
				}

				board[i][j] = new ChessPiece(color, type);
			}

		}

	}

	/**
	 * Resets the board to a default state
	 */
	public void resetBoard() {
		this.setBoard(DEFAULT_BOARDSTATE);
	}

	/**
	 * A String representation of the board
	 */
	@Override
	public String toString() {
		StringBuilder outStr = new StringBuilder();

		outStr.append("  h  g  f  e  d  c  b  a  \n");

		for (int i = 0; i < this.board.length; i++) {
			outStr.append("| ");

			for (int j = 0; j < board[j].length; j++) {
				// Gets the piece at the current index
				ChessPiece piece = board[i][j];
				char pieceSymbol;

				if (piece == null) {
					pieceSymbol = '-';	
				} else {
					pieceSymbol = ChessPiece.resolveChessPiece(piece.getPieceType());
				}

				outStr.append(String.format(" %c ", pieceSymbol));
			}

			outStr.append(" |");
			if (i < this.board.length - 1) {
				outStr.append("\n");
			}
		}

		return outStr.toString();
	}

	/**
	 * A hash method for the board
	 *
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		int hash = 0;

		for (int i = 0; i < this.rowNum; i++) {
			for (int j = 0; j < this.colNum; j++) {
				// If there is a piece on the spot, add the char value to the hash
				// If not, just add some index-based value
				if (board[i][j] != null) {
					PieceType type = this.board[i][j].getPieceType();
					hash += (int) ChessPiece.resolveChessPiece(type);
				} else {
					hash += i * 3 + j * 5;
				}
			}
			hash *= 31;
		}

		return hash;
	}

	/**
	 * Checks to see if two boards are equal.
	 *
	 * Considers to boards equal if each square contains the same piece.
	 *
	 * @return true if equal, false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		// obj must exist and be of type ChessBoard
		if (obj == null) { return false; }
		if (obj.getClass() != ChessBoard.class) { return false; }

		ChessBoard other = (ChessBoard) obj;

		// Compare each index
		for (int i = 0; i < this.rowNum; i++) {
			for (int j = 0; j < this.colNum; j++) {
				ChessPosition pos = new ChessPosition(i + 1, j + 1);

				ChessPiece thisPiece = this.getPiece(pos);
				ChessPiece otherPiece = other.getPiece(pos);

				// If one piece is null and the other is not
				// or they are not equal to each other, return false.
				if (thisPiece == null) {
					if (thisPiece != otherPiece) { return false; }
				} else {
					if (!thisPiece.equals(otherPiece)) { return false; }
				}	
			}
		}
		
		// Nothing was different, must be equal
		return true;
	}
}

