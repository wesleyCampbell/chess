package chess;

import chess.ChessPiece.PieceType;
import chess.moveCalculator.ChessPieceMoveCalculator;
import static util.Debugger.debug;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
	//
	// ============================ STATIC ATTRIBUTES =======================
	//
	
	private static final TeamColor DEFAULT_START_COLOR = TeamColor.WHITE;

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public static enum TeamColor {
        WHITE,
        BLACK;

		private static final TeamColor[] VALUES = values();

		public TeamColor next() {
			return VALUES[(this.ordinal() + 1) % VALUES.length];
		}
    }

	private static Map<TeamColor, ChessTeamDatabase> generateTeamDatabase(ChessBoard board) {
		HashMap<TeamColor, ChessTeamDatabase> database = new HashMap<>();
		for (TeamColor color : TeamColor.values()) {
			database.put(color, new ChessTeamDatabase(color, board));
		}

		return database;
	}

	//
	// ============================ MEMBER ATTRIBUTES =======================
	//
	
	private ChessBoard gameBoard;
	private TeamColor activeTeam;

	private Map<TeamColor, ChessTeamDatabase> chessTeamData;
	
	//
	// ============================ CONSTRUCTORS =======================
	//

    public ChessGame() {
		this.gameBoard = new ChessBoard();
		this.gameBoard.resetBoard();

		this.activeTeam = ChessGame.DEFAULT_START_COLOR;

		this.chessTeamData = ChessGame.generateTeamDatabase(this.gameBoard);

    }

	//
	// ============================ MEMBER METHODS =======================
	//

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
		return activeTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
		this.activeTeam = team;
    }


    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
		HashSet<ChessMove> allMoves = new HashSet<>();

		// STEP 1: Get the default moves of the piece on the square, if it exists
		ChessPiece piece = this.gameBoard.getPiece(startPosition);

		// If there is no piece on the square, just return empty set
		if (piece == null) {
			return allMoves;
		}

		// allMoves.addAll(piece.pieceMoves(this.gameBoard, startPosition));
		Collection<ChessMove> defaultMoves = piece.pieceMoves(this.gameBoard, startPosition);

		// STEP 2: Take out moves that put king in check
		for (ChessMove move : defaultMoves) {
			if (this.moveRevealsCheck(move)) {
				continue;
			}

			// If the move doesn't reveal check, make the move
			allMoves.add(move);
		}

		// STEP 3: Add special moves, if necessary

		return allMoves;
    }

	private boolean moveRevealsCheck(ChessMove move) {
		Boolean ret = false;
		ChessPosition startPos = move.getStartPosition();
		ChessPosition endPos = move.getEndPosition();

		// Copy the data that will be overwritten
		ChessPiece capturedPiece = this.gameBoard.getPiece(endPos);
		ChessPiece movedPiece = this.gameBoard.getPiece(startPos);

		// Make the move to see if it puts the king in check.
		this._makeMove(move, movedPiece);

		// If the king is in check, the move revealed check.
		if (this.isInCheck(movedPiece.getTeamColor())) {
			ret = true;	
		}

		// undo the move
		this.gameBoard.addPiece(startPos, movedPiece);
		this.gameBoard.addPiece(endPos, capturedPiece);
		this.updateDatabases();
		
		return ret;
	}

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
		System.out.println(this.gameBoard);
		debug(String.format("move: %s", move), 0);

		// STEP 1: Check to see if there is a valid piece at the start position
		ChessPosition startPos = move.getStartPosition();
		ChessPiece piece = this.gameBoard.getPiece(startPos);
		if (piece == null || piece.getTeamColor() != this.activeTeam) {
			String err = String.format("Piece at square %s either doesn't exist or it is not their turn!", startPos);
			throw new InvalidMoveException(err);
		}

		// STEP 2: Verify that move is valid
		if (!this.isMoveValid(move)) {
			String err = String.format("Move %s is invalid!", move);
			throw new InvalidMoveException(err);
		}

		// STEP 3: Make move
		this._makeMove(move, piece);

		debug("GameBoard after move: ", 0);
		System.out.println(this.gameBoard);
		
		// STEP 4: Update databases

		// STEP 5: Change who's turn it is
		this.changeTurn();
    }

	/**
	 * Performs a chess move, no questions asked.
	 *
	 * @param move The move to make
	 * @param piece2Move The piece being moved
	 */
	private void _makeMove(ChessMove move, ChessPiece piece2Move) {
		ChessPosition startPos = move.getStartPosition();
		ChessPosition endPos = move.getEndPosition();

		ChessPiece capturePiece = this.gameBoard.getPiece(endPos);
		if (capturePiece != null) {
			this.chessTeamData.get(piece2Move.getTeamColor()).addCapturedPiece(capturePiece);
		}

		// Check to see if there is a promotion to do
		PieceType promotionType = move.getPromotionPiece();
		if (promotionType != null) {
			piece2Move = ChessPiece.makeNewPiece(piece2Move.getTeamColor(), promotionType);
		}

		// The actual move
		this.gameBoard.addPiece(startPos, null);
		this.gameBoard.addPiece(endPos, piece2Move);

		// Update the move databases
		this.updateDatabases();
	}

	/**
	 * Checks to see if a proposed move would be valid.
	 *
	 * @param move The move to test
	 *
	 * @return true if move is valid, false otherwise
	 */
	private boolean isMoveValid(ChessMove move) {
		ChessPosition startPos = move.getStartPosition();

		Collection<ChessMove> validMoves = this.validMoves(startPos);

		if (!validMoves.contains(move)) {
			return false;
		}
	
		return true;
	}

	/**
	 * Changes the active team to the next team in the order
	 */
	private void changeTurn() {
		this.activeTeam = this.activeTeam.next();
	}

	/** 
	 * Returns a union of all attack moves except for one team color's
	 *
	 * @param excludeColor The color to exclude from the set
	 *
	 * @return A set of all attack moves from all teams except the one provided
	 */
	private HashSet<ChessMove> generateTeamAttacks(TeamColor teamColor) {
		HashSet<ChessMove> attackMoves = new HashSet<>();

		for (ChessTeamDatabase db : this.chessTeamData.values()) {
			// Exclude the provided team
			if (db.getTeamColor() == teamColor) { continue; }

			attackMoves.addAll(db.getAttackMoveSet());
		}

		return attackMoves;
	}

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
		HashSet<ChessPosition> kingPos = this.chessTeamData.get(teamColor).getKingPos();

		// Generate attacks from all teams except teamColor and take out the endPositions
		HashSet<ChessPosition> attackSquares = ChessMove.extractEndPositions(this.generateTeamAttacks(teamColor));

		// Check to see if any of the king positions are in the attack squares
		for (ChessPosition king : kingPos) {
			if (attackSquares.contains(king)) { return true; }
		}

		return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
		// Must be in check to be in checkmate
		if (!this.isInCheck(teamColor)) {
			return false; 
		}

		HashSet<ChessPosition> attackSquares = ChessMove.extractEndPositions(this.generateTeamAttacks(teamColor));
		HashSet<ChessPosition> kingPos = this.chessTeamData.get(teamColor).getKingPos();

		for (ChessPosition pos : kingPos) {
			ChessPiece king = this.gameBoard.getPiece(pos);
			// Get all the move squares
			Collection<ChessMove> moves = king.pieceMoves(this.gameBoard, pos);
			

			HashSet<ChessPosition> moveSquares = ChessMove.extractEndPositions(moves);

			// Remove all of the attackSquares from the moveSquares to see if the king has any legal moves
			moveSquares.removeAll(attackSquares);

			// e.g. the king cannot move
			if (moveSquares.isEmpty()) {
				return true;
			}
		}

		return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
		if (this.isInCheck(teamColor)) { return false; }

		// If the moveSet is empty, then the team is in stalemate

		return this.chessTeamData.get(teamColor).getMoveSet().isEmpty();
    }

	/**
	 * Updates the team databases with a new board state
	 *
	 * @param board The new board state
	 */
	private void updateDatabases() {

		for (ChessTeamDatabase db : this.chessTeamData.values()) {
			db.update(this.gameBoard);
		}
	}

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
		for (int row = 0; row < board.getBoardHeight(); row++) {
			for (int col = 0; col < board.getBoardWidth(); col++) {
				ChessPosition square = new ChessPosition(row+1, col+1);
				ChessPiece piece = board.getPiece(square);

				if (piece == null) {
					continue;
				} 
			}
		}

		this.gameBoard = new ChessBoard(board);

		this.updateDatabases();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
		return this.gameBoard;
    }
}
