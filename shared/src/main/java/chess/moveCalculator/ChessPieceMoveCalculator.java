package chess.moveCalculator;

import java.util.HashSet;

import chess.*;

public class ChessPieceMoveCalculator {
	
	protected ChessPosition[] directionVectors;
	protected int moveStanima;

	//
	// ========================= CONSTRUCTORS ===================================
	//
	public ChessPieceMoveCalculator(ChessPosition[] dirVectors, int moveStanima) {
		this.directionVectors = dirVectors;
		this.moveStanima = moveStanima;
	}

	protected boolean checkBounds(ChessPosition pos, ChessPosition bounds1, ChessPosition bounds2, ChessBoard board) {
		if (pos.getRow() < bounds1.getRow() || pos.getRow() > bounds2.getRow()) {
			return false;
		}
		if (pos.getColumn() < bounds1.getColumn() || pos.getColumn() > bounds2.getColumn()) {
			return false;
		}

		return true;
	}

	public HashSet<ChessMove> calculateMoves(ChessBoard board, ChessPiece piece, ChessPosition curPosition) {
		// This is the collection of all moves that are correct
		HashSet<ChessMove> validMoves = new HashSet<>();

		System.out.println(board.toString());

		for (ChessPosition dirVec : this.directionVectors) {
			// Make our new position vector and set it equal to the current piece position
			ChessPosition newPos = new ChessPosition(0, 0);
			newPos.add(curPosition);

			int depth = 1;
			while (depth <= this.moveStanima || this.moveStanima < 0) {
				// Move in the current direction
				newPos.add(dirVec);

				// If the new position is out of bounds, go on to the next direction.
				if (!checkBounds(newPos, new ChessPosition(1, 1), // the positions start at index 1
						new ChessPosition(board.getBoardWidth(), board.getBoardHeight()),
						board)) {
					break;
				}

				// A copy of the new position vector to pass into the validMoves set
				ChessPosition movePos = new ChessPosition(newPos);

				// Check for piece blocking the path
				ChessPiece blockingPiece = board.getPiece(newPos);
				if (blockingPiece != null) {
					// If it is of another color, we can capture it.
					if (blockingPiece.getTeamColor() != piece.getTeamColor()) {
						validMoves.add(new ChessMove(curPosition, movePos, null));
					}
					break;
				}
// In bounds, no piece to capure
				System.out.println("    Adding to validMoves");
				validMoves.add(new ChessMove(curPosition, movePos, null));

				// update the move depth
				depth++;
			}
		}

		return validMoves;
	}
}
