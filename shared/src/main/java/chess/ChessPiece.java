package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.lang.StringBuilder;

import chess.ChessGame.TeamColor;
import chess.moveCalculator.*;

/**
 * Represents a single chess piece
 */
public class ChessPiece {
	//
	// ============================== STATIC ATTRIBUTES ==========================
	//
	
    public static enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

	private static final Map<ChessPiece.PieceType, Character> PieceSymbols = 
		Map.of(
				PieceType.KING, 'K',
				PieceType.QUEEN, 'Q',
				PieceType.BISHOP, 'B',
				PieceType.KNIGHT, 'N',
				PieceType.ROOK, 'R',
				PieceType.PAWN, 'P'
			  );

	//
	// ========================= STATIC METHODS ==============================
	//
	
	/**
	 * Dereferences a character into its cooresponding piece type
	 *
	 * @param symbol: The symbol code. Valid options are 'K', 'Q', 'B', 'N', 'R', 'P'.
	 * @return The piece type or null, if it doesn't exist
	 */
	public static PieceType resolveChessPiece(char symbol) {
		for (Map.Entry<PieceType, Character> entry : PieceSymbols.entrySet()) {
			if (entry.getValue() == symbol) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Dereferences a piece type into its cooresponding character symbol
	 *
	 * @param type: The piece type
	 * @return The character representation of the piece.
	 */
	public static char resolveChessPiece(PieceType type) {
		return PieceSymbols.get(type);
	}
	
	//
	// ========================= MEMBER ATTRIBUTES ==============================
	//
	
	private PieceType type;
	private TeamColor pieceColor;
	private char pieceSymbol;

	//
	// ========================= CONSTRUCTORS ===================================
	//
	
	/**
	 * Constructor for ChessPiece class
	 *
	 * @param pieceColor: The team color of the new piece
	 * @param type: The type of chess piece, e.g. PAWN or ROOK.
	 */
	public ChessPiece(TeamColor pieceColor, PieceType type) {
		this.type = type;
		this.pieceColor = pieceColor;
		this.pieceSymbol = ChessPiece.PieceSymbols.get(type);
	}

	// 
	// ============================ MEMBER METHODS ==============================
	//
	
	/**
	 * Getter for the piece's color
	 *
	 * @return Color of the chess piece
	 */
    public ChessGame.TeamColor getTeamColor() {
		return this.pieceColor;
    }

    /**
	 * Getter for the piece's type
	 *
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
		PawnMoveCalculator calc;
		calc = new PawnMoveCalculator(this.getTeamColor());

		return calc.calculateMoves(board, this, myPosition);
    }

	/**
	 * Checks to see if an object is equal to the chess piece.
	 *
	 * Note that for it to return true, the object must be a 
	 * ChessPiece with the same color and type.
	 *
	 * @return true if equal, false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		// obj must exist and must be a ChessPiece
		if (obj == null) { return false; }
		if (obj.getClass() != ChessPiece.class) { return false; }

		ChessPiece other = (ChessPiece) obj;

		// Compare the piece type and color
		if (this.type != other.getPieceType() ||
				this.pieceColor != other.getTeamColor()) {
					return false;
			}

		return true;
	}

	/**
	 * Provids a hashing method for the ChessPiece.
	 *
	 * Is determined by its type and color.
	 *
	 * @return int: the hash code
	 */
	@Override
	public int hashCode() {
		int out = this.type.hashCode() * 31;
		out += this.pieceColor.hashCode();
		return out;
	}

	/**
	 * Provides a string representation of a ChessPiece
	 *
	 * @return String representation.
	 */
	@Override
	public String toString() {
		StringBuilder outStr = new StringBuilder();

		switch(this.pieceColor) {
			case BLACK:
				outStr.append("Black ");
				break;
			case WHITE:
				outStr.append("White ");
				break;
			default:
				outStr.append("Unknown Color ");
				break;
		}

		outStr.append(this.pieceSymbol);
		
		return outStr.toString();
	}
}
