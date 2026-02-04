package chess.moveCalculator;

import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessBoard;
import chess.ChessPiece.PieceType;
import chess.ChessPiece;
import chess.ChessGame.TeamColor;

/**
 * Class for the Pawn ChessPiece move calculator
 */
public abstract class PawnMoveCalculator extends ChessPieceMoveCalculator {
	//
	// ======================== STATIC ATTRIBUTES =======================
	//
	
	protected static final ChessPosition[] DIR_VECTORS = {
		new ChessPosition(1, 0),	
	};

	protected static final ChessPosition[] ATTACK_VECTORS = { 
		new ChessPosition(1, -1),
		new ChessPosition(1, 1)
	};

	protected static final ChessPosition DOUBLE_JUMP_VECTOR = new ChessPosition(2, 0);

	protected static final int STANIMA = 1;

	protected static final Set<PieceType> INVALID_PROMOTION_TYPES = Set.of(
		PieceType.KING,
		PieceType.PAWN
	);

	//
	// ======================== STATIC METHODS ========================
	//
	
	/**
	 * A helper function used for making a new pawn object based on an inputed color
	 *
	 * @param pawnColor The color of the pawn
	 *
	 * @return A new PawnMoveCalculator object
	 */
	public static PawnMoveCalculator makeNewPawnMoveCalculator(TeamColor pawnColor) {
		switch (pawnColor) {
			case TeamColor.WHITE:
				return new WhitePawnMoveCalculator();
			case TeamColor.BLACK:
				return new BlackPawnMoveCalculator();
			default:
				return null;
		}
	}
	
	/**
	 * Applies a direction modifier to some movement vectors
	 *
	 * @param dirModifier The direction modifier
	 * @param vectors The movement vectors
	 *
	 * @return The array of modified vectors
	 */
	protected static ChessPosition[] applyDirModifier(ChessPosition dirModifier, ChessPosition[] vectors) {
		ChessPosition[] outVectors = new ChessPosition[vectors.length];

		for (int i = 0; i < vectors.length; i++) {
			outVectors[i] = PawnMoveCalculator.applyDirModifier(dirModifier, vectors[i]);
		}
		
		return outVectors;
	}

	/**
	 * Applies a direction modifier to a vector
	 *
	 * @param dirModifier The direction modifier
	 * @param vector The vector
	 *
	 * @return The modified vector
	 */
	protected static ChessPosition applyDirModifier(ChessPosition dirModifier, ChessPosition vector) {
		ChessPosition outVector = new ChessPosition(vector);
		outVector.multiply(dirModifier);
		
		return outVector;
	}
	
	//
	// ======================== MEMBER ATTRIBUTES ==============================
	//
	
	ChessPosition[] attackVectors;
	ChessPosition doubleJumpVector;


	//
	// ======================== CONSTRUCTORS ==============================
	//
	
	protected PawnMoveCalculator(ChessPosition[] moveVectors, ChessPosition[] attackVectors, ChessPosition doubleJumpVector) {
		super(moveVectors, PawnMoveCalculator.STANIMA);
		this.attackVectors = attackVectors;
		this.doubleJumpVector = doubleJumpVector;

	}

	//
	// ======================== MEMBER METHODS ============================
	//
	
	/**
	 * Calculates all the possible moves for the pawn chess piece.
	 *
	 * This is more complex than the chess move calculations for the other
	 * pieces. The Pawn can also:
	 * - Conditionally double jump
	 * - Conditionally capture in en passant
	 * - capture in different lines than its movement lines
	 * - Can promote
	 * - only move in one direction (uni-directional)
	 *
	 * @param color the pawn's color
	 * @param curPos The current position of the pawn
	 * @param board The current board
	 *
	 * @return A HashMap of all possible moves
	 */
	@Override
	public HashSet<ChessMove> calculateMoves(TeamColor color, ChessPosition curPos, ChessBoard board) {
		// Step 1: Collect all the normal moves
		HashSet<ChessMove> validMoves = super.calculateMoves(color, curPos, board, false);
		
		// Step 2: Calculate double jump
		if (canDoubleJump(color, curPos, board)) {
			ChessPosition jumpSquare = new ChessPosition(curPos);
			jumpSquare.add(this.doubleJumpVector);

			validMoves.add(new ChessMove(curPos, jumpSquare, null));
		}

		// Step 3: Calculate captures 
		
		for (ChessPosition attackVector : this.attackVectors) {
			// The square we are attacking 
			ChessPosition attackSquare = new ChessPosition(curPos);
			attackSquare.add(attackVector);

			// Check bounds
			if (!this.checkBoundaries(board, attackSquare)) { continue; }

			ChessPiece attackPiece = board.getPiece(attackSquare);
			if (attackPiece != null && attackPiece.getTeamColor() != color) {
				validMoves.add(new ChessMove(curPos, attackSquare, null));
			}
		}

		// Step 4: Calculate promotion lines

		HashSet<ChessMove> promotionMoves = new HashSet<>();  // A place to store the promotion moves during iteration
		HashSet<ChessMove> trash = new HashSet<>();  // A place to put the useless placeholder moves
		for (ChessMove move : validMoves) {
			if (color == TeamColor.WHITE) {
				if (move.getEndPosition().getRow() == board.getBoardHeight()) {
					this.addPromotionTypes(move, promotionMoves);
					trash.add(move);
				}
			}
			else if (color == TeamColor.BLACK) {
				if (move.getEndPosition().getRow() == 1) {
					this.addPromotionTypes(move, promotionMoves);
					trash.add(move);
				}
			}
		}

		validMoves.addAll(promotionMoves);
		validMoves.removeAll(trash);
		
		// Step 5: Return 
		
		return validMoves;
	}

	/**
	 * Adds all the valid promotion moves to a set.
	 *
	 * @param move The chess move to iterate through
	 * @param promotionMoves The set to add all valid promotion moves
	 */
	private void addPromotionTypes(ChessMove move, Collection<ChessMove> promotionMoves) {
		for (PieceType type : PieceType.values()) {
			if (PawnMoveCalculator.INVALID_PROMOTION_TYPES.contains(type)) {
				continue;
			}

			promotionMoves.add(new ChessMove(move, type));
		}
	}

	/**
	 * Calculates to see if a pawn is capable of double jumping
	 *
	 * @param color The color of the pawn 
	 * @param curPos The current position of the Pawn
	 * @param board The current board 
	 *
	 * @return true, if the pawn can double jump, false otherwise
	 */
	private boolean canDoubleJump(TeamColor color, ChessPosition curPos, ChessBoard board) {
		// Check to see if the pawn is on the correct row
		if (color == TeamColor.WHITE && curPos.getRow() != 2) {
			return false;
		}
		if (color == TeamColor.BLACK && curPos.getRow() != board.getBoardHeight() - 1) {
			return false;
		}

		// Check to see if there are pieces blocking the jump
		ChessPosition jumpSquare = new ChessPosition(curPos);
		for (int i = 1; i <=2; i++) {
			// I don't like this, but it isn't likely to cause an issue yet.
			// TODO: Find a better way to do this.
			jumpSquare.add(this.directionVectors[0]);
			if (board.getPiece(jumpSquare) != null) { return false; }
		}

		// With the checks passed, we can jump
		return true;
	}

	/**
	 * Calculates all the valid attack moves that the pawn piece can make
	 *
	 * @param board The current chess board
	 * @param pos The position of the pawn
	 * @param color The pawn's team color
	 *
	 * @return A hash set containing all legal attack moves
	 */
	@Override
	public HashSet<ChessMove> calculateAttackMoves(ChessBoard board, ChessPosition pos, TeamColor color) {
		HashSet<ChessMove> outMoves = new HashSet<>();
		// Iterate through all attack moves and see if they are possible
		for (ChessPosition attackMove : this.attackVectors) {
			// calculate the square position
			ChessPosition attackSquare = new ChessPosition(pos);
			attackSquare.add(attackMove);
			
			// Verify that it is in bounds
			if (!this.checkBoundaries(board, attackSquare)) {
				continue;
			}

			// Check to see if there is a piece blocking
			ChessPiece attackPiece = board.getPiece(attackSquare);
			if (attackPiece != null && attackPiece.getTeamColor() == color) {
				continue;
			}

			// Move is within bounds and there is no blocking piece
			outMoves.add(new ChessMove(pos, attackSquare, null));
		}

		return outMoves;
	}
}

