package chess;

import java.util.HashSet;
import java.util.Collection;

import chess.ChessPiece.PieceType;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

	//
	// ======================== STATIC METHODS =========================
	//
	
	/**
	 * Returns a HashSet of the start positions of a collection of chessmoves
	 *
	 * @param moves A Collection of chess moves
	 *
	 * @return A Hashset of all the start positions of the ChessMove Collection
	 */
	public static HashSet<ChessPosition> extractStartPositions(Collection<ChessMove> moves) {
		HashSet<ChessPosition> startPositions = new HashSet<>();

		for (ChessMove move: moves) {
			startPositions.add(move.getStartPosition());
		}

		return startPositions;
	}

	/**
	 * Returns a HashSet of the end positions of a collection of chess moves
	 *
	 * @param moves A Collection of ChessMove objects
	 *
	 * @return A HashSet of all the end positions of the ChessMove Collection
	 */
	public static HashSet<ChessPosition> extractEndPositions(Collection<ChessMove> moves) {
		HashSet<ChessPosition> endPositions = new HashSet<>();

		for (ChessMove move : moves) {
			endPositions.add(move.getEndPosition());
		}

		return endPositions;
	}

	//
	// ======================== MEMBER ATTRIBUTES =========================
	//
	
	private ChessPosition startPos;
	private ChessPosition endPos;
	private ChessPiece.PieceType promotionPiece;

	//
	// ======================== CONSTRUCTORS ==============================
	//

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
		this.startPos = startPosition;
		this.endPos = endPosition;
		this.promotionPiece = promotionPiece;
    }

	/**
	 * Copy constructor using another move and a different promotion piece type
	 *
	 * @param other The ChessMove to copy
	 * @param promotionPiece The new Promotion piece type
	 */
	public ChessMove(ChessMove other, PieceType promotionPiece) {
		this.startPos = other.getStartPosition();
		this.endPos = other.getEndPosition();
		this.promotionPiece = promotionPiece;
	}

	//
	// ======================== MEMBER METHODS =============================
	//
	
    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
		return this.startPos;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
		return this.endPos;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
		return this.promotionPiece;
    }

	/**
	 * Overriden equality method
	 *
	 * Evaluates equality based upon the object's startPos, endPos, and promotionPiece.
	 *
	 * @param obj: The other ChessMove object
	 */
	@Override
	public boolean equals(Object obj) {
		// obj must exist and must be ChessMove object
		if (obj == null || obj.getClass() != ChessMove.class) {
			return false;
		}

		ChessMove other = (ChessMove)obj;
		// Check for equality of member attributes
		// startPos and endPos guaranteed to not be null.
		if (!this.startPos.equals(other.getStartPosition()) ||
			!this.endPos.equals(other.getEndPosition()) ||
			this.promotionPiece != other.getPromotionPiece()) {
				return false;
		}
		
		return true;
	}

	/**
	 * Overriden hash code method
	 *
	 * Returns a unique hash based on its start position, end position, and promotion piece.
	 *
	 * @return the unique hash code
	 */
	@Override
	public int hashCode() {
		int hash = this.startPos.hashCode() * 31;
		hash += this.endPos.hashCode();
		hash *= 31;

		hash += this.promotionPiece != null ? this.promotionPiece.hashCode() : 3;

		return hash * 31;
	}

	/**
	 * Provides a string representation of a ChessMove
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		StringBuilder outStr = new StringBuilder();

		outStr.append(this.startPos.toString());
		outStr.append("->");
		outStr.append(this.endPos.toString());

		if (this.promotionPiece != null) {
			outStr.append(": ");
			outStr.append(this.promotionPiece.toString());
		}
 

		return outStr.toString();
	}
}
