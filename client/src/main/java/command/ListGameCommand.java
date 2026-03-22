package command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import client.Client;
import client.exception.AuthenticationException;
import client.exception.DataAccessException;
import model.GameData;

public class ListGameCommand extends CommandBase {

	public static record ListGameResult(Collection<GameData> games) {}

	private static final String COMMAND_STR = "list-games";
	private static final String DESC_STR = """
		List all games in the database.""";
	private static final String[] PARAMS = {};

	public ListGameCommand(Client app) {
		super(COMMAND_STR, DESC_STR, PARAMS, app);
	}

	public boolean executeCommand(List<String> parameters) {
		if (parameters.size() != PARAMS.length) {
			this.printUsage();
			return false;
		}
		
		System.out.println("Games: ");

		ArrayList<GameData> games;
		try {
			// get the games
			games = new ArrayList<>(this.app.getServer().listGames(this.app.getAuthToken()));
			// sort the games by gameID
			games.sort(Comparator.comparingInt(GameData::getGameID));
		} catch (AuthenticationException ex) {
			System.out.println(NOT_AUTH_MSG);
			return false;
		} catch (DataAccessException ex) {
			System.out.println(SERVER_ERROR_MSG);
			return false;
		}
		
		this.printGameTable(games);
		this.app.updateGamesCache(games);

		return true;
	}

	private void printGameTable(List<GameData> games) {
		String[] headers = {"Game ID", "Game Name", "White Player", "Black Player"};
		int[] colWidths = new int[headers.length];

		for (int i = 0; i < headers.length; i++) {
			colWidths[i] = headers[i].length();
		}	
		
		for (int i = 0; i < games.size(); i++) {
			GameData game = games.get(i);
			colWidths[0] = Math.max(colWidths[0], Integer.toString(i + 1).length());
			colWidths[1] = Math.max(colWidths[1], game.gameName().length());
			if (game.whiteUsername() != null) {
				colWidths[2] = Math.max(colWidths[2], game.whiteUsername().length());
			}
			if (game.blackUsername() != null) {
				colWidths[3] = Math.max(colWidths[3], game.blackUsername().length());
			}
		}

		StringBuilder b = new StringBuilder();
		int totalWidth = 0;
		b.append("\t|");
		for (int width : colWidths) {
			b.append(" %-");
			b.append(width);
			b.append("s |");

			totalWidth += width;
		}

		String rowFormat = b.toString();

		// Print the headers
		System.out.println(String.format(rowFormat, headers[0], headers[1], headers[2], headers[3]));
	
		// Print the separator
		b = new StringBuilder();
		b.append("\t|");
		for (int width : colWidths) {
			b.append("-");
			b.append("-".repeat(width));
			b.append("-|");
		}
		System.out.println(b.toString());
		
		// Print the actual games
		for (GameData game : games) {
			String id = game.gameID();
			String name = game.gameName();
			String white = game.whiteUsername() == null ? "" : game.whiteUsername();
			String black = game.blackUsername() == null ? "" : game.blackUsername();
	

			System.out.println(String.format(rowFormat, id, name, white, black));

		}
	}
}

