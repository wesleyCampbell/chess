package command;

import java.util.List;

import appstate.*;

import client.Client;
import client.Client.ActiveGame;

import chess.*;

import model.*;

public class HighlightMovesCommand extends CommandBase {
	private static final String COMMAND_STR = "highlight-moves";
	private static final String DESC_STR = """
		Show all valid moves of a piece on a given chess square.""";
	private static final String[] PARAMETERS = {
		"square"
	};

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
		try {
			row = Integer.valueOf(rowChar);
		} catch (NumberFormatException ex) {
			return null;
		}

		return new ChessPosition(col, row);
	}

	public boolean executeCommand(List<String> parameters) {
		if (!this.verifyParameters(parameters, PARAMETERS.length)) {
			return false;
		}

		String squareStr = parameters.get(0);
		ChessPosition square = this.parseSquareStr(squareStr);

		System.out.println("Imma print these valid moves out");

		ActiveGame game = this.app.getActiveGame();
		if (game == null) {
			System.out.println("Game null");
			return false;
		}

		this.app.printBoardValidMoves(game, square);

		return false;
	}
}
