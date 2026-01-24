package chess.pieces;

import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;
import chess.moveCalculator.BlackPawnMoveCalculator;
import chess.moveCalculator.PawnMoveCalculator;
import chess.moveCalculator.WhitePawnMoveCalculator;

/**
 * Represents a Pawn on the board.
 */

public class Pawn extends ChessPiece {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	private static final PieceType TYPE = PieceType.PAWN;


	//
	// ======================== CONSTRUCTORS =======================
	//
	
	public Pawn(TeamColor color) {
		super(color, Pawn.TYPE, PawnMoveCalculator.makeNewPawnMoveCalculator(color));
	}
}

