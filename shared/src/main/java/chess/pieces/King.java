package chess.pieces;

import chess.ChessPiece;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;
import chess.moveCalculator.ChessPieceMoveCalculator;
import chess.moveCalculator.KingMoveCalculator;

/**
 * Represents a Rook on the board
 */

public class King extends ChessPiece {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	private static final PieceType TYPE = PieceType.KING;
	private static final ChessPieceMoveCalculator MOVE_CALCULATOR = new KingMoveCalculator();

	//
	// ======================== CONSTRUCTORS =======================
	//

	public King(TeamColor color) {
		super(color, King.TYPE, King.MOVE_CALCULATOR);
	}
}
