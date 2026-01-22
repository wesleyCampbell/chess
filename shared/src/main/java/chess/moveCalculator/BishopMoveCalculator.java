package chess.moveCalculator;

import chess.*;

public class BishopMoveCalculator extends ChessPieceMoveCalculator {
	private static ChessPosition[] bishopMoves = {
		new ChessPosition(1, 1),
		new ChessPosition(1, -1),
		new ChessPosition(-1, 1),
		new ChessPosition(-1, -1),
	};

	private static int moveStanima = -1;

	public BishopMoveCalculator() {
		super(BishopMoveCalculator.bishopMoves, BishopMoveCalculator.moveStanima);
	}
}
