package chess.pieces;

import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;
import chess.movecalculator.BlackPawnMoveCalculator;
import chess.movecalculator.PawnMoveCalculator;
import chess.movecalculator.WhitePawnMoveCalculator;

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

