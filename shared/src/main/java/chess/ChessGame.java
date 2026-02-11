package chess;

import chess.ChessPiece.PieceType;
import chess.moveCalculator.ChessPieceMoveCalculator;
import static util.Debugger.debug;
import chess.moveEngine.*;

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

	public static Map<TeamColor, ChessTeamDatabase> generateTeamDatabase(ChessBoard board) {
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
	private ChessMoveEngine moveEngine;
	private Map<TeamColor, ChessTeamDatabase> chessTeamData;

	
	//
	// ============================ CONSTRUCTORS =======================
	//

    public ChessGame() {
		this.gameBoard = new ChessBoard();
		this.gameBoard.resetBoard();

		this.activeTeam = ChessGame.DEFAULT_START_COLOR;

		this.moveEngine = new StandardChessMoveEngine(this.gameBoard);

		this.chessTeamData = this.moveEngine.getChessTeamDatabase();
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
		return this.moveEngine.validMoves(this.gameBoard, startPosition);
    }

	/**
	 * Tests a move to see if it leaves the king in check
	 *
	 * @param move The move to test
	 *
	 * @return true if move leaves king in check, false otherwise
	 */
	private boolean moveRevealsCheck(ChessMove move) {
		return this.moveEngine.moveRevealsCheck(this.gameBoard, move);
	}

    /**
     * Makes a move in a chess game and updates all team database values
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
		this.moveEngine.makeMove(this.gameBoard, move, this.activeTeam);

		// Switches turns
		this.changeTurn();
    }


	/**
	 * Checks to see if a proposed move would be valid.
	 *
	 * @param move The move to test
	 *
	 * @return true if move is valid, false otherwise
	 */
	private boolean isMoveValid(ChessMove move) {
		return this.moveEngine.isMoveValid(this.gameBoard, move);
	}

	/**
	 * Changes the active team to the next team in the order
	 */
	private void changeTurn() {
		this.activeTeam = this.activeTeam.next();
	}


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
		return this.moveEngine.isInCheck(teamColor);
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
		return this.moveEngine.isInCheckmate(this.gameBoard, teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
		return moveEngine.isInStalemate(this.gameBoard, teamColor);
    }


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
		this.gameBoard = new ChessBoard(board);

		this.moveEngine.updateDatabases(board);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
		return this.gameBoard;
    }

	/**
	 * Overrides the equals() method.
	 *
	 * Determines equality based on the chessBoard contained as well as who's turn it is
	 *
	 * @param obj The other chessGame to compare
	 *
	 * @return boolean true if equal, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		// Must exist and must be ChessGame
		if (obj == null || !(obj instanceof ChessGame)) { return false; }

		ChessGame other = (ChessGame)obj;

		// Check to see if the chessboards are equal
		if (!this.gameBoard.equals(other.getBoard())) { return false; }

		if (this.activeTeam != other.getTeamTurn()) { return false; }

		return true;
	}

	/**
	 * Overrides the hashCode() method.
	 *
	 * Returns a unique integer hash based upon the game board and team turn.
	 *
	 * @return Unique int code
	 */
	@Override
	public int hashCode() {
		int hash = 0;

		hash += this.gameBoard.hashCode() * 31;

		hash += this.activeTeam.hashCode();

		return hash * 31;
	}
}
