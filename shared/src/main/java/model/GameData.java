package model;

import chess.ChessGame;

public record GameData(String gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

	public int getGameID() {
		return Integer.parseInt(gameID); 
	}

}
