package chess.moveCalculator;

import chess.ChessPosition;

/**
 * Class for the Rook ChessPiece move calculator
 */
public class RookMoveCalculator extends ChessPieceMoveCalculator {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	private static final ChessPosition[] DIR_VECTORS = {
		new ChessPosition(1, 0),	
		new ChessPosition(-1, 0),	
		new ChessPosition(0, 1),	
		new ChessPosition(0, -1),	
	};

	private static final int STANIMA = -1;

	//
	// ======================== CONSTRUCTORS ==============================
	//
	
	public RookMoveCalculator() {
		super(RookMoveCalculator.DIR_VECTORS, RookMoveCalculator.STANIMA);
	}
}

