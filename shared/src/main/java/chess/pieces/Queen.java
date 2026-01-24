package chess.pieces;

import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;
import chess.moveCalculator.ChessPieceMoveCalculator;
import chess.moveCalculator.QueenMoveCalculator;

/**
 * Represents a Pawn on the board.
 */

public class Queen extends ChessPiece {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	private static final PieceType TYPE = PieceType.QUEEN;
	private static final ChessPieceMoveCalculator MOVE_CALCULATOR = new QueenMoveCalculator();

	//
	// ======================== CONSTRUCTORS =======================
	//
	
	public Queen(TeamColor color) {
		super(color, Queen.TYPE, Queen.MOVE_CALCULATOR);
	}
}

