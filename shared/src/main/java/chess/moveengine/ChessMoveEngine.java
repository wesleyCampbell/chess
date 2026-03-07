package chess.moveengine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

public interface ChessMoveEngine {
	//
	// ================================ MEMBER METHODS =================================
	//
	
	/**
	 * Updates the team data bases that are required to make movement decisions
	 *
	 * @param board The current board state
	 */
	public void updateDatabases(ChessBoard board);

	/**
	 * Returns the team data of a certain color
	 *
	 * @param teamColor The color of the team to return
	 *
	 * @return The database of the requested team.
	 */
	public ChessTeamDatabase getTeamData(TeamColor color);
	
	/**
	 * Returns a collection of valid chess moves from a start position
	 *
	 * @param startPos the start position
	 *
	 * @return A Collection of valid chess moves
	 */
	public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition startPos);

	/** 
	 * Returns a union of all attack moves except for one team color's
	 *
	 * @param excludeColor The color to exclude from the set
	 *
	 * @return A set of all attack moves from all teams except the one provided
	 */
	public Collection<ChessMove> generateTeamAttacks(TeamColor teamColor);

	/**
	 * Determines whether a given move is valid or not based on the move rules.
	 *
	 * @param move The move to test
	 *
	 * @return true if valid, false otherwise
	 */
	public boolean isMoveValid(ChessBoard board, ChessMove move);

	/**
	 * Determines whether a given move would leave the king in check
	 *
	 * @param move The move to test
	 *
	 * @return true if move puts king in check, false otherwise
	 */
	public boolean moveRevealsCheck(ChessBoard board, ChessMove move);

	/**
	 * Makes a given chess move if valid.
	 *
	 * If invalid, will throw an InvalidMoveException
	 *
	 * @param move The move to make
	 */
	public void makeMove(ChessBoard board, ChessMove move, TeamColor activeTeamColor) throws InvalidMoveException;

	/**
	 * Performs a chess move, no questions asked. A dumb utility function. Use at your risk.
	 *
	 * Note that this method will only update the team movement databases as to allow testing.
	 * All other team databases will need to be updated by the caller.
	 *
	 * Can be used to perform an actual chess move, in which case the caller will want to
	 * update the cooresponding team databases.
	 *
	 * Can also be used to test if a move is valid as the movement databases are updated.
	 * If used in this way, make sure to undo the move.
	 *
	 * @param move The move to make
	 * @param piece2Move The piece being moved
	 * @param updateMoveDatabase If true, will update the database. Won't, otherwise
	 */
	public void utilMakeMove(ChessBoard board, ChessMove move, ChessPiece piece2Move, boolean updateDatabase);

	/**
	 * Checks to see if a given team is in check or not
	 *
	 * @param teamColor The color of the team to test
	 *
	 * @return true if in check, false otherwise
	 */
	public boolean isInCheck(TeamColor teamColor);

	/**
	 * Checks to see if a given team is in checkmate or not
	 *
	 * @param teamColor The color fo the team to test
	 *
	 * @return true if in check, false otherwise
	 */
	public boolean isInCheckmate(ChessBoard board, TeamColor teamColor);

	public boolean isInStalemate(ChessBoard board, TeamColor teamColor);

	public Map<TeamColor, ChessTeamDatabase> getChessTeamDatabase();
}
