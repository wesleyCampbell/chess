package websocket.commands;

import chess.*;

import com.google.gson.Gson;

public class MakeMoveCommand extends UserGameCommand {
	private final static CommandType CMD_TYPE = CommandType.MAKE_MOVE;

	private final static Gson GSON = new Gson();

	private ChessMove move;

	public MakeMoveCommand(String authToken, Integer gameID, ChessMove move) {
		super(CMD_TYPE, authToken, gameID);

		this.move = move;
	}

	public ChessMove getMove() {
		return this.move;
	}

	@Override
	public String toString() {
		return GSON.toJson(this);
	}
}
