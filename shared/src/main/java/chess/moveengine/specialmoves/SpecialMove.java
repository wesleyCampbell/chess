package chess.moveengine.specialmoves;

import chess.moveengine.ChessMoveEngine;

public abstract class SpecialMove implements SpecialMoveInterface {
	//
	// ========================== CONSTRUCTORS ==============
	//
	private transient ChessMoveEngine moveEngine;

	protected SpecialMove(ChessMoveEngine moveEngine) {
		this.moveEngine = moveEngine;
	}

	public ChessMoveEngine getMoveEngine() {
		return this.moveEngine;
	}
}
