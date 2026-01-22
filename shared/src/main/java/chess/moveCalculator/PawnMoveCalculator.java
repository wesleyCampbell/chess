package chess.moveCalculator;

import java.util.HashSet;

import chess.*;
import chess.ChessGame.TeamColor;

public class PawnMoveCalculator extends ChessPieceMoveCalculator {

	//
	// ============================ STATIC ATTRIBUTES =============================
	//

	private static ChessPosition[] pawnMoves = {
		new ChessPosition(1, 0),
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
		for (ChessPosition move : moves) {
			System.out.println(move.toString());
		}
		switch(color) {
			case TeamColor.WHITE:
				break;
			case TeamColor.BLACK:
				// Invert the vertical component for each move vector 
				for (int i = 0; i < moves.length; i++) {
					moves[i].setRow(-1 * moves[0].getRow());
				}
				break;
		}

		System.out.println("==================");
		for (ChessPosition move : moves) {
			System.out.println(move.toString());
		}
		return moves;	
	}
	
	//
	// ============================ CONSTRUCTORS =============================
	//
	
	public PawnMoveCalculator(TeamColor pawnColor) {
		super(PawnMoveCalculator.applyDirectionScalarMoves(PawnMoveCalculator.pawnMoves, pawnColor),
				PawnMoveCalculator.moveStanima);
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
		
		// Step 2: Check for double jump moves
		if (this.canDoubleJump(board, pawn.getTeamColor(), curPosition)) {
			// set up the new move
			int dirMod = (pawn.getTeamColor() == TeamColor.WHITE) ? 1 : -1;
			ChessPosition targetSquare = new ChessPosition(curPosition.getRow() + 2 * dirMod, curPosition.getColumn());
			ChessMove doubleJump = new ChessMove(curPosition, targetSquare, null);

			// append the new move to the set
			moves.add(doubleJump);
		}

		// Step 3: Look for special diagnol capture lines

		// Step 4: Calculate the promotion lines
		
		return moves;
	}
}


