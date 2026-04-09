package websocket.messages.error;

import chess.ChessMove;

import com.google.gson.Gson;

public class InvalidMoveError extends Error {
	private final static String MSG = "error: Invalid move!";

	public InvalidMoveError() {
		super(MSG);
	}
}
