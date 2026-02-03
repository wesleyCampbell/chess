package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import chess.ChessGame.TeamColor;
import chess.moveCalculator.BishopMoveCalculator;
import chess.moveCalculator.ChessPieceMoveCalculator;
import chess.moveCalculator.KingMoveCalculator;
import chess.moveCalculator.KnightMoveCalculator;
import chess.moveCalculator.PawnMoveCalculator;
import chess.moveCalculator.QueenMoveCalculator;
import chess.moveCalculator.RookMoveCalculator;
import chess.pieces.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
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
	
	private static Map<PieceType, Character> typeSymbolMap = Map.of(
		PieceType.KING, 'K',
		PieceType.QUEEN, 'Q',
		PieceType.ROOK, 'R',
		PieceType.BISHOP, 'B',
		PieceType.KNIGHT, 'N',
		PieceType.PAWN, 'P'
	);

	//
	// ======================== STATIC METHODS =======================
	//

	/**
	 * Generates the correct chessPiece based on a input type;
	 *
	 * @param color The chess piece color
	 * @param type The chess piece type
	 *
	 * @return A child of the ChessPiece object
	 */
	public static ChessPiece makeNewPiece(TeamColor color, PieceType type) {
		// If the type is null, then there is no piece to make.
		if (type == null) { return null; }

		switch(type) {
			case KING:
				return new King(color);
			case QUEEN:
				return new Queen(color);
			case BISHOP:
				return new Bishop(color);
			case KNIGHT:
				return new Knight(color);
			case ROOK:
				return new Rook(color);
			case PAWN:
				return new Pawn(color);
			default:
				return null;
		}
	}
	
	/**
	 * Resolves a character symbol into its cooresponding pieceType
	 *
	 * @param symbol the character symbolizing the pieceType
	 * @return pieceType or null, if the symbol doesn't match any known type.
	 */
	public static PieceType resolveChessType(char symbol) {
		for (Map.Entry<PieceType, Character> entry : ChessPiece.typeSymbolMap.entrySet()) {
			if (entry.getValue() == symbol) {
				return entry.getKey();
			}
		}
		return null;
	}	

	/**
	 * Resolves a PieceType into its cooresponding symbol.
	 *
	 * @param pieceType The piece's type
	 * @return the character symbol of the type, or null if it doesn't match any known symbol
	 */
	public static Character resolveChessType(PieceType pieceType) {
		return ChessPiece.typeSymbolMap.get(pieceType);
	}	
	
	//
	// ======================== MEMBER ATTRIBUTES =======================
	//
	
	protected TeamColor color;
	protected PieceType type;
	protected ChessPieceMoveCalculator moveCalculator;

	//
	// ======================== CONSTRUCTORS =======================
	//

	/**
	 * I only really have this constructor for the test cases that are grading me.
	 * I will personally never call this.
	 */
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
		this.color = pieceColor;
		this.type = type;

		// I mean, honestly. This goes against everything OOP stands for. 
		switch(type) {
			case KING:
				this.moveCalculator = new KingMoveCalculator();
				break;
			case QUEEN:
				this.moveCalculator = new QueenMoveCalculator();
				break;
			case ROOK:
				this.moveCalculator = new RookMoveCalculator();
				break;
			case KNIGHT:
				this.moveCalculator = new KnightMoveCalculator();
				break;
			case BISHOP:
				this.moveCalculator = new BishopMoveCalculator();
				break;
			case PAWN:
				this.moveCalculator = PawnMoveCalculator.makeNewPawnMoveCalculator(pieceColor);
				break;
			default:
				this.moveCalculator = null;
		}
    }

	/**
	 * Constructor for the ChessPiece object.
	 *
	 * Really, I am treating this as an abstract class, although the test cases that give me my
	 * grade disagree.
	 *
	 * @param pieceColor The team color of the new piece
	 * @param type The type of the new piece
	 * @param moveCalculator The calculator that will be perfoming the move calculations for the piece
	 */ public ChessPiece(TeamColor pieceColor, PieceType type, ChessPieceMoveCalculator moveCalculator) {
		this.color = pieceColor;
		this.type = type;
		this.moveCalculator = moveCalculator;
	}

	//
	// ======================== MEMBER METHODS =======================
	//

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
		return this.color;
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
		return this.moveCalculator.calculateMoves(this.color, myPosition, board);
    }

	/** 
	 * Calculates all the valid attack moves that a piece can do.
	 * Most of the time will be the same as ChessPiece.pieceMoves
	 *
	 * @param board The Current chessboard
	 * @param pos The position of the chess piece
	 *
	 * @return Collection of valid chess moves
	 */
	public HashSet<ChessMove> getAttackMoves(ChessBoard board, ChessPosition pos) {
		return this.moveCalculator.calculateAttackMoves(board, pos, this.color);
	}

	/**
	 * Overriden equality opperator
	 *
	 * Determines equality based upon the pieces type and color.
	 *
	 * @param obj: The other chess piece
	 *
	 * @return true if equal, false if otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		// obj must exist and must be ChessPiece object
		if (obj == null || !(obj instanceof ChessPiece)) {
			return false;
		}

		ChessPiece other = (ChessPiece) obj;

		if (this.type != other.getPieceType() ||
			this.color != other.getTeamColor()) {
				return false;
		}

		return true;
	}

	/** 
	 * Overriden hashCode opperation.
	 *
	 * Returns a unique hash code determined by its type and color
	 *
	 * @return unique hash code
	 */
	@Override
	public int hashCode() {
		int hash = this.color.hashCode() * 31;
		hash += this.type.hashCode();

		return hash;
	}

	/**
	 * Creates a string representation of the ChessPiece.
	 *
	 * @return String version of board.
	 */
	@Override
	public String toString() {
		return String.format("%s %s", this.color.toString(), this.type.toString());
	}
}
