package websocket.messages;

import com.google.gson.Gson;

public class PlayerLeaveNotification extends ServerMessage {
	private final static String MSG_TEMPLATE = """
		Playser %s left the game.""";

	private String msg;

	public PlayerLeaveNotification(String username) {
		super(ServerMessageType.NOTIFICATION);
		this.msg = String.format(MSG_TEMPLATE, username);
	}

	public String getMsg() {
		return this.msg;
	}

	@Override 
	public String toJson() {
		return new Gson().toJson(this);
	}
}
