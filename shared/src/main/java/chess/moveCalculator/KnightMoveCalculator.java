package chess.moveCalculator;

import chess.ChessPosition;

/**
 * Class for the Knight ChessPiece move calculator
 */
public class KnightMoveCalculator extends ChessPieceMoveCalculator {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	private static final ChessPosition[] DIR_VECTORS = {
		new ChessPosition(2, 1),
		new ChessPosition(1, 2),
		new ChessPosition(2, -1),
		new ChessPosition(1, -2),
		
		new ChessPosition(-2, 1),
		new ChessPosition(-1, 2),
		new ChessPosition(-2, -1),
		new ChessPosition(-1, -2),
	};

	private static final int STANIMA = 1;

	//
	// ======================== CONSTRUCTORS ==============================
	//
	
	public KnightMoveCalculator() {
		super(KnightMoveCalculator.DIR_VECTORS, KnightMoveCalculator.STANIMA);
	}
}

