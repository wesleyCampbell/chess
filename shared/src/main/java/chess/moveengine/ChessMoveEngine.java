package chess.moveEngine;

import java.util.Collection;
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
	 * Returns a collection of valid chess moves from a start position
	 *
	 * @param startPos the start position
	 *
	 * @return A Collection of valid chess moves
	 */
	public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition startPos);

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
