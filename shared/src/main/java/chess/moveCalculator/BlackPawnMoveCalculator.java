package chess.moveCalculator;

import chess.ChessPosition;
import chess.moveCalculator.PawnMoveCalculator;

public class BlackPawnMoveCalculator extends PawnMoveCalculator {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	private static final ChessPosition DIR_MODIFIER = new ChessPosition(-1, 1);

	//
	// ======================== CONSTRUCTORS =======================
	//
	
	public BlackPawnMoveCalculator() {
		super(
			applyDirModifier(DIR_MODIFIER, DIR_VECTORS),
			applyDirModifier(DIR_MODIFIER, ATTACK_VECTORS),
			applyDirModifier(DIR_MODIFIER, DOUBLE_JUMP_VECTOR)
				);
	}
}
