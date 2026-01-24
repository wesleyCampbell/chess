package chess.pieces;

import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;
import chess.moveCalculator.BishopMoveCalculator;
import chess.moveCalculator.ChessPieceMoveCalculator;

/**
 * Represents a Pawn on the board.
 */

public class Bishop extends ChessPiece {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	private static final PieceType TYPE = PieceType.BISHOP;
	private static final ChessPieceMoveCalculator MOVE_CALCULATOR = new BishopMoveCalculator();

	//
	// ======================== CONSTRUCTORS =======================
	//
	
	public Bishop(TeamColor color) {
		super(color, Bishop.TYPE, Bishop.MOVE_CALCULATOR); 
	}
}
