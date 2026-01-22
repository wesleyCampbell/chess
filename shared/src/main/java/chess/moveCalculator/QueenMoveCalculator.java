package chess.moveCalculator;

import chess.*;

public class QueenMoveCalculator extends ChessPieceMoveCalculator {
	private static ChessPosition[] queenMoves = {
		new ChessPosition(1, 1),
		new ChessPosition(1, -1),
		new ChessPosition(-1, 1),
		new ChessPosition(-1, -1),

		new ChessPosition(0, -1),
		new ChessPosition(0, 1),
		new ChessPosition(-1, 0),
		new ChessPosition(1, 0),
	};

	private static int moveStanima = -1;

	public QueenMoveCalculator() {
		super(QueenMoveCalculator.queenMoves, QueenMoveCalculator.moveStanima);
	}
}
