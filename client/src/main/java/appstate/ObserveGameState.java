package appstate;

import java.util.List;
import java.util.function.Function;

import command.*;

import chess.*;
import chess.ChessGame.TeamColor;

import model.*;

import static ui.EscapeSequences.*;

import client.Client;
import client.Client.ActiveGame;

public class ObserveGameState extends BaseState {
	private static final String WELCOME_MSG = """
		Observing game %s...\s""" + HELP_PROMPT;
	private static final String PROMPT_HEADER = "OBSERVING GAME";
	private static final String PROMPT_COLOR = SET_TEXT_COLOR_GREEN;

	private static final List<Function<Client, Command>> COMMAND_LIST = List.of(
			HelpCommand::new,
			LeaveGameCommand::new,
			HighlightMovesCommand::new,
			RedrawChessBoardCommand::new
	);

	private ChessGame game;
	private TeamColor orientation;

	private static String generateWelcomeMsg(GameData gameData) {
		return String.format(WELCOME_MSG, gameData.gameName());
	}

	public ObserveGameState(Client app, GameData gameData, TeamColor orientation) {
		super(app, BaseState.initCommands(COMMAND_LIST, app), generateWelcomeMsg(gameData), PROMPT_HEADER, PROMPT_COLOR);
		this.game = gameData.game();
		this.orientation = orientation;
	}

	public ObserveGameState(Client app, ActiveGame game) {
		this(app, game.game(), game.team());
	}

	public ChessGame getGame() {
		return this.game;
	}

	public TeamColor getOrientation() {
		return this.orientation;
	}
}
