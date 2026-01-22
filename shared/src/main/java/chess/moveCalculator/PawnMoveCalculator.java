package chess.moveCalculator;

import java.util.HashSet;

import chess.*;
import chess.ChessGame.TeamColor;

public class PawnMoveCalculator extends ChessPieceMoveCalculator {

	//
	// ============================ STATIC ATTRIBUTES =============================
	//

	private static final ChessPosition[] pawnMoves = {
		new ChessPosition(1, 0),
	};

	private static final ChessPosition[] attackMoves = {
		new ChessPosition(1, -1),
		new ChessPosition(1, 1),
	};

	private static int moveStanima = 1;

	//
	// ============================ STATIC METHODS =============================
	//

	/**
	 * If the color is black, will invert the vertical component as pawns are unidirectional.
	 *
	 * @param moves: the collection of movement vectors
	 * @param color: the pawn's color
	 *
	 * @return the updated set of moves.
	 */
	private static ChessPosition[] applyDirectionScalarMoves(ChessPosition[] moves, TeamColor color) {
		// Copy the new moves
		ChessPosition[] newMoves = new ChessPosition[moves.length];
		System.out.println("Called applyDirectionScalarMoves");
		ChessPosition scalars;
		switch(color) {
			case TeamColor.WHITE:
				scalars = new ChessPosition(1, 1);
				break;
			case TeamColor.BLACK:
				scalars = new ChessPosition(-1, 1);
				break;
			default:
				scalars = new ChessPosition(0, 0);
		}

		for (int i = 0; i < moves.length; i++) {
			ChessPosition move = moves[i];

			int newRow = move.getRow() * scalars.getRow();
			int newCol = move.getColumn() * scalars.getColumn();

			newMoves[i] = new ChessPosition(newRow, newCol);
		}

		return newMoves;	
	}
	
	//
	// ============================ CONSTRUCTORS =============================
	//
	
	public PawnMoveCalculator(TeamColor pawnColor) {
		super(PawnMoveCalculator.applyDirectionScalarMoves(PawnMoveCalculator.pawnMoves, pawnColor),
				PawnMoveCalculator.moveStanima);
		System.out.println("Exited applyDirectionScalarMoves");
	}
	
	//
	// ============================ MEMBER METHODS =============================
	//
	
	private boolean canDoubleJump(ChessBoard board, TeamColor pawnColor, ChessPosition pos) {
		// Verify the positions. White has to be on row 2, black on the second to last
		if (pawnColor == TeamColor.WHITE && pos.getRow() != 2) {
			return false;
		} 
		if (pawnColor == TeamColor.BLACK && pos.getRow() != board.getBoardHeight() - 1) {
			return false;
		}

		int dirMod = (pawnColor == TeamColor.WHITE) ? 1 : -1;
		// We need to check the two spaces in front of the pawn for pieces
		for (int i = 1; i <= 2; i++) {
			ChessPosition squareAhead = new ChessPosition(pos.getRow() + i * dirMod, pos.getColumn());
			// If there is a piece, we cannot double jump.
			if (board.getPiece(squareAhead) != null) {
				return false;
			}
		}

		return true;
	}
	
	public HashSet<ChessMove> calculateMoves(ChessBoard board, ChessPiece pawn, ChessPosition curPosition) {
		// Step 1: Get the normal chess moves
		HashSet<ChessMove> moves = super.calculateMoves(board, pawn, curPosition, false);
		
		System.out.println("Finished step 1");

		// Step 2: Check for double jump moves
		if (this.canDoubleJump(board, pawn.getTeamColor(), curPosition)) {
			// set up the new move
			int dirMod = (pawn.getTeamColor() == TeamColor.WHITE) ? 1 : -1;
			ChessPosition targetSquare = new ChessPosition(curPosition.getRow() + 2 * dirMod, curPosition.getColumn());
			ChessMove doubleJump = new ChessMove(curPosition, targetSquare, null);

			// append the new move to the set
			moves.add(doubleJump);
		}
		System.out.println("Finished step 2");

		// Step 3: Look for special diagnol capture lines
		ChessPosition[] attackMoves = PawnMoveCalculator.applyDirectionScalarMoves(PawnMoveCalculator.attackMoves, pawn.getTeamColor());

		for (ChessPosition move : attackMoves) {
			System.out.println(move);
		}
		
		for (ChessPosition attack : attackMoves) {
			ChessPosition attackSquare = new ChessPosition(curPosition);
			attackSquare.add(attack);

			// Make sure the attack square is witin bounds
			boolean inBounds = this.checkBounds(attackSquare, new ChessPosition(1,1), new ChessPosition(board.getBoardHeight(), board.getBoardWidth()), board);
			if (!inBounds) {
				continue;
			}

			// If there is a piece on the the square, we can capture it if it is of the other color
			ChessPiece piece = board.getPiece(attackSquare);
			if (piece != null && piece.getTeamColor() != pawn.getTeamColor()) {
				moves.add(new ChessMove(curPosition, attackSquare, null));
			}
		}

		// Step 4: Calculate the promotion lines

		HashSet<ChessMove> promotionMoves = new HashSet<>();
		HashSet<ChessMove> oldPromotionMoves = new HashSet<>();
		for (ChessMove move : moves) {
			// If a pawn reached the end of the board
			if (pawn.getTeamColor() == TeamColor.WHITE && move.getEndPosition().getRow() == board.getBoardHeight() ||
				pawn.getTeamColor() == TeamColor.BLACK && move.getEndPosition().getRow() == 1) {
				// Add each of the valid chess types to the move set
				for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
					// Pawns cannot promote to pawns
					if (type == ChessPiece.PieceType.PAWN) { continue; }
					// Pawns cannot promote to kings
					if (type == ChessPiece.PieceType.KING) { continue; }

					ChessMove promotionMove = new ChessMove(move.getStartPosition(), move.getEndPosition(), type);
					promotionMoves.add(promotionMove);
				}

				// Remove the null promotion type
				oldPromotionMoves.add(move);
			}
		}

		moves.removeAll(oldPromotionMoves);
		moves.addAll(promotionMoves);
		
		return moves;
	}
}


