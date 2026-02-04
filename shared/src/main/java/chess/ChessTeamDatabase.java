package chess;

import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

import java.util.HashSet;
import java.util.ArrayList;

public class ChessTeamDatabase {
	//
	// ============================== MEMBER ATTRIBUTES ============================== 
	//
	
	// private static ChessPosition[] findKingPos(TeamColor color, Chessboard board) {
	//
	// }
	
	//
	// ============================== MEMBER ATTRIBUTES ============================== 
	//
	
	private TeamColor teamColor;
	private HashSet<ChessPosition> kingPositions;
	private HashSet<ChessMove> attackMoveSet;
	private HashSet<ChessMove> moveSet;
	private ArrayList<ChessPiece> capturedPieces;

	//
	// ============================== CONSTRUCTORS ============================== 
	//
	
	public ChessTeamDatabase(TeamColor teamColor, ChessBoard currentBoard) {
		this.teamColor = teamColor;
		// this.kingPositions = ChessTeamDatabase.findKingPos(teamColor, currentBoard);
		this.kingPositions = this.findKingPos(currentBoard);
		this.attackMoveSet = this.generateAttackMoveSet(currentBoard);
		this.moveSet = this.generateMoveSet(currentBoard);
		this.capturedPieces = new ArrayList<>();
	}

	private HashSet<ChessPosition> findKingPos(ChessBoard board) {
		HashSet<ChessPosition> kingPos = new HashSet<>();

		// Iterate through each square on the board looking for the kings
		for (int row = 0; row < board.getBoardHeight(); row++) {
			for (int col = 0; col < board.getBoardWidth(); col++) {
				ChessPosition square = new ChessPosition(row + 1, col + 1);
				ChessPiece piece = board.getPiece(square);

				// Checks to see if the piece exists, is the same color, and is a king
				if (piece != null &&
					piece.getPieceType() == PieceType.KING &&
					piece.getTeamColor() == this.teamColor) {
						kingPos.add(square);
				}
			}
		}

		return kingPos;
	}

	public void updateKingPos(ChessBoard board) {
		this.kingPositions = this.findKingPos(board);
	}

	private HashSet<ChessMove> generateAttackMoveSet(ChessBoard board) {
		HashSet<ChessMove> attackMoves = new HashSet<>();

		// Iterates through all the squares on the board looking for pieces of the same color
		for (int row = 0; row < board.getBoardHeight(); row++) {
			for (int col = 0; col < board.getBoardWidth(); col++) {
				// Get the piece at the current square
				ChessPosition square = new ChessPosition(row+1, col+1);
				ChessPiece piece = board.getPiece(square);

				// If the sqare is empty, nothing to calculate
				if (piece == null) {
					continue;
				}

				// Filter out pieces of different color
				if (piece.getTeamColor() == this.teamColor) {
					HashSet<ChessMove> attacks = piece.getAttackMoves(board, square);
					// Adds all the ending positions of the attack moves
					attackMoves.addAll(attacks);
				} 
			}
		}
		
		return attackMoves;
	}

	private HashSet<ChessMove> generateMoveSet(ChessBoard board) {
		System.out.println(String.format("    DEBUG: Color: %s", this.teamColor));
		System.out.println(board);
		HashSet<ChessMove> moves = new HashSet<>();

		// Iterates through all the squares on the board looking for pieces of the same color
		for (int row = 0; row < board.getBoardHeight(); row++) {
			for (int col = 0; col < board.getBoardWidth(); col++) {
				// Get the piece at the current square
				ChessPosition square = new ChessPosition(row+1, col+1);
				ChessPiece piece = board.getPiece(square);

				// If the square is empty, there is nothing to do
				if (piece == null) {
					continue;
				}

				System.out.println(String.format("    DEBUG: position: %s", square));
				System.out.println(String.format("    DEBUG: piece: %s", piece));

				// Filter out pieces of different colors
				if (piece.getTeamColor() == this.teamColor) {
					moves.addAll(piece.pieceMoves(board, square));
				}
			}
		}

		System.out.println(String.format("    DEBUG moves: %s", moves));
		return moves;
	}	

	public void updateAttackMoveSet(ChessBoard board) {
		this.attackMoveSet = this.generateAttackMoveSet(board);
	}

	// not the most efficient. Good enough, for now
	public void update(ChessBoard board) {
		this.updateAttackMoveSet(board);
		this.updateKingPos(board);
	}


	public HashSet<ChessPosition> getKingPos() {
		return this.kingPositions;
	}	

	public TeamColor getTeamColor() {
		return this.teamColor;
	}

	public HashSet<ChessMove> getAttackMoveSet() {
		return this.attackMoveSet;
	}

	public HashSet<ChessMove> getMoveSet() {
		return this.moveSet;
	}

	public ArrayList<ChessPiece> getCapturedPieces() {
		return this.capturedPieces;
	}

	public void addCapturedPiece(ChessPiece capturedPiece) {
		this.capturedPieces.add(capturedPiece);
	}
}
