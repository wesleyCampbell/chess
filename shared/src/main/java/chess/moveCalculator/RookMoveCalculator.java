package chess.moveCalculator;

import chess.*;

public class RookMoveCalculator extends ChessPieceMoveCalculator {
	private static ChessPosition[] rookMoves = {
		new ChessPosition(0, 1),
		new ChessPosition(0, -1),
		new ChessPosition(-1, 0),
		new ChessPosition(1, 0),
	};

	private static int moveStanima = -1;

	public RookMoveCalculator() {
		super(RookMoveCalculator.rookMoves, RookMoveCalculator.moveStanima);
	}
}
