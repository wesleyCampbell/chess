package chess.moveCalculator;

import chess.ChessPosition;

/**
 * Class for the Queen ChessPiece move calculator
 */
public class QueenMoveCalculator extends ChessPieceMoveCalculator {
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

	private static final int STANIMA = -1;

	//
	// ======================== CONSTRUCTORS ==============================
	//
	
	public QueenMoveCalculator() {
		super(QueenMoveCalculator.DIR_VECTORS, QueenMoveCalculator.STANIMA);
	}
}

