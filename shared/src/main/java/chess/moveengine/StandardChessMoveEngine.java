package chess.moveEngine;


import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

import static util.Debugger.debug;
import util.Pair;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class StandardChessMoveEngine implements ChessMoveEngine {
	//
	// ================================ MEMBER ATTRIBUTES =============================
	//
	
	private static final ChessPosition[] SPECIAL_MOVE_RULE_CASTLING_DIR_VECTORS = {
		new ChessPosition(0, -1),
		new ChessPosition(0, 1)
	};
	private static final ChessPosition SPECIAL_MOVE_RULE_CASTLING_KING_MOVE = new ChessPosition(0, 2);
	private static final ChessPosition SPECIAL_MOVE_RULE_CASTLING_ROOK_OFFSET = new ChessPosition(0, -1);

	private static final ChessPosition SPECIAL_MOVE_RULE_EM_PASSANT_PAWN_OFFSET = new ChessPosition(0, 1);
	private static final ChessPosition SPECIAL_MOVE_RULE_EM_PASSANT_PAWN_ATTACK = new ChessPosition(1, 1);
	private static final ChessPosition SPECIAL_MOVE_RULE_EM_PASSANT_TRIGGER_MOVE = new ChessPosition(2, 0);


	//
	// ================================ MEMBER ATTRIBUTES =============================
	//
	

	private Map<TeamColor, ChessTeamDatabase> chessTeamData;

	//
	// ================================ CONSTRUCTORS ==================================
	//
	
	public StandardChessMoveEngine(ChessBoard board) {
		this.chessTeamData = ChessGame.generateTeamDatabase(board);
	}
	
	//
	// ================================ MEMBER METHODS ==================================
	//

	public void updateDatabases(ChessBoard board) {
		for (ChessTeamDatabase db : this.chessTeamData.values()) {
			db.update(board);
		}
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
		allMoves.addAll(this.specialMoveRule_Castle(board, startPos));
		allMoves.addAll(this.specialMoveRule_EmPassant(board, startPos));

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
	private HashSet<ChessMove> specialMoveRule_EmPassant(ChessBoard board, ChessPosition startPos) {
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
	private boolean specialMoveCheck_EmPassant(ChessBoard board, ChessMove move) {
		
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
	private void specialMoveMake_EmPassant(ChessBoard board, ChessMove move, TeamColor color) {
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
		this._makeMove(board, move, pawn);
	}

	/**
	 * Checks to see if a move is a special castling move
	 *
	 * @param board The current game baord
	 * @param move The move to check
	 *
	 * @return true if the move is a castling move, false otherwise
	 */
	private boolean specialMoveCheck_Castle(ChessBoard board, ChessMove move) {
		// Collect relevant info
		ChessPosition startPos = new ChessPosition(move.getStartPosition());
		ChessPosition endPos = new ChessPosition(move.getEndPosition());


		ChessPiece piece = board.getPiece(startPos);

		// piece must exist in order to castle it
		if (piece == null) { return false; }

		// Piece must be a KING in order to castle
		if (piece.getPieceType() != PieceType.KING) { return false; }

		// Figure out the distance between the two points
		startPos.multiply(new ChessPosition(-1, -1));
		endPos.add(startPos);
		endPos.absValue();

		// If the absolute value of the difference vector is equal to the special castle move,
		// then the king can castle
		return endPos.equals(StandardChessMoveEngine.SPECIAL_MOVE_RULE_CASTLING_KING_MOVE);
	}

	/**
	 * Effectuates a special castling move
	 *
	 * @param board The current game board
	 * @param move The castling move that MUST be a valid castling move
	 */
	private void specialMoveMake_Castle(ChessBoard board, ChessMove move) {
		// STEP 1: determine the direction of the castle
		ChessPosition startPos = new ChessPosition(move.getStartPosition());
		ChessPosition endPos = new ChessPosition(move.getEndPosition());
		startPos.multiply(new ChessPosition(-1, -1));
		endPos.add(startPos);
		startPos.multiply(new ChessPosition(-1, -1));

		// dir = -1 if king moved left or down. 1 otherwise
		ChessPosition dirVector = new ChessPosition(StandardChessMoveEngine.SPECIAL_MOVE_RULE_CASTLING_KING_MOVE);
		dirVector.normalize();
		// if king moved left
		if (endPos.getColumn() < 0) {
			dirVector.multiply(new ChessPosition(1, -1));
		} 
		// if king moved down
		if (endPos.getRow() < 0) {
			dirVector.multiply(new ChessPosition(-1, 1));
		}

		// STEP 2: Go and fetch the rook piece
		ChessPosition rookStartSquare = new ChessPosition(startPos);
		ChessPiece piece = null;
		// assumes that there will be a rook in route
		while (piece == null || piece.getPieceType() != PieceType.ROOK) {
			rookStartSquare.add(dirVector);
			piece = board.getPiece(rookStartSquare);
		}

		// STEP 3: Figure out where the rook is going to sit
		ChessPosition rookEndSquare = new ChessPosition(StandardChessMoveEngine.SPECIAL_MOVE_RULE_CASTLING_ROOK_OFFSET);
		// if the king is moving left or down, the rook has gotta move the other way
		if (dirVector.getColumn() < 0 || dirVector.getRow() < 0) {
			rookEndSquare.multiply(new ChessPosition(-1, -1));
		} 
		rookEndSquare.add(move.getEndPosition());


		// STEP 4: Make the move
		ChessMove rookMove = new ChessMove(rookStartSquare, rookEndSquare, null);

		this._makeMove(board, move, board.getPiece(startPos), false);
		this._makeMove(board, rookMove, piece, true);  // Only update the database after the water is settled
	}

	/**
	 * The rules for determining whether a king can castle or not
	 *
	 * @param board The current game board
	 * @param startPos the position to check
	 *
	 * @return HashSet containing all the valid castling moves. Empty, if there are none.
	 */
	private HashSet<ChessMove> specialMoveRule_Castle(ChessBoard board, ChessPosition startPos) {
		HashSet<ChessMove> castleMoves = new HashSet<>();

		ChessPiece king = board.getPiece(startPos);

		// If there is no piece and the piece isn't a king, we can't castle
		if (king == null || king.getPieceType() != PieceType.KING) {
			return castleMoves;	
		}

		// get all the necessary information
		TeamColor kingColor = king.getTeamColor();
		ChessTeamDatabase db = this.chessTeamData.get(kingColor);
		Collection<ChessPosition> attackSquares = ChessMove.extractEndPositions(this.generateTeamAttacks(kingColor));
		
		// If the king has moved, it cannot castle.
		if (db.getMovedPieces().contains(king)) {
			return castleMoves;
		}
		
		// If the king is in check, it cannot castle
		if (attackSquares.contains(startPos)) {
			return castleMoves;
		}

		// Go in all of the valid directions to see if a castling move can work.
		for (ChessPosition checkDir : StandardChessMoveEngine.SPECIAL_MOVE_RULE_CASTLING_DIR_VECTORS) {

			ChessPosition pointer = new ChessPosition(startPos);
			pointer.add(checkDir);

			// Follow the direction vector until we hit the edge of the board
			while (board.isInBounds(pointer)) {
				// Check to see if there is a piece blocking the path
				ChessPiece piece = board.getPiece(pointer);
				if (piece == null) {
					// Check to see if the square is under attack
					// if there is, it is impossible to castle
					if (attackSquares.contains(pointer)) {  
						break;
					}

					// if there is no piece, we just gotta keep going
					pointer.add(checkDir);
				
					continue;
				}

				// if we hit a blocking piece that isn't a rook, castling isn't possible in
				// this direction.
				if (piece.getPieceType() != PieceType.ROOK) {
					break;
				}

				// Check to see if the rook has already moved
				if (db.pieceHasMoved(piece)) {
					break;
				}

				// There is a castling move here!
				castleMoves.addAll(this.generateCastleMoves(startPos, pointer));

				break;
			}
		}

		return castleMoves;
	}

	private HashSet<ChessMove> generateCastleMoves(ChessPosition kingPos, ChessPosition rookPos) {
		HashSet<ChessMove> castlingMoves = new HashSet<>();

		// Figure out what direction we need to castle in
		int colDirection = (kingPos.getColumn() < rookPos.getColumn()) ? 1 : -1;
		ChessPosition modDir = new ChessPosition(1, colDirection);

		// Calculate the movement vectors for each piece
		ChessPosition kingDir = new ChessPosition(StandardChessMoveEngine.SPECIAL_MOVE_RULE_CASTLING_KING_MOVE);
		kingDir.multiply(modDir);

		// Calculate the new king position
		ChessPosition newKingPos = new ChessPosition(kingPos);
		newKingPos.add(kingDir);

		// Add the new moves;
		castlingMoves.add(new ChessMove(kingPos, newKingPos, null));

		return castlingMoves;
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
	private void _makeMove(ChessBoard board, ChessMove move, ChessPiece piece2Move) {
		this._makeMove(board, move, piece2Move, true);
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
	private void _makeMove(ChessBoard board, ChessMove move, ChessPiece piece2Move, boolean updateMoveDatabase) {
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
		this._makeMove(board, move, movedPiece);

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
		if (specialMoveCheck_Castle(board, move)) {
			specialMoveMake_Castle(board, move);
		} else if (specialMoveCheck_EmPassant(board, move)) {
			specialMoveMake_EmPassant(board, move, activeTeamColor);
		} else {
			this._makeMove(board, move, piece);
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
	private HashSet<ChessMove> generateTeamAttacks(TeamColor teamColor) {
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
