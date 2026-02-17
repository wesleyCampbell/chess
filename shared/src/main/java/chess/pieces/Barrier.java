package chess.pieces;

import chess.ChessPiece;
import chess.ChessGame.TeamColor;
import chess.moveCalculator.ChessPieceMoveCalculator;
import chess.moveCalculator.ImmobileMoveCalculator;

public class Barrier extends ChessPiece {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	private static final PieceType TYPE = PieceType.IMMOBILE;
	private static final TeamColor COLOR = TeamColor.UTILITY;
	private static final ChessPieceMoveCalculator MOVE_CALCULATOR = new ImmobileMoveCalculator();

	//
	// ======================== CONSTRUCTORS =======================
	//
	
	public Barrier() {
		super(COLOR, TYPE, MOVE_CALCULATOR); 
	}
}	
