package client.websocket;

import com.google.gson.Gson;
import exception.ResponseException;

import jakarta.websocket.*;

import websocket.commands.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
	private Session session;
	private NotificationHandler notificationhandler;

	public WebSocketFacade(String url, NotificationHandler notificationHandler) {
        try {
			// Process the URL and convert it to a web socket one
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

			// Set up the web socket connection
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

			// Notification handling
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Notification notification = new Gson().fromJson(message, Notification.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
	}

	@Override 
	public void onOpen(Session session, EndpointConfig endpointConfig) {
	}

	/************* WEBSOCKET ENDPOINTS **********************/

	public void connect(String authToken, int gameID) {
		try {
			UserGameCommand cmd = new ConnectCommand(authToken, gameID);
			this.session.getBasicRemote().sendText(cmd.toJson());
		} catch (IOException ex) {
			System.out.println("ERROR:");
			return;
		}
	}

	public void makeMove() {

	}

	public void resign() {

	}

	public void leave() {
	}	
	
}
