package chess.moveengine.specialmoves;

import java.util.Collection;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import chess.moveengine.*;

public interface SpecialMoveInterface {
	//
	// =============================== MEMBER METHODS ========================
	//
	/**
	 * Calculates and returns a collection of all the valid moves that follow
	 * the special move rules
	 *
	 * @param board The current chess board
	 * @param pos The position to check
	 *
	 * @return A collection of valid moves
	 */
	public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition pos);

	/**
	 * Verifies that a certain move matches the rules defined in the special move
	 *
	 * @param move The move to check
	 *
	 * @return True if the move matches rules, false otherwise;
	 */
	public boolean checkMove(ChessBoard board, ChessMove move);

	/**
	 * Performs a special move on a chess board
	 *
	 * @param board The board to make the move on
	 * @param move The move to make
	 */
	public void makeMove(ChessBoard board, ChessMove move);

	/**
	 * Getter for the getMoveEngine attribute
	 *
	 * @return The ChessMoveEngine of the special move
	 */
	public ChessMoveEngine getMoveEngine();

}
