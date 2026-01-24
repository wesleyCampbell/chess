package chess.moveCalculator;

import chess.ChessPosition;

/**
 * Class for the King ChessPiece move calculator
 */
public class KingMoveCalculator extends ChessPieceMoveCalculator {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	private static final ChessPosition[] DIR_VECTORS = {
		new ChessPosition(1, 0),	
		new ChessPosition(-1, 0),	
		new ChessPosition(0, 1),	
		new ChessPosition(0, -1),	

		new ChessPosition(1, 1),	
		new ChessPosition(1, -1),	
		new ChessPosition(-1, 1),	
		new ChessPosition(-1, -1)	
	};

	private static final int STANIMA = 1;

	//
	// ======================== CONSTRUCTORS ==============================
	//
	
	public KingMoveCalculator() {
		super(KingMoveCalculator.DIR_VECTORS, KingMoveCalculator.STANIMA);
	}
}
