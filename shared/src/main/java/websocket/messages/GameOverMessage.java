package websocket.messages;

public class GameOverMessage extends ServerMessage {
	private static final ServerMessageType MSG_TYPE = ServerMessageType.GAME_OVER;

	public GameOverMessage() {
		super(MSG_TYPE);
	}
}
