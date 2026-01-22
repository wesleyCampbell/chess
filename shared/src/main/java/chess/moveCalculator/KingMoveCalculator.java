package chess.moveCalculator;

import chess.*;

public class KingMoveCalculator extends ChessPieceMoveCalculator {
	private static ChessPosition[] kingMoves = {
		new ChessPosition(1, 1),
		new ChessPosition(1, -1),
		new ChessPosition(-1, 1),
		new ChessPosition(-1, -1),

		new ChessPosition(0, -1),
		new ChessPosition(0, 1),
		new ChessPosition(-1, 0),
		new ChessPosition(1, 0),
	};

	private static int moveStanima = 1;

	public KingMoveCalculator() {
		super(KingMoveCalculator.kingMoves, KingMoveCalculator.moveStanima);
	}
}

