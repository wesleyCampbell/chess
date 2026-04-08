package websocket.messages;

import chess.ChessMove;

import com.google.gson.Gson;

public class InvalidMoveError extends ServerMessage {
	private final static String MSG = "Error: Invalid move!";

	public InvalidMoveError() {
		super(MSG);
	}
}
