package chess.moveengine;


import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;
import chess.moveengine.specialmoves.CastlingMove;
import chess.moveengine.specialmoves.EmPassantMove;
import chess.moveengine.specialmoves.SpecialMove;

import static util.Debugger.debug;
import util.Pair;

import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

public class StandardChessMoveEngine implements ChessMoveEngine {
	//
	// ================================ MEMBER ATTRIBUTES =============================
	//
	

	private static final ChessPosition SPECIAL_MOVE_RULE_EM_PASSANT_PAWN_OFFSET = new ChessPosition(0, 1);
	private static final ChessPosition SPECIAL_MOVE_RULE_EM_PASSANT_PAWN_ATTACK = new ChessPosition(1, 1);
	private static final ChessPosition SPECIAL_MOVE_RULE_EM_PASSANT_TRIGGER_MOVE = new ChessPosition(2, 0);


	//
	// ================================ MEMBER ATTRIBUTES =============================
	//
	

	private Map<TeamColor, ChessTeamDatabase> chessTeamData;
	private Map<String, SpecialMove> specialMoveCalculators;

	//
	// ================================ CONSTRUCTORS ==================================
	//
	
	public StandardChessMoveEngine(ChessBoard board) {
		this.chessTeamData = ChessGame.generateTeamDatabase(board);
		this.specialMoveCalculators = new HashMap<>();

		this.generateSpecialMoves();
	}

	/**
	 * Helper generation function that creates the objects
	 * necessary to calculate special moves.
	 */
	private void generateSpecialMoves() {
		this.specialMoveCalculators.put("castle", new CastlingMove(this));
		this.specialMoveCalculators.put("emPassant", new EmPassantMove(this));
	}
	
	//
	// ================================ MEMBER METHODS ==================================
	//

	public void updateDatabases(ChessBoard board) {
		for (ChessTeamDatabase db : this.chessTeamData.values()) {
			db.update(board);
		}
	}

	public ChessTeamDatabase getTeamData(TeamColor color) {
		return this.chessTeamData.get(color);
	}
	
	public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition startPos) {
		HashSet<ChessMove> allMoves = new HashSet<>();

		// STEP 1: Get the default moves of the piece on the square, if it exists
		ChessPiece piece = board.getPiece(startPos);

		// If there is no piece on the square, just return empty set
		if (piece == null) {
			return allMoves;
		}

		Collection<ChessMove> defaultMoves = piece.pieceMoves(board, startPos);

		// STEP 2: Take out moves that put king in check
		for (ChessMove move : defaultMoves) {
			if (this.moveRevealsCheck(board, move)) {
				continue;
			}

			// If the move doesn't reveal check, make the move
			allMoves.add(move);
		}

		// STEP 3: Add special moves, if necessary
		allMoves.addAll(this.specialMoveRuleCastle(board, startPos));
		allMoves.addAll(this.specialMoveRuleEmPassant(board, startPos));

		return allMoves;
	}

	/**
	 * Calculates the special em passant rule for a piece on a square, if applicable
	 *
	 * @param board The current game board
	 * @param startPos The starting position of the piece
	 *
	 * @return The em passant move
	 */
	private HashSet<ChessMove> specialMoveRuleEmPassant(ChessBoard board, ChessPosition startPos) {
		// The piece has to be a pawn
		ChessPiece pawn = board.getPiece(startPos);
		HashSet<ChessMove> emPassantMoves = new HashSet<>();

		if (pawn == null || pawn.getPieceType() != PieceType.PAWN) {
			return emPassantMoves;
		}

		// Check all enemy team databases to see if there was a pawn that performed
		// double jump that is next to the current pawn
		for (ChessTeamDatabase db : this.chessTeamData.values()) {
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


			if (!lastMoveVec.absValueCopy().equals(StandardChessMoveEngine.SPECIAL_MOVE_RULE_EM_PASSANT_TRIGGER_MOVE)) {
				continue;
			}

			// STEP 3: Check to see if the last moved pawn is the requisite distance to perform em passant
			ChessPosition distance = new ChessPosition(startPos);
			distance.subtract(lastMovedPiece.getSecond().getEndPosition());
			

			if (!distance.absValueCopy().equals(StandardChessMoveEngine.SPECIAL_MOVE_RULE_EM_PASSANT_PAWN_OFFSET)) {
				continue;
			}

			// STEP 4: Make the em passant rule as all conditions are met
			ChessPosition pawnEndPos = new ChessPosition(lastMove.getEndPosition());
			lastMoveVec.normalize();
			lastMoveVec.multiply(new ChessPosition(-1, -1));

			pawnEndPos.add(lastMoveVec);

			ChessMove emPassantMove = new ChessMove(startPos, pawnEndPos, null);


			emPassantMoves.add(emPassantMove);
		}

		return emPassantMoves;
	}

	/**
	 * Checks to see if a move is a special em passant move
	 *
	 * @param board The current game board
	 * @param move The move to check
	 *
	 * @return true if the move is a castling move, false otherwise
	 */
	private boolean specialMoveCheckEmPassant(ChessBoard board, ChessMove move) {
		
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
		if (!moveVec.absValueCopy().equals(StandardChessMoveEngine.SPECIAL_MOVE_RULE_EM_PASSANT_PAWN_ATTACK)) {
			return false;
		}

		// Else, double check that there is a pawn adjacent that just moved
		// necessary because the em passant rule is default equal to a standard attack move
		for (ChessTeamDatabase db: this.chessTeamData.values()) {
			Pair<ChessPiece, ChessMove> lastMovedPiece = db.getLastMovedPiece();

			// Must be a pawn
			if (lastMovedPiece == null || lastMovedPiece.getFirst().getPieceType() != PieceType.PAWN) {
				continue;
			}

			// must be adjacent in the direction of the move
			ChessPosition enemyPos = new ChessPosition(lastMovedPiece.getSecond().getEndPosition());
			enemyPos.subtract(startPos);
			enemyPos.absValue();
			if (!enemyPos.equals(StandardChessMoveEngine.SPECIAL_MOVE_RULE_EM_PASSANT_PAWN_OFFSET)) {
				continue;
			}

			// all qualifications met, must be a em passant rule
			return true;
		}

		return false;
	}

	/**
	 * Makes an em passant rule. This assumes that it is valid, so use at your own risk.
	 *
	 * @param board The current chess board
	 * @param move The em passant move
	 * @param color The pawn's color
	 */
	private void specialMoveMakeEmPassant(ChessBoard board, ChessMove move, TeamColor color) {
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

		ChessPosition attackPawnPos = new ChessPosition(StandardChessMoveEngine.SPECIAL_MOVE_RULE_EM_PASSANT_PAWN_OFFSET);
		attackPawnPos.multiply(modDir);

		attackPawnPos.add(startPos);

		ChessPiece attackPawn = board.getPiece(attackPawnPos);

		// STEP 2: Remove the attacked attackPawn
		this.chessTeamData.get(pawn.getTeamColor()).addCapturedPiece(attackPawn);
		board.removePiece(attackPawnPos);

		// STEP 3: Move the em passant pawn
		this.utilMakeMove(board, move, pawn);
	}

	/**
	 * Checks to see if a move is a special castling move
	 *
	 * @param board The current game baord
	 * @param move The move to check
	 *
	 * @return true if the move is a castling move, false otherwise
	 */
	private boolean specialMoveCheckCastle(ChessBoard board, ChessMove move) {
		return this.specialMoveCalculators.get("castle").checkMove(board, move);
	}

	/**
	 * Effectuates a special castling move
	 *
	 * @param board The current game board
	 * @param move The castling move that MUST be a valid castling move
	 */
	private void specialMoveMakeCastle(ChessBoard board, ChessMove move) {
		this.specialMoveCalculators.get("castle").makeMove(board, move);
	}

	/**
	 * The rules for determining whether a king can castle or not
	 *
	 * @param board The current game board
	 * @param startPos the position to check
	 *
	 * @return HashSet containing all the valid castling moves. Empty, if there are none.
	 */
	private Collection<ChessMove> specialMoveRuleCastle(ChessBoard board, ChessPosition startPos) {
		return this.specialMoveCalculators.get("castle").calculateMoves(board, startPos);
	}


	public boolean isMoveValid(ChessBoard board, ChessMove move) {
		ChessPosition startPos = move.getStartPosition();

		Collection<ChessMove> validMoves = this.validMoves(board, startPos);

		if (!validMoves.contains(move)) {
			return false;
		}
	
		return true;
	}

	/**
	 * Performs a chess move, no questions asked. A dumb utility function. Use at your risk.
	 *
	 * Note that this method will only update the team movement databases as to allow testing.
	 * All other team databases will need to be updated by the caller.
	 *
	 * Can be used to perform an actual chess move, in which case the caller will want to
	 * update the cooresponding team databases.
	 *
	 * Can also be used to test if a move is valid as the movement databases are updated.
	 * If used in this way, make sure to undo the move.
	 *
	 * @param move The move to make
	 * @param piece2Move The piece being moved
	 */
	private void utilMakeMove(ChessBoard board, ChessMove move, ChessPiece piece2Move) {
		this.utilMakeMove(board, move, piece2Move, true);
	}

	/**
	 * Performs a chess move, no questions asked. A dumb utility function. Use at your risk.
	 *
	 * Note that this method will only update the team movement databases as to allow testing.
	 * All other team databases will need to be updated by the caller.
	 *
	 * Can be used to perform an actual chess move, in which case the caller will want to
	 * update the cooresponding team databases.
	 *
	 * Can also be used to test if a move is valid as the movement databases are updated.
	 * If used in this way, make sure to undo the move.
	 *
	 * @param move The move to make
	 * @param piece2Move The piece being moved
	 * @param updateMoveDatabase If true, will update the database. Won't, otherwise
	 */
	public void utilMakeMove(ChessBoard board, ChessMove move, ChessPiece piece2Move, boolean updateMoveDatabase) {
		ChessPosition startPos = move.getStartPosition();
		ChessPosition endPos = move.getEndPosition();

		// Check to see if there is a promotion to do
		PieceType promotionType = move.getPromotionPiece();
		if (promotionType != null) {
			piece2Move = ChessPiece.makeNewPiece(piece2Move.getTeamColor(), promotionType);
		}

		// The actual move
		board.addPiece(startPos, null);
		board.addPiece(endPos, piece2Move);

		// Update the move databases
		if (updateMoveDatabase) {
			this.updateDatabases(board);
		}
	}
	
	public boolean moveRevealsCheck(ChessBoard board, ChessMove move) {
		Boolean ret = false;
		ChessPosition startPos = move.getStartPosition();
		ChessPosition endPos = move.getEndPosition();

		// Copy the data that will be overwritten
		ChessPiece capturedPiece = board.getPiece(endPos);
		ChessPiece movedPiece = board.getPiece(startPos);

		// Make the move to see if it puts the king in check.
		this.utilMakeMove(board, move, movedPiece);

		// If the king is in check, the move revealed check.
		if (this.isInCheck(movedPiece.getTeamColor())) {
			ret = true;	
		}

		// undo the move
		board.addPiece(startPos, movedPiece);
		board.addPiece(endPos, capturedPiece);
		this.updateDatabases(board);
		
		return ret;
	}


	public void makeMove(ChessBoard board, ChessMove move, TeamColor activeTeamColor) throws InvalidMoveException{
		// STEP 1: Check to see if there is a valid piece at the start position
		ChessPosition startPos = move.getStartPosition();
		ChessPiece piece = board.getPiece(startPos);

		if (piece == null || piece.getTeamColor() != activeTeamColor) {
			String err = String.format("Piece at square %s either doesn't exist or it is not their turn!", startPos);
			throw new InvalidMoveException(err);
		}

		// STEP 2: Verify that move is valid
		if (!this.isMoveValid(board, move)) {
			String err = String.format("Move %s is invalid!", move);
			throw new InvalidMoveException(err);
		}

		// STEP 3: Check to see if move is special:
		if (specialMoveCheckCastle(board, move)) {
			specialMoveMakeCastle(board, move);
		} else if (specialMoveCheckEmPassant(board, move)) {
			specialMoveMakeEmPassant(board, move, activeTeamColor);
		} else {
			this.utilMakeMove(board, move, piece);
		}

		// STEP 4: Update databases
		ChessTeamDatabase db = this.chessTeamData.get(piece.getTeamColor());
		db.addMovedPiece(piece, move);

		ChessPiece capturePiece = board.getPiece(move.getEndPosition());
		if (capturePiece != null) { 
			db.addCapturedPiece(capturePiece);
		}
	}

	/** 
	 * Returns a union of all attack moves except for one team color's
	 *
	 * @param excludeColor The color to exclude from the set
	 *
	 * @return A set of all attack moves from all teams except the one provided
	 */
	public HashSet<ChessMove> generateTeamAttacks(TeamColor teamColor) {
		HashSet<ChessMove> attackMoves = new HashSet<>();

		for (ChessTeamDatabase db : this.chessTeamData.values()) {
			// Exclude the provided team
			if (db.getTeamColor() == teamColor) { continue; }

			attackMoves.addAll(db.getAttackMoveSet());
		}

		return attackMoves;
	}

	/**
	 * Gets all enemy attacks that target a set of squares
	 *
	 * @param teamColor The team being targeted
	 * @param squares A collection of squares to target
	 *
	 * @return A hashset containing all the attacks that target the squares
	 */
	private HashSet<ChessMove> getMovesTargetingSquare(TeamColor teamColor,
													   Collection<ChessPosition> squares) {
		HashSet<ChessMove> attackMoves = this.generateTeamAttacks(teamColor);
		HashSet<ChessMove> outMoves = new HashSet<>();

		for (ChessMove move : attackMoves) {
			if (squares.contains(move.getEndPosition())) {
				outMoves.add(move);
			}
		}

		return outMoves;
	}

	public boolean isInCheck(TeamColor teamColor){
		HashSet<ChessPosition> kingPos = this.chessTeamData.get(teamColor).getKingPos();

		// Generate attacks from all teams except teamColor and take out the endPositions
		HashSet<ChessPosition> attackSquares = ChessMove.extractEndPositions(this.generateTeamAttacks(teamColor));

		// Check to see if any of the king positions are in the attack squares
		for (ChessPosition king : kingPos) {
			if (attackSquares.contains(king)) { return true; }
		}

		return false;
	}

	public boolean isInCheckmate(ChessBoard board, TeamColor teamColor){
		// Must be in check to be in checkmate
		if (!this.isInCheck(teamColor)) {
			return false; 
		}

		HashSet<ChessPosition> kingPos = this.chessTeamData.get(teamColor).getKingPos();

		// Get the enemy attacks targeting the king
		HashSet<ChessPosition> attackSquares = ChessMove.extractEndPositions(this.generateTeamAttacks(teamColor));

		for (ChessPosition pos : kingPos) {
			ChessPiece king = board.getPiece(pos);
			// Get all the move squares
			Collection<ChessMove> moves = king.pieceMoves(board, pos);
			
			HashSet<ChessPosition> moveSquares = ChessMove.extractEndPositions(moves);

			// Remove all of the attackSquares from the moveSquares to see if the king has any legal moves
			moveSquares.removeAll(attackSquares);

			// If the king's move set isn't empty, it can escape check
			if (!moveSquares.isEmpty()) {
				return false;
			}

			// Check to see if the king can have a piece capture the attacking piece.
			HashSet<ChessPosition> movesTargetingKing = ChessMove.extractStartPositions(this.getMovesTargetingSquare(teamColor, kingPos));

			for (ChessMove defenseMove : this.chessTeamData.get(teamColor).getAttackMoveSet()) {
				// If there is a piece that can kill the attacking team
				if (movesTargetingKing.contains(defenseMove.getEndPosition())) {
					// Check to see if taking out the piece resolves the check attack
					if (this.moveRevealsCheck(board, defenseMove)) {
						continue;
					}

					return false;
				}
			}
		}

		return true;
	}

	public boolean isInStalemate(ChessBoard board, TeamColor teamColor) {
		// You can't be in check and be in stalemate
		if (this.isInCheck(teamColor)) { return false; }

		// Fetch all the available moves
		HashSet<ChessMove> allMoves = this.chessTeamData.get(teamColor).getMoveSet();

		// If there is a move that can escape check, the team is not in stalemate
		for (ChessMove move : allMoves) {
			if (!this.moveRevealsCheck(board, move)) {
				return false;
			}
		}

		// There are no moves
		return true;

	}

	public Map<TeamColor, ChessTeamDatabase> getChessTeamDatabase() {
		return this.chessTeamData;
	}
}
