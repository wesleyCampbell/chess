package chess;

import chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	private static final int STANDARD_ROW_NUM = 8;
	private static final int STANDARD_COL_NUM = 8;
	

	//
	// ======================== MEMBER ATTRIBUTES =======================
	//
	
	private int rowNum;
	private int colNum;

	private ChessBoardGenerator boardGenerator;

	private ChessPiece[][] board;

	//
	// ======================== CONSTRUCTORS =======================
	//
	
	/**
	 * Default constructor. Assigns colNum and rowNum to predetermined standard values
	 */
    public ChessBoard() {
		this(ChessBoard.STANDARD_ROW_NUM, ChessBoard.STANDARD_COL_NUM);
    }

	/**
	 * Copy constructor
	 * @param other The ChessBoard to copy
	 */
	public ChessBoard(ChessBoard other) {
		this(other.getBoardHeight(), other.getBoardWidth());

		this.board = this.boardGenerator.copyBoardState(other);
	}

	/**
	 * Constructor. Allows custom row and column values for the board.
	 */
	public ChessBoard(int rowNum, int colNum) {
		this.colNum = colNum;
		this.rowNum = rowNum;

		this.boardGenerator = new ChessBoardGenerator();
		this.board = this.boardGenerator.generateEmptyBoard(rowNum, colNum);
	}

	//
	// ======================== MEMBER METHODS =======================
	//

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
		int row = position.getRow();
		int col = position.getColumn();

		this.board[row - 1][col - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
		int row = position.getRow();
		int col = position.getColumn();

		return this.board[row - 1][col - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
		this.board = this.boardGenerator.generateStandardBoard();	
    }

	/**
	 * Returns the number of rows in the board
	 *
	 * @return int
	 */
	public int getBoardHeight() {
		return this.rowNum;
	}

	/**
	 * Returns the number of columns in the board.
	 *
	 * @return int
	 */
	public int getBoardWidth() {
		return this.colNum;
	}

	public String[] getBoardState() {
		return this.boardGenerator.getState(this);
	}

	/**
	 * Overriden equality opperator.
	 *
	 * Equality based upon the same pieces being in the same squares on the board
	 */
	@Override
	public boolean equals(Object obj) {
		// obj must exist and must be a ChessBoard object
		if (obj == null || obj.getClass() != ChessBoard.class) {
			return false;
		}

		ChessBoard other = (ChessBoard)obj;

		// Check each board position for equality
		for (int row = 0; row < this.rowNum; row++) {
			for (int col = 0; col < this.colNum; col++) {
				ChessPosition square = new ChessPosition(row + 1, col + 1);
				
				ChessPiece thisPiece = this.getPiece(square);
				ChessPiece otherPiece = other.getPiece(square);

				// Can't perform equals() if thisPiece is null
				if (thisPiece == null) {
					if (thisPiece != otherPiece) { return false; }
				} else {
					if (!thisPiece.equals(otherPiece)) { return false; }
				}
			}
		}
		
		// If we haven't returned false by this point, the boards are equal.
		return true;
	}

	/**
	 * Overriden hashCode method.
	 *
	 * Returns a unique hash code based upon the pieces on the board and their indexes.
	 *
	 * @return The hash code
	 */
	@Override
	public int hashCode() {
		int hash = 0;

		for (int row = 0; row < this.rowNum; row++) {
			for (int col = 0; col < this.colNum; col++) {
				ChessPosition square = new ChessPosition(row + 1, col + 1);

				ChessPiece piece = this.getPiece(square);

				if (piece == null) { hash += 3 * col + 5 * row; }

				else { hash += piece.hashCode(); }

				hash *= 31;
			}
		}
		
		return hash;
	}

	/**
	 * Provides a string representation of the ChessBoard
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		StringBuilder outStr = new StringBuilder();

		for (int row = this.rowNum - 1; row >= 0; row--) {
			outStr.append(String.format("%d: ", row + 1));
			for (int col = 0; col < this.colNum; col++ ){
				ChessPosition square = new ChessPosition(row + 1, col + 1);
				ChessPiece piece = this.getPiece(square);

				char symbol;

				if (piece == null) { 
					symbol = '-';
				} else {
					symbol = ChessPiece.resolveChessType(piece.getPieceType());
				}

				outStr.append(String.format(" %c ", symbol));
			}

			if (row != 0) {
				outStr.append("\n");
			}
		}

		outStr.append("   ------------------------\n");
		outStr.append("\n    A  B  C  D  E  F  G  H  \n");

		return outStr.toString();
	}
}
