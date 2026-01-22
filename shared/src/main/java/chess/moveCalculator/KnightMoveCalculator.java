package chess.moveCalculator;

import chess.*;

public class KnightMoveCalculator extends ChessPieceMoveCalculator {
	private static ChessPosition[] kngihtMoves = {
		new ChessPosition(1, 2),
		new ChessPosition(2, 1),

		new ChessPosition(2, -1),
		new ChessPosition(1, -2),

		new ChessPosition(-1, -2),
		new ChessPosition(-2, -1),

		new ChessPosition(-2, 1),
		new ChessPosition(-1, 2),
	};

	private static int moveStanima = 1;

	public KnightMoveCalculator() {
		super(KnightMoveCalculator.kngihtMoves, KnightMoveCalculator.moveStanima);
	}
}

