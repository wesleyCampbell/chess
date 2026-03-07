package chess.moveengine.specialmoves;

import java.util.Collection;
import java.util.HashSet;

import chess.*;
import chess.moveengine.*;

public class EmPassantMove extends SpecialMove {
	// 
	// ========================== CONSTRUCTORS ===============================
	//
	public EmPassantMove(ChessMoveEngine moveEngine) {
		super(moveEngine);
	}

	//
	// =============================== MEMBER METHODS ========================
	//
	
	@Override
	public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition pos) {
		return new HashSet<ChessMove>();
	}

	@Override 
	public boolean checkMove(ChessBoard board, ChessMove move) {
		return false;
	}

	@Override
	public void makeMove(ChessBoard board, ChessMove move) {

	}

}
