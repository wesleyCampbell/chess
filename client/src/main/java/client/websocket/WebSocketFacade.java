package client.websocket;

import com.google.gson.Gson;

import jakarta.websocket.*;

import websocket.messages.*;
import websocket.commands.*;

import chess.ChessMove;

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

	private void sendCommand(UserGameCommand cmd) throws IOException {
		this.session.getBasicRemote().sendText(cmd.toJson());
	}

	public void closeSession() throws IOException {
		this.session.close();
	}

	/************* WEBSOCKET ENDPOINTS **********************/

	public void connect(String authToken, int gameID) throws IOException {
		UserGameCommand cmd = new ConnectCommand(authToken, gameID);
		this.sendCommand(cmd);
	}

	public void makeMove(String authToken, int gameID, ChessMove move) throws IOException{
		UserGameCommand cmd = new MakeMoveCommand(authToken, gameID, move);
		this.sendCommand(cmd);
	}

	public void resign(String authToken, int gameID) throws IOException {
		UserGameCommand cmd = new ResignCommand(authToken, gameID);	
		this.sendCommand(cmd);
	}

	public void leave(String authToken, int gameID) throws IOException {
		UserGameCommand cmd = new LeaveCommand(authToken, gameID);
		this.sendCommand(cmd);
	}	
}
