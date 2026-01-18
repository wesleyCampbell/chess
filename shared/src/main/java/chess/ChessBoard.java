package chess;

import java.util.InputMismatchException;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
	// ======================= STATIC ATTRIBUTES ======================

	private static int DEFAULT_ROW_NUM = 8;
	private static int DEFAULT_COL_NUM = 8;

	private static String [] DEFAULT_BOARDSTATE = {
		"RNBQKBNR",
		"PPPPPPPP",
		"--------",
		"--------",
		"--------",
		"--------",
		"PPPPPPPP",
		"RNBQKBNR"
	};

	// ======================= STATIC METHODS ======================
	/**
	 * Generates a completely clean board
	 *
	 * @param rowNum: the number of rows in the board
	 * @param colNum: the number of columns in the board
	 */
	private static ChessPiece[][] generateClearBoard(int rowNum, int colNum) {
		return new ChessPiece[rowNum][colNum] ;	
	}
	/**
	 * Verifies the dimentions of a game-state.
	 *
	 * @param boardState: an array of strings that represent the different rows of the board
	 */
	private static boolean validateBoardState(String[] boardState) {
		int colNum = boardState[0].length();
		for (int i = 1; i < boardState.length; i++) {
			if (boardState[i].length() != colNum) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns a chess piece type based on a symbol.
	 *
	 * For example, 'R' will return ROOK.
	 *
	 * @param char pieceSymbol : The character representation of a piece
	 *
	 * @return The object type of the piece
	 */
	private static ChessPiece.PieceType resolveChessPiece(char pieceSymbol) {
		switch(pieceSymbol) {
			case 'R':
				return ChessPiece.PieceType.ROOK;
			case 'N':
				return ChessPiece.PieceType.KNIGHT;
			case 'B':
				return ChessPiece.PieceType.BISHOP;
			case 'Q':
				return ChessPiece.PieceType.QUEEN;
			case 'K':
				return ChessPiece.PieceType.KING;
			case 'P':
				return ChessPiece.PieceType.PAWN;
			default:
				String err = String.format("Invalid piece symbol, %c", pieceSymbol);
				throw new InputMismatchException(err);
		}
	}
	
	/**
	 * Returns a character symbol based on a chess piece type
	 *
	 * @param pieceType : the chess piece type
	 * 
	 * @return The symbol representing a chess type
	 */
	private static char resolveChessPiece(ChessPiece.PieceType pieceType) {
		switch(pieceType) {
			case KING:
				return 'K';
			case QUEEN:
				return 'Q';
			case ROOK:
				return 'R';
			case KNIGHT:
				return 'N';
			case BISHOP:
				return 'B';
			case PAWN:
				return 'P';
			default:
				return '?';
		}
	}

	// ======================= MEMBER ATTRIBUTES ====================
	int rowNum;
	int colNum;

	// ======================= CONSTRUTORS ==========================
	private ChessPiece[][] board;

	public ChessBoard() {
		this(ChessBoard.DEFAULT_COL_NUM, ChessBoard.DEFAULT_COL_NUM);
	}

    public ChessBoard(int rowNum, int colNum) {
        // A 2D array representing the actual board
		this.rowNum = rowNum;
		this.colNum = colNum;
		this.board = ChessBoard.generateClearBoard(rowNum, colNum); 
    }

	// ==================== MEMBER METHODS =============================

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
		int row = position.getRow() - 1;
		int col = position.getColumn() - 1;

		this.board[row][col] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
		int row = position.getRow() - 1;
		int col = position.getColumn() - 1;

		return this.board[row][col];
    }
	

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
	private void clearBoard() {
		for (int i = 0; i < this.rowNum; i++) {
			for (int j = 0; j < this.colNum; j++) {
				this.board[i][j] = null;
			}
		}
	}

	private void setBoard(String[] newBoardState) {
		
		ChessBoard.validateBoardState(newBoardState);

		for (int i = 0; i < this.rowNum; i++) {
			String row = newBoardState[i];
			ChessGame.TeamColor pieceColor;

			// If we are on the top half of the board, the pieces are white.
			// Black on the bottom half
			if (i < rowNum / 2) {
				pieceColor = ChessGame.TeamColor.WHITE;
			} else {
				pieceColor = ChessGame.TeamColor.BLACK;
			}

			for (int j = 0; j < colNum; j++) {
				char pieceSymbol = row.charAt(j);
				ChessPiece.PieceType pieceType;
				
				// Will try to resolve the piece representation to a valid piece.
				// If it fails, the square will be empty.
				try {
					pieceType = ChessBoard.resolveChessPiece(pieceSymbol);
				} catch (InputMismatchException e) {
					board[i][j] = null;
					continue;
				}

				board[i][j] = new ChessPiece(pieceColor, pieceType);
			}
		}

	}

    public void resetBoard() {
		this.setBoard(ChessBoard.DEFAULT_BOARDSTATE);
    }

	/**
	 * Overrides the default toString() method to return a string implementation of the board
	 */
	@Override
	public String toString() {
		StringBuilder outStr = new StringBuilder();
		for (int row = 0; row < board.length; row++) {
			outStr.append("| ");
			
			for (int col = 0; col < board[row].length; col++) {
				// Gets the piece at current index
				ChessPiece piece = board[row][col];
				char pieceSymbol;  

				if (piece == null) {
					pieceSymbol = '-';
					continue;
				}
				pieceSymbol = ChessBoard.resolveChessPiece(piece.getPieceType());

				outStr.append(String.format(" %c ", pieceSymbol));
			}	
			outStr.append(" |");
			if (row < board.length - 1) {
				outStr.append("\n");
			}
		}
		return outStr.toString(); 
	}
	
	@Override
	public int hashCode() {
		int hash = 0;

		for (int i = 0; i < this.rowNum; i++) {
			for (int j = 0; j < this.colNum; j++) {
				// If there is a piece on the spot, add the char value to the hash
				// If not, just some index-based value
				if (board[i][j] != null) {
					ChessPiece.PieceType piece = this.board[i][j].getPieceType();
					hash += (int) ChessBoard.resolveChessPiece(piece);
				} else {
					hash += i * 3 + j * 9;
				}
			}
			hash *= 31;
		}

		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		// Obj must exist and be of type ChessBoard
		if (obj == null) { return false; }
		if (obj.getClass() != ChessBoard.class) { return false; }

		ChessBoard other = (ChessBoard) obj;

		// Compare each spot. Each spot on the board must contain the same piece.
		for (int i = 0; i < this.rowNum; i++) {
			for (int j = 0; j < this.colNum; j++) {
				ChessPosition pos = new ChessPosition(i + 1, j + 1);

				ChessPiece thisPiece = this.getPiece(pos);
				ChessPiece otherPiece = other.getPiece(pos);

				// If one piece is null and the other is not
				// or they are not equal to each other return false.
				if (thisPiece == null) {
					if (thisPiece != otherPiece) { return false; }
				} else {
					if (!thisPiece.equals(otherPiece)) { return false; }
				}
			}	
		}
		
		// Nothing was different, must be equal.
		return true;
	}
}
