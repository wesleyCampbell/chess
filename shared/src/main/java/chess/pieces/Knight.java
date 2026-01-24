package chess.pieces;

import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;
import chess.moveCalculator.ChessPieceMoveCalculator;
import chess.moveCalculator.KnightMoveCalculator;

/**
 * Represents a Pawn on the board.
 */

public class Knight extends ChessPiece {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	private static final PieceType TYPE = PieceType.KNIGHT;
	private static final ChessPieceMoveCalculator MOVE_CALCULATOR = new KnightMoveCalculator();

	//
	// ======================== CONSTRUCTORS =======================
	//
	
	public Knight(TeamColor color) {
		super(color, Knight.TYPE, Knight.MOVE_CALCULATOR);
	}
}

