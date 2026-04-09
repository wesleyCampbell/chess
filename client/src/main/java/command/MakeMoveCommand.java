package command;

import appstate.*;

import client.*;
import client.Client.ActiveGame;
import client.exception.*;

import chess.*;
import chess.InvalidMoveException;

import java.util.List;

import java.io.IOException;

public class MakeMoveCommand extends CommandBase {
	private static final String COMMAND_STR = "make-move";
	private static final String DESC_STR = """
		Make a chess move.""";
	private static final String[] PARAMS = {
		"move",
	};

	private static final String INVALID_MOVE_MSG = "\n\tError: Invalid move!";

	public MakeMoveCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	private ChessPosition parsePos(String posStr) {
		Character colChar = Character.toLowerCase(posStr.charAt(0));
		Character rowChar = posStr.charAt(1);

		Integer col = ChessBoard.parseRowHeader(colChar);
		// if the character symbol doesn't coorelate with a column, return null
		if (col == null) {
			return null;
		}
		
		int row;
		row = Character.getNumericValue(rowChar);
		
		// if the row char is out of bounds, return null
		if (row <= 0 || row > 8) {
			return null;
		}

		return new ChessPosition(row, col);
	}

	private ChessMove parseMove(String moveStr) {
		// For now, only a very simple move syntax is allowed
		if (moveStr.length() != 4 || moveStr.length() != 5) {

		}

		String startPosStr = moveStr.substring(0, 2);
		String endPosStr = moveStr.substring(2, 4);

		ChessPosition startPos = this.parsePos(startPosStr);
		ChessPosition endPos = this.parsePos(endPosStr);

		// if either square is invalid, return null
		if (startPos == null || endPos == null) {
			return null;
		}

		ChessPiece.PieceType pieceType = null;
		if (moveStr.length() == 5) {
			pieceType = ChessPiece.resolveChessType(moveStr.charAt(4));
			// If the symbol is invalid, we need to return null
			if (pieceType == null) {
				return null;
			}
		}

		return new ChessMove(startPos, endPos, pieceType);
	}

	public boolean executeCommand(List<String> parameters) {
		// Verify that the correct parameters have been passed in
		if (!this.verifyParameters(parameters, PARAMS.length)) {
			return false;
		}

		String moveStr = parameters.get(0);

		ChessMove move = this.parseMove(moveStr);
		if (move == null) {
			System.out.println(INVALID_MOVE_MSG);
			return false;
		}

		// Try to send the move over the web socket
		ActiveGame game = this.app.getActiveGame();
		try {
			int gameID = Integer.parseInt(game.game().gameID());
			this.app.getWebSocket().makeMove(this.app.getAuthToken(), gameID, move);
			// we update the local game just to allow it to render in real time
			game.game().game().makeMove(move);
		} catch (IOException ex) {
			System.out.println(SERVER_ERROR_MSG);
			return false;
		} catch (InvalidMoveException ex) {
			// nothing really needs to happen here as the server handles invalid move handling
			System.out.println(INVALID_MOVE_MSG);
			return false;
		}

		
		// Draw the new chess board	
		this.app.printBoard(game);

		return true;
	}
}
