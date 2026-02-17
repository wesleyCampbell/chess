package passoff.chess;

import chess.ChessBoard;
import chess.ChessBoardGenerator;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.InvalidMoveException;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChessBoardGeneratorTests {
	public ChessBoardGeneratorTests() {
	}

	@Test
	@DisplayName("Construct Rectangular ChessBoards")
	public void constructRectangleBoards() {
		int rowNum = 3;
		int colNum = 7;

		ChessBoard board = new ChessBoard(rowNum, colNum);

		String[] validBoardState = {
			"-------",
			"-------",
			"-------",
		};

		System.out.println(board.getBoardState());

		Assertions.assertArrayEquals(validBoardState, board.getBoardState());

		Assertions.assertEquals(rowNum, board.getBoardHeight());

		Assertions.assertEquals(colNum, board.getBoardWidth());

		ChessPosition invPos = new ChessPosition(4, 6);
		ChessPiece piece = ChessPiece.makeNewPiece(TeamColor.WHITE, PieceType.PAWN);
		Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> {board.addPiece(invPos, piece);});

		ChessPosition invPos2 = new ChessPosition(2, 8);
		Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> {board.addPiece(invPos2, piece);});
	}	
}
