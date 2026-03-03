package chess.moveCalculator;

import chess.ChessPosition;

/**
 * Class for the Bishop ChessPiece move calculator
 */
public class BishopMoveCalculator extends ChessPieceMoveCalculator {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	private static final ChessPosition[] DIR_VECTORS = {
		new ChessPosition(1, 1),	
		new ChessPosition(1, -1),	
		new ChessPosition(-1, 1),	
		new ChessPosition(-1, -1)	
	};

	private static final int STANIMA = -1;

	//
	// ======================== CONSTRUCTORS ==============================
	//
	
	public BishopMoveCalculator() {
		super(BishopMoveCalculator.DIR_VECTORS, BishopMoveCalculator.STANIMA);
	}
}

