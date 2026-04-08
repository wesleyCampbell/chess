package client.websocket;

import com.google.gson.Gson;
import exception.ResponseException;

import jakarta.websocket.*;

import websocket.messages.*;
import websocket.commands.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
	private Session session;
	private NotificationHandler notificationHandler;

	public WebSocketFacade(String serverDomain, int serverPort, NotificationHandler notificationHandler) {
        try {
			// Process the URL and convert it to a web socket one
			String url = String.format("ws://%s:%d/ws", serverDomain, serverPort);
            URI socketURI = new URI(url);
            this.notificationHandler = notificationHandler;

			// Set up the web socket connection
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

			// Server Message handling
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage msg = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.manageMsg(msg, message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
			System.out.print("ERROR in WebSocketFacade initialization: ");
        }
	}

	@Override 
	public void onOpen(Session session, EndpointConfig endpointConfig) {
	}

	private void sendCommand(UserGameCommand cmd) {
		this.session.getBasicRemote().sendText(cmd.toJson());
	}

	/************* WEBSOCKET ENDPOINTS **********************/

	public void connect(String authToken, int gameID) throws IOException {
		UserGameCommand cmd = new ConnectCommand(authToken, gameID);
		this.sendCommand(cmd);
	}

	public void makeMove() {

	}

	public void resign(String authToken, int gameID) throws IOException {
		UserGameCommand cmd = new ResignCommand(authToken, gameID);	
		this.sendCommand(cmd);
	}

	public void leave() {
	}	
	
}
