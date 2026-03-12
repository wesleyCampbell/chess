package chess.moveengine;

import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;
import chess.moveengine.specialmoves.CastlingMove;
import chess.moveengine.specialmoves.EmPassantMove;
import chess.moveengine.specialmoves.SpecialMove;


import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

public class StandardChessMoveEngine implements ChessMoveEngine {
	//
	// ================================ MEMBER ATTRIBUTES =============================
	//
	

	private Map<TeamColor, ChessTeamDatabase> chessTeamData;
	private transient Map<String, SpecialMove> specialMoveCalculators;

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

	public Map<TeamColor, ChessTeamDatabase> getTeamDatabase() {
		return this.chessTeamData;
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
		for (SpecialMove specialMove : this.specialMoveCalculators.values()) {
			allMoves.addAll(specialMove.calculateMoves(board, startPos));
		}

		return allMoves;
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

		// STEP 3: Make move

		// Check to see if move qualifies for a special move 
		boolean specialMoveMade = false;
		for (SpecialMove specialMove : this.specialMoveCalculators.values()) {
			if (specialMove.checkMove(board, move)) {
				specialMove.makeMove(board, move);
				specialMoveMade = true;
				break;  // Only one special move allowed to be made at a time
			}
		}

		// If the move wasn't special, default to the normal move
		if (!specialMoveMade) {
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
