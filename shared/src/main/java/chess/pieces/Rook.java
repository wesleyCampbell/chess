package chess.pieces;

import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;
import chess.moveCalculator.ChessPieceMoveCalculator;
import chess.moveCalculator.RookMoveCalculator;

/**
 * Represents a Pawn on the board.
 */

public class Rook extends ChessPiece {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	private static final PieceType TYPE = PieceType.ROOK;
	private static final ChessPieceMoveCalculator MOVE_CALCULATOR = new RookMoveCalculator();

	//
	// ======================== CONSTRUCTORS =======================
	//
	
	public Rook(TeamColor color) {
		super(color, Rook.TYPE, Rook.MOVE_CALCULATOR);
	}
}
