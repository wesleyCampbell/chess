package command;

import java.util.List;

import appstate.*;

import client.Client;
import client.Client.ActiveGame;

import chess.*;

import model.*;
import util.Debugger;

public class HighlightMovesCommand extends CommandBase {
	private static final String COMMAND_STR = "highlight-moves";
	private static final String DESC_STR = """
		Show all valid moves of a piece on a given chess square.""";
	private static final String[] PARAMETERS = {
		"square"
	};

	private static final String INVALID_SQUARE_MSG = """
		\tThe square '%s' is invalid. Please try again.\n""";

	public HighlightMovesCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMETERS, app);
	}

	private ChessPosition parseSquareStr(String squareStr) {
		if (squareStr.length() != 2) {
			return null;
		}

		Character colChar = squareStr.charAt(0);
		Character rowChar = squareStr.charAt(1);


		// parse the column
		Integer col = ChessBoard.parseRowHeader(colChar);
		if (col == null) {
			return null;
		}

		// parse the row
		int row;
		row = Character.getNumericValue(rowChar);

		// If the row char is invalid
		if (row <= 0 || row > 8) {
			return null;	
		}


		return new ChessPosition(row, col);
	}

	public boolean executeCommand(List<String> parameters) {
		if (!this.verifyParameters(parameters, PARAMETERS.length)) {
			return false;
		}

		String squareStr = parameters.get(0);
		ChessPosition square = this.parseSquareStr(squareStr);

		if (square == null) {
			System.out.println(String.format(
				INVALID_SQUARE_MSG, squareStr
						));
			return false;
		}

		ActiveGame game = this.app.getActiveGame();
		if (game == null) {
			System.out.println("Game null");
			return false;
		}

		this.app.printBoardValidMoves(game, square);

		return false;
	}
}
