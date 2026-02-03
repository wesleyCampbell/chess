package chess;

import chess.ChessPiece.PieceType;
import java.util.Collection;
import java.util.HashSet;
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
        BLACK
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
		// this.gameBoard = new ChessBoard(board);
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
