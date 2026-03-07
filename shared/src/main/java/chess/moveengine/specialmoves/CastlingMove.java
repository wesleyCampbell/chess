package chess.moveengine.specialmoves;

import java.util.Collection;
import java.util.HashSet;

import chess.*;
import chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor;
import chess.moveengine.*;

public class CastlingMove extends SpecialMove {
	//
	// ============================== STATIC GLOBALS =============================
	//

	private static final ChessPosition SPECIAL_MOVE_RULE_CASTLING_KING_MOVE = new ChessPosition(0, 2);
	private static final ChessPosition SPECIAL_MOVE_RULE_CASTLING_ROOK_OFFSET = new ChessPosition(0, -1);

	private static final ChessPosition[] SPECIAL_MOVE_RULE_CASTLING_DIR_VECTORS = {
		new ChessPosition(0, -1),
		new ChessPosition(0, 1)
	};

	//
	// =============================== CONSTRUCTORS ========================
	//
	public CastlingMove(ChessMoveEngine moveEngine) {
		super(moveEngine);
	}

	//
	// =============================== MEMBER METHODS =======================
	//
	
	public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition pos) {
		HashSet<ChessMove> castleMoves = new HashSet<>();

		ChessPiece king = board.getPiece(pos);
		
		// If there is no piece and the piece isn't a king, we can't castle
		if (king == null || king.getPieceType() != PieceType.KING) {
			return castleMoves;	
		}

		// get all the necessary information
		TeamColor kingColor = king.getTeamColor();
		ChessTeamDatabase db = this.getMoveEngine().getTeamData(kingColor);
		Collection<ChessPosition> attackSquares;
		attackSquares = ChessMove.extractEndPositions(this.getMoveEngine().generateTeamAttacks(kingColor));
		
		// If the king has moved, it cannot castle.
		if (db.getMovedPieces().contains(king)) {
			return castleMoves;
		}
		
		// If the king is in check, it cannot castle
		if (attackSquares.contains(pos)) {
			return castleMoves;
		}

		// Go in all of the valid directions to see if a castling move can work.
		for (ChessPosition checkDir : SPECIAL_MOVE_RULE_CASTLING_DIR_VECTORS) {

			ChessPosition pointer = new ChessPosition(pos);
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
				castleMoves.addAll(this.generateCastleMoves(pos, pointer));

				break;
			}
		}

		return castleMoves;
	}

	/**
	 * Helper function that generates the set of ChessMoves comprised in castling
	 *
	 * @param kingPos The position of the king
	 * @param rookPos The position of the rook
	 */
	private HashSet<ChessMove> generateCastleMoves(ChessPosition kingPos, ChessPosition rookPos) {
		HashSet<ChessMove> castlingMoves = new HashSet<>();

		// Figure out what direction we need to castle in
		int colDirection = (kingPos.getColumn() < rookPos.getColumn()) ? 1 : -1;
		ChessPosition modDir = new ChessPosition(1, colDirection);

		// Calculate the movement vectors for each piece
		ChessPosition kingDir = new ChessPosition(SPECIAL_MOVE_RULE_CASTLING_KING_MOVE);
		kingDir.multiply(modDir);

		// Calculate the new king position
		ChessPosition newKingPos = new ChessPosition(kingPos);
		newKingPos.add(kingDir);

		// Add the new moves;
		castlingMoves.add(new ChessMove(kingPos, newKingPos, null));

		return castlingMoves;
	}

	@Override
	public boolean checkMove(ChessBoard board, ChessMove move) {
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
		return endPos.equals(SPECIAL_MOVE_RULE_CASTLING_KING_MOVE);
	}

	@Override
	public void makeMove(ChessBoard board, ChessMove move) {
		// STEP 1: determine the direction of the castle
		ChessPosition startPos = new ChessPosition(move.getStartPosition());
		ChessPosition endPos = new ChessPosition(move.getEndPosition());
		startPos.multiply(new ChessPosition(-1, -1));
		endPos.add(startPos);
		startPos.multiply(new ChessPosition(-1, -1));

		// dir = -1 if king moved left or down. 1 otherwise
		ChessPosition dirVector = new ChessPosition(SPECIAL_MOVE_RULE_CASTLING_KING_MOVE);
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
		ChessPosition rookEndSquare = new ChessPosition(SPECIAL_MOVE_RULE_CASTLING_ROOK_OFFSET);
		// if the king is moving left or down, the rook has gotta move the other way
		if (dirVector.getColumn() < 0 || dirVector.getRow() < 0) {
			rookEndSquare.multiply(new ChessPosition(-1, -1));
		} 
		rookEndSquare.add(move.getEndPosition());


		// STEP 4: Make the move
		ChessMove rookMove = new ChessMove(rookStartSquare, rookEndSquare, null);

		this.getMoveEngine().utilMakeMove(board, move, board.getPiece(startPos), false);
		this.getMoveEngine().utilMakeMove(board, rookMove, piece, true);  // Only update the database after the water is settled
	}
}

