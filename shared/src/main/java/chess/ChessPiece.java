package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

	PieceType type;
	ChessGame.TeamColor teamColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
		this.type = type;
		this.teamColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
		return this.teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
		return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

	@Override
	public boolean equals(Object obj) {
		// Obj must exist and be ChessPiece
		if (obj == null) { return false; }
		if (obj.getClass() != ChessPiece.class) { return false; }

		ChessPiece otherPiece = (ChessPiece) obj;

		// If piece is the same type and color as the other, they are equal
		if (this.type == otherPiece.getPieceType()) {
			if (this.teamColor == otherPiece.getTeamColor()) {
				return true;
			}
		}

		return false;
	}

	@Override 
	public int hashCode() {
		int out = 0;
		switch(this.type) {
			case PAWN:
				out++;
			case BISHOP:
				out++;
			case KNIGHT:
				out++;
			case ROOK:
				out++;
			case QUEEN:
				out++;
			case KING:
				out++;
				break;
			default:
				out = 100;
		}
		out *= 31;

		switch(this.teamColor) {
			case BLACK:
				out++;
			case WHITE:
				out++;
				break;
			default:
				out += 100;
		}

		return out;
	}
}
