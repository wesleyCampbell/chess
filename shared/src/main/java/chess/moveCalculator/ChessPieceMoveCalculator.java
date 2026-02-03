package chess.moveCalculator;

import java.util.HashSet;

import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

/**
 * Interface for the ChessPieceMoveCalculator object.
 */
public abstract class ChessPieceMoveCalculator {
	
	//
	// ======================== STATIC METHODS =======================
	//
	
	protected ChessPosition[] directionVectors;
	protected int moveStanima;

	// ======================== CONSTRUCTOR =======================
	//
	
	protected ChessPieceMoveCalculator(ChessPosition[] directionVectors, int moveStanima) {
		this.directionVectors = directionVectors;
		this.moveStanima = moveStanima;
	}

	// ======================== METHODS =======================
	//
	
	/**
	 * Checks to see if a given position is found within a board
	 *
	 * @param board The game board
	 * @param pos The position to check
	 *
	 * @return true if the position is within the board, false otherwise
	 */
	protected boolean checkBoundaries(ChessBoard board, ChessPosition pos) {
		int row = pos.getRow() - 1;
		int col = pos.getColumn() - 1;

		if (row < 0 || row >= board.getBoardHeight()) { return false; }
		if (col < 0 || col >= board.getBoardWidth()) { return false; }

		return true;
	}

	/**
	 * Default version of the calculateMoves method.
	 *
	 * When this is called, capture will always be set to true, meaning 
	 * that the piece will always try to capture, if it is able
	 *
	 * @param piece The chess piece
	 * @param curPos The position of the chess piece
	 * @param board The current board
	 * 
	 * @return A HashSet of all the valid moves the piece can make.
	 */
	public HashSet<ChessMove> calculateMoves(TeamColor color, ChessPosition curPos, ChessBoard board) {
		return this.calculateMoves(color, curPos, board, true);
	}
	
	/**
	 * Given a piece, position, and a board state, this function will
	 * calculate all of the valid moves the piece can make.
	 *
	 * @param piece The chess piece 
	 * @param curPos The position of the piece
	 * @param board The current board
	 * @param capture Whether the piece should capture pieces or not
	 *
	 * @return A Hashset of all the valid moves.
	 */
	public HashSet<ChessMove> calculateMoves(TeamColor color, ChessPosition curPos, ChessBoard board, boolean capture) {
		HashSet<ChessMove> validMoves = new HashSet<>();

		// iterate through each of the object's direction vectors;
		for (ChessPosition dir : this.directionVectors) {
			// copy the current position vector
			ChessPosition newPos = new ChessPosition(curPos);

			// We can only move so far in one direction
			// If this.moveStanima == -1, then there is no limit
			int moveStanima = this.moveStanima;
			while (this.moveStanima == -1 || moveStanima  > 0) {

				// Adds the movement
				newPos.add(dir);

				// Checks the boundaries 
				if (!this.checkBoundaries(board, newPos)) {
					break;
				}

				// Make a copy of the current position vector to pass into the new move.
				// It can't be the same because the passed reference would be changed with 
				// future iterations
				ChessPosition movePos = new ChessPosition(newPos);

				// Check to see if we are blocked by another piece and whether we can capture
				ChessPiece blockingPiece = board.getPiece(newPos);
				if (blockingPiece != null) {
					// If capture is set to true and the piece belongs to an enemy team, we may capture
					if (capture && blockingPiece.getTeamColor() != color) {
						ChessMove lastMove = new ChessMove(curPos, movePos, null);
						validMoves.add(lastMove);
					}
					// we are blocked, so we cannot continue.
					break;
				}
				
				// If nothing is blocking us, we can add the move
				ChessMove move = new ChessMove(curPos, movePos, null);

				validMoves.add(move);

				moveStanima--;
			}
		}

		return validMoves;
	}

	/**
	 * Calculates all valid attack moves. Note that in most cases this method will return
	 * the same as ChessPieceMoveCalculator.calculateMoves().
	 *
	 * @param board The current chess board
	 * @param pos The position of the ChessPiece
	 * @param color The color of the piece
	 */
	public HashSet<ChessMove> calculateAttackMoves(ChessBoard board, ChessPosition pos, TeamColor color) {
		return this.calculateMoves(color, pos, board);
	}
}

