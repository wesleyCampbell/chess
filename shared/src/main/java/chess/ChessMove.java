package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

	private ChessPosition startPos;
	private ChessPosition endPos;
	private ChessPiece.PieceType promotionPiece;


    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
		this.startPos = startPosition;
		this.endPos = endPosition;
		this.promotionPiece = promotionPiece;
    }

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
	 * Overrides the equality between two objects.
	 * 
	 * Will return true if the other object is a ChessMove object with the same:
	 * - startPos
	 * - endPos
	 * - promotionPiece
	 *
	 * @return true if equal, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		// Must exist and must be ChessMove object
		if (obj == null) { return false; }
		if (obj.getClass() != ChessMove.class) { return false; }

		ChessMove other = (ChessMove) obj;

		if (this.startPos.equals(other.getStartPosition()) &&
				this.endPos.equals(other.getEndPosition()) &&
				this.promotionPiece == other.getPromotionPiece()) {
			return true;
		}

		// If they are different, return false
		return false;
	}

	/**
	 * Returns string representation of ChessMove object
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		StringBuilder outStr = new StringBuilder();
		outStr.append("{ Start: ");
		outStr.append(this.startPos.toString());
		outStr.append("; End: ");
		outStr.append(this.endPos.toString());
		if (this.promotionPiece != null) {
			outStr.append("; Promotion Piece: ");
			outStr.append(this.promotionPiece.toString());
		}
		outStr.append(" }");

		return outStr.toString();
	}

	/**
	 * Hashing method for the ChessMove item
	 *
	 * Based upon its start position, end position, and promotionPiece.
	 *
	 * @return the hash code
	 */
	public int hashCode() {
		int hash = 0;
		hash += this.startPos.hashCode() * 31;
		hash += this.endPos.hashCode();
		hash *= 31;

		if (this.promotionPiece != null) {
			hash += this.promotionPiece.hashCode();
			hash *= 31;
		}

		return hash;
	}
}
