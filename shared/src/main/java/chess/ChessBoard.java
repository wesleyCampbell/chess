package chess;

import chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor;

import java.util.Iterator;
import java.lang.Iterable;
import java.util.NoSuchElementException;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Iterable<ChessBoard.IndexedPiece>{
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	private static final int STANDARD_ROW_NUM = 8;
	private static final int STANDARD_COL_NUM = 8;

	public static record IndexedPiece(ChessPosition position, ChessPiece piece) {}
	

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
		//this(ChessBoard.STANDARD_ROW_NUM, ChessBoard.STANDARD_COL_NUM);
		this.colNum = STANDARD_COL_NUM;
		this.rowNum = STANDARD_ROW_NUM;
		
		this.boardGenerator = new ChessBoardGenerator();
		this.board = this.boardGenerator.generateEmptyBoard(rowNum, colNum);
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

		String[] standardBoardState = ChessBoardGenerator.generateEmptyBoardState(rowNum, colNum);

		this.boardGenerator = new ChessBoardGenerator(standardBoardState);
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
	 * Removes a piece from the board, esentially deleting it
	 *
	 * @param position The position to remove the piece from
	 */
	public void removePiece(ChessPosition position) {
		int row = position.getRow() - 1;
		int col = position.getColumn() - 1;

		this.board[row][col] = null;
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
		for (IndexedPiece pieceInx : this) {
			ChessPiece thisPiece = pieceInx.piece();
			ChessPiece otherPiece = other.getPiece(pieceInx.position());

			// Can't perform equals() if thisPiece is null
			if (thisPiece == null) {
				if (thisPiece != otherPiece) { return false; }
			} else {
				if (!thisPiece.equals(otherPiece)) { return false; }
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

		for (IndexedPiece pieceInx : this) {

			if (pieceInx.piece() == null) {
				hash += 3 * pieceInx.position().getColumn();
				hash += 5 * pieceInx.position().getRow();
			} else {
				hash += pieceInx.piece().hashCode();
			}

			hash *= 31;
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

		for (IndexedPiece pieceInx : this) {
			char symbol;
			ChessPiece piece = pieceInx.piece();
			ChessPosition square = pieceInx.position();

			// Inserts the row number in front of each row
			if (square.getColumn() == 1) {
				outStr.append(String.format("%d ", square.getRow()));
			}

			if (piece == null) { 
				symbol = '-';
			} else {
				symbol = ChessPiece.resolveChessType(piece.getPieceType(), piece.getTeamColor());
			}

			outStr.append(String.format(" %c ", symbol));

			// gives a new line for each new row
			if (square.getColumn() == this.rowNum) {
				outStr.append("\n");
			}
			
		}

		outStr.append("   ------------------------\n");
		outStr.append("    A  B  C  D  E  F  G  H  \n");

		return outStr.toString();
	}

	/**
	 * Checks to see if a given chess position is within bounds of the board
	 *
	 * @param pos The position to check
	 *
	 * @return true if in bounds, false otherwise.
	 */
	public boolean isInBounds(ChessPosition pos) {
		int row = pos.getRow();
		int col = pos.getColumn();

		// check row bounds
		if (0 >= row || row > this.rowNum) {
			return false;
		}

		// check column bounds
		if (0 >= col || col > this.colNum) {
			return false;
		}

		return true;
	}

	//
	// ===================================== ITERATOR =======================================
	//
	
	public Iterator<IndexedPiece> iterator() {
		return new BoardIterator();
	}

	private class BoardIterator implements Iterator<IndexedPiece> {
		private int row = 0;
		private int col = 0;

		@Override 
		public boolean hasNext() {
			return row < rowNum;
		}

		@Override
		public IndexedPiece next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}
			
			ChessPiece piece = board[row][col];
			ChessPosition pos = new ChessPosition(row + 1, col + 1);  // +1 as ChessPosition is 1-indexed

			this.incrementIndex();

			return new IndexedPiece(pos, piece);
		}

		private void incrementIndex() {
			col++;
			if (col >= board[row].length) {
				col = 0;
				row++;
			}

		}
	}
}
