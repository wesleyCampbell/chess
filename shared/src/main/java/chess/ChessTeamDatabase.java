package chess;

import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;
import static util.Debugger.debug;
import util.Pair;

import java.util.HashSet;

import java.util.ArrayList;

public class ChessTeamDatabase {
	//
	// ============================== MEMBER ATTRIBUTES ============================== 
	//
	
	private TeamColor teamColor;
	private HashSet<ChessPosition> kingPositions;
	private HashSet<ChessMove> attackMoveSet;
	private HashSet<ChessMove> moveSet;
	private ArrayList<ChessPiece> capturedPieces;
	private ArrayList<ChessPiece> movedPieces;
	private Pair<ChessPiece, ChessMove> lastMovedPiece;

	//
	// ============================== CONSTRUCTORS ============================== 
	//
	
	/**
	 * Constructor.
	 *
	 * @param teamColor The color of the team to be databased
	 * @param currentBoard A reference to the current board state
	 */
	public ChessTeamDatabase(TeamColor teamColor, ChessBoard currentBoard) {
		this.teamColor = teamColor;

		this.kingPositions = this.findKingPos(currentBoard);

		this.attackMoveSet = this.generateAttackMoveSet(currentBoard);
		this.moveSet = this.generateMoveSet(currentBoard);

		this.capturedPieces = new ArrayList<>();
		this.movedPieces = new ArrayList<>();

		this.lastMovedPiece = null;
	}

	//
	// ============================== MEMBER METHODS ============================== 
	//
	
	/**
	 * Returns a HashSet of all the king positions on a chessboard
	 *
	 * @param board A current chess board to search
	 *
	 * @return HashSet containing all positions of a color's kings.
	 */
	private HashSet<ChessPosition> findKingPos(ChessBoard board) {
		HashSet<ChessPosition> kingPos = new HashSet<>();

		// Iterate through each square on the board looking for the kings
		for (ChessBoard.IndexedPiece pieceInx : board) {
			ChessPiece piece = pieceInx.piece();

			// Checks to see if the piece exists, is the same color, and is a king
			if (piece != null &&
				piece.getPieceType() == PieceType.KING &&
				piece.getTeamColor() == this.teamColor) {
					kingPos.add(pieceInx.position());
			}
		}

		return kingPos;
	}

	/**
	 * Update the database records of king positions
	 *
	 * @param board The current game board
	 */
	public void updateKingPos(ChessBoard board) {
		this.kingPositions = this.findKingPos(board);
	}

	/**
	 * Given a Chessboard, generates a HashSet of all attack moves for the team
	 *
	 * @param board The current game board
	 *
	 * @return HashSet containing all moves attacking other pieces.
	 */
	private HashSet<ChessMove> generateAttackMoveSet(ChessBoard board) {
		HashSet<ChessMove> attackMoves = new HashSet<>();

		// Iterates through all the squares on the board looking for pieces of the same color
		for (ChessBoard.IndexedPiece pieceInx : board) {
			ChessPiece piece = pieceInx.piece();

			// If the sqare is empty, nothing to calculate
			if (piece == null) {
				continue;
			}

			// Filter out pieces of different color
			if (piece.getTeamColor() == this.teamColor) {
				HashSet<ChessMove> attacks = piece.getAttackMoves(board, pieceInx.position());
				// Adds all the ending positions of the attack moves
				attackMoves.addAll(attacks);
			} 
		}

		return attackMoves;
	}

	/**
	 * Updates the team's attackMoveSet
	 *
	 * @param board The current game board
	 */

	public void updateAttackMoveSet(ChessBoard board) {
		this.attackMoveSet = this.generateAttackMoveSet(board);
	}

	/**
	 * Given a ChessBoard, generates a HashSet of all moves a color can make
	 *
	 * @param board The current game board
	 *
	 * @return HashSet containing all team piece moves.
	 */
	private HashSet<ChessMove> generateMoveSet(ChessBoard board) {
		HashSet<ChessMove> moves = new HashSet<>();

		// Iterates through all the squares on the board looking for pieces of the same color
		for (ChessBoard.IndexedPiece pieceInx : board) {
			ChessPiece piece = pieceInx.piece();	

			// If the square is empty, there is nothing to do
			if (piece == null) {
				continue;
			}

			// Filter out pieces of different colors
			if (piece.getTeamColor() == this.teamColor) {
				moves.addAll(piece.pieceMoves(board, pieceInx.position()));
			}
		}

		return moves;
	}	


	/**
	 * Updates the teams moveSet
	 *
	 * @param board The current game board
	 */
	public void updateMoveSet(ChessBoard board) {
		this.moveSet = this.generateMoveSet(board);
	}

	// not the most efficient. Good enough, for now
	/**
	 * Updates all team database sets
	 *
	 * @param board The current game board
	 */
	public void update(ChessBoard board) {
		this.updateAttackMoveSet(board);
		this.updateMoveSet(board);
		this.updateKingPos(board);
	}


	/**
	 * Getter for the teams king position data set
	 *
	 * @return HashSet containing all positions of team's kings
	 */
	public HashSet<ChessPosition> getKingPos() {
		return this.kingPositions;
	}	

	/**
	 * Getter for the team's color
	 *
	 * @return the team color
	 */
	public TeamColor getTeamColor() {
		return this.teamColor;
	}

	/**
	 * Getter for the team's attack move data set
	 *
	 * @return HashSet containing all team piece attacks
	 */
	public HashSet<ChessMove> getAttackMoveSet() {
		return this.attackMoveSet;
	}

	/**
	 * Getter for team's move data set
	 *
	 * @return HashSet containing all team piece moves
	 */
	public HashSet<ChessMove> getMoveSet() {
		return this.moveSet;
	}

	/**
	 * Getter for team's captured pieces data set
	 *
	 * @return ArrayList containing all captured pieces
	 */
	public ArrayList<ChessPiece> getCapturedPieces() {
		return this.capturedPieces;
	}

	/**
	 * Adds a captured piece to the list
	 *
	 * @param capturedPiece The piece that was captured
	 */
	public void addCapturedPiece(ChessPiece capturedPiece) {
		this.capturedPieces.add(capturedPiece);
	}

	/**
	 * Getter for the team's moved pieces
	 *
	 * @return HashSet containing all of team's pieces that have moved.
	 */
	public ArrayList<ChessPiece> getMovedPieces() {
		return this.movedPieces;
	}

	/**
	 * Adds a piece to the database's movedPieces data set
	 *
	 * @param piece The chess piece that moved
	 * @param move The move that the piece did
	 */
	public void addMovedPiece(ChessPiece piece, ChessMove move) {
		this.movedPieces.add(piece);
		this.lastMovedPiece = new Pair<ChessPiece, ChessMove>(piece, move);
	}

	/** 
	 * Checks to see if the moved piece database contains a piece by reference, not 
	 * the equals() method.
	 *
	 * @param piece The piece to check for
	 *
	 * @return true if the list containst the same reference, false otherwise
	 */
	public boolean pieceHasMoved(ChessPiece piece) {
		for (ChessPiece dbPiece : this.movedPieces) {
			if (dbPiece == piece) {
				return true;
			}
		}

		return false;
	}

	/** 
	 * Returns a refference to the piece that was last moved by the team
	 *
	 * @return ChessPiece
	 */
	public Pair<ChessPiece, ChessMove> getLastMovedPiece() {
		return this.lastMovedPiece;
	}
}
