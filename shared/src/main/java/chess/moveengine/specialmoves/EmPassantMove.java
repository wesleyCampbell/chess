package chess.moveengine.specialmoves;

import java.util.Collection;
import java.util.HashSet;

import chess.*;
import chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor;
import chess.moveengine.*;

import util.Pair;

public class EmPassantMove extends SpecialMove {
	// 
	// ============================= STATIC GLOBALS ======================================
	//
	
	private static final ChessPosition SPECIAL_MOVE_RULE_EM_PASSANT_PAWN_OFFSET = new ChessPosition(0, 1);
	private static final ChessPosition SPECIAL_MOVE_RULE_EM_PASSANT_PAWN_ATTACK = new ChessPosition(1, 1);
	private static final ChessPosition SPECIAL_MOVE_RULE_EM_PASSANT_TRIGGER_MOVE = new ChessPosition(2, 0);

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
		// The piece has to be a pawn
		ChessPiece pawn = board.getPiece(pos);
		HashSet<ChessMove> emPassantMoves = new HashSet<>();

		if (pawn == null || pawn.getPieceType() != PieceType.PAWN) {
			return emPassantMoves;
		}

		// Check all enemy team databases to see if there was a pawn that performed
		// double jump that is next to the current pawn
		for (ChessTeamDatabase db : this.getMoveEngine().getTeamDatabase().values()) {
			if (db.getTeamColor() == pawn.getTeamColor()) {
				continue;
			}

			Pair<ChessPiece, ChessMove> lastMovedPiece = db.getLastMovedPiece();

			// STEP 1: verify that the piece is a pawn

			if (lastMovedPiece == null || 
					lastMovedPiece.getFirst().getPieceType() != PieceType.PAWN) {
				continue;
			}

			// STEP 2: Verify that the move vector from the last moved piece matches
			// the trigger move
			ChessMove lastMove = lastMovedPiece.getSecond();
			ChessPosition lastMoveVec = new ChessPosition(lastMove.getEndPosition());
			lastMoveVec.subtract(lastMove.getStartPosition());


			if (!lastMoveVec.absValueCopy().equals(SPECIAL_MOVE_RULE_EM_PASSANT_TRIGGER_MOVE)) {
				continue;
			}

			// STEP 3: Check to see if the last moved pawn is the requisite distance to perform em passant
			ChessPosition distance = new ChessPosition(pos);
			distance.subtract(lastMovedPiece.getSecond().getEndPosition());
			

			if (!distance.absValueCopy().equals(SPECIAL_MOVE_RULE_EM_PASSANT_PAWN_OFFSET)) {
				continue;
			}

			// STEP 4: Make the em passant rule as all conditions are met
			ChessPosition pawnEndPos = new ChessPosition(lastMove.getEndPosition());
			lastMoveVec.normalize();
			lastMoveVec.multiply(new ChessPosition(-1, -1));

			pawnEndPos.add(lastMoveVec);

			ChessMove emPassantMove = new ChessMove(pos, pawnEndPos, null);


			emPassantMoves.add(emPassantMove);
		}

		return emPassantMoves;
	}

	@Override 
	public boolean checkMove(ChessBoard board, ChessMove move) {
		
		// move must be on square containint pawn
		ChessPosition startPos = new ChessPosition(move.getStartPosition());
		ChessPiece pawn = board.getPiece(startPos);

		if (pawn == null || pawn.getPieceType() != PieceType.PAWN) {
			return false;
		}

		// Caluclate the generalized move vector from the move
		ChessPosition moveVec = new ChessPosition(move.getEndPosition());
		moveVec.subtract(move.getStartPosition());

		// If the move vector does not match the em passant attack move, it cannot be em passant
		if (!moveVec.absValueCopy().equals(SPECIAL_MOVE_RULE_EM_PASSANT_PAWN_ATTACK)) {
			return false;
		}

		// Else, double check that there is a pawn adjacent that just moved
		// necessary because the em passant rule is default equal to a standard attack move
		for (ChessTeamDatabase db: this.getMoveEngine().getTeamDatabase().values()) {
			Pair<ChessPiece, ChessMove> lastMovedPiece = db.getLastMovedPiece();

			// Must be a pawn
			if (lastMovedPiece == null || lastMovedPiece.getFirst().getPieceType() != PieceType.PAWN) {
				continue;
			}

			// must be adjacent in the direction of the move
			ChessPosition enemyPos = new ChessPosition(lastMovedPiece.getSecond().getEndPosition());
			enemyPos.subtract(startPos);
			enemyPos.absValue();
			if (!enemyPos.equals(SPECIAL_MOVE_RULE_EM_PASSANT_PAWN_OFFSET)) {
				continue;
			}

			// all qualifications met, must be a em passant rule
			return true;
		}

		return false;
	}

	@Override
	public void makeMove(ChessBoard board, ChessMove move) {
		// STEP 0: get info
		ChessPosition startPos = move.getStartPosition();
		ChessPosition endPos = move.getEndPosition();
		
		ChessPiece pawn = board.getPiece(startPos);

		// STEP 1: Find the pawn to remove
		ChessPosition modDir; 
		if (endPos.getColumn() - startPos.getColumn() < 0) {
			modDir = new ChessPosition(-1, -1);
		} else {
			modDir = new ChessPosition(1, 1);
		}

		ChessPosition attackPawnPos = new ChessPosition(SPECIAL_MOVE_RULE_EM_PASSANT_PAWN_OFFSET);
		attackPawnPos.multiply(modDir);

		attackPawnPos.add(startPos);

		ChessPiece attackPawn = board.getPiece(attackPawnPos);

		// STEP 2: Remove the attacked attackPawn
		this.getMoveEngine().getTeamData(pawn.getTeamColor()).addCapturedPiece(attackPawn);
		board.removePiece(attackPawnPos);

		// STEP 3: Move the em passant pawn
		this.getMoveEngine().utilMakeMove(board, move, pawn, true);
	}
}
