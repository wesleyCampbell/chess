package client.websocket;

import websocket.messages.ServerMessage;

public interface NotificationHandler {
	public void manageMsg(ServerMessage notification, String origMsg);
}	
