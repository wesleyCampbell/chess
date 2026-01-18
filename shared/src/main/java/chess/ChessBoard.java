package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
	private static int DEFAULT_ROW_NUM = 8;
	private static int DEFAULT_COL_NUM = 8;

	private static ChessPiece[][] generateNewBoard(int rowNum, int colNum) {
		return new ChessPiece[rowNum][colNum] ;	
	}

	int rowNum;
	int colNum;

	private ChessPiece[][] board;

	public ChessBoard() {
		this(ChessBoard.DEFAULT_COL_NUM, ChessBoard.DEFAULT_COL_NUM);
	}

    public ChessBoard(int rowNum, int colNum) {
        // A 2D array representing the actual board
		this.rowNum = rowNum;
		this.colNum = colNum;
		this.board = ChessBoard.generateNewBoard(rowNum, colNum); 
    }

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
    public void resetBoard() {
		this.board = generateNewBoard(this.rowNum, this.colNum);
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
				switch(piece.getPieceType()) {
					case KING:
						pieceSymbol = 'K';
						break;
					case QUEEN:
						pieceSymbol = 'Q';
						break;
					case BISHOP:
						pieceSymbol = 'B';
						break;
					case KNIGHT:
						pieceSymbol = 'N';
						break;
					case ROOK:
						pieceSymbol = 'R';
						break;
					case PAWN:
						pieceSymbol = 'P';
						break;
					default:
						pieceSymbol = '?';
				}

				outStr.append(String.format(" %c ", pieceSymbol));
			}	
			outStr.append(" |");
			if (row < board.length - 1) {
				outStr.append("\n");
			}
		}
		return outStr.toString(); 
	}
}
