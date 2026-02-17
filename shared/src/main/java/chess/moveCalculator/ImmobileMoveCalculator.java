package chess.moveCalculator;

import chess.ChessPosition;

/**
 * Class for the immobile utility chess piece move calculator. In effect, they move as much as a rock.
 */
public class ImmobileMoveCalculator extends ChessPieceMoveCalculator {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	private static final ChessPosition[] DIR_VECTORS = {
		new ChessPosition(0, 0)
	};

	private static final int STANIMA = 0;

	//
	// ======================== CONSTRUCTORS ==============================
	//
	
	public ImmobileMoveCalculator() {
		super(DIR_VECTORS, STANIMA);
	}
}

