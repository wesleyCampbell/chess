package appstate;

import java.util.List;
import java.util.function.Function;

import command.*;
import command.global.*;

import static ui.EscapeSequences.*;

import chess.*;
import chess.ChessGame.TeamColor;
import model.*;

import client.Client;

public class GameplayState extends BaseState {
	private static final String WELCOME_MSG = """
		Playing game %s...\s""" + HELP_PROMPT;
	private static final String PROMPT_HEADER = "GAMEPLAY";
	private static final String PROMPT_COLOR = SET_TEXT_COLOR_PURPLE;

	private static final List<Function<Client, Command>> COMMAND_LIST = List.of(
		HelpCommand::new,
		LeaveGameCommand::new,
		HighlightMovesCommand::new,
		RedrawChessBoardCommand::new,
		MakeMoveCommand::new,
		ResignCommand::new
			);

	private ChessGame game;
	private TeamColor orientation;

	private static String generateWelcomeMsg(GameData gameData) {
		return String.format(WELCOME_MSG, gameData.gameName());
	}

	public GameplayState(Client app, GameData gameData, TeamColor orientation) {
		super(app, BaseState.initCommands(COMMAND_LIST, app), generateWelcomeMsg(gameData), PROMPT_HEADER, PROMPT_COLOR);
		this.game = gameData.game();
		this.orientation = orientation;
	}
}
