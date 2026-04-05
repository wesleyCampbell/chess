package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import util.Debugger;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.Map;

public class ConnectionsManager {
	public final ConcurrentHashMap<Integer, ConcurrentHashMap<Session, Session>> connections = new ConcurrentHashMap<>();

	public boolean connExists(int gameID) {
		return connections.containsKey(gameID);
	}

	public void createConn(int gameID) {
		this.connections.put(gameID, new ConcurrentHashMap<Session, Session>());
	}

	public void deleteConn(int gameID) {
		this.connections.remove(gameID);
	}

	public Map<Session, Session> getConn(int gameID) {
		return this.connections.get(gameID);
	}

	public void add(int gameID, Session session) {
		if (!this.connExists(gameID)) {
			this.createConn(gameID);
		}

		this.connections.get(gameID).put(session, session);
	}

	public void remove(int gameID, Session session) {
		if (this.connExists(gameID)) {
			this.connections.get(gameID).remove(session);

			if (this.connections.get(gameID).isEmpty()) {
				this.deleteConn(gameID);
			}
		}
	}

	public void broadcast(int gameID, Session excludeSession, ServerMessage message) throws IOException {
		if (this.connExists(gameID)) {
			for (Session s : this.connections.get(gameID).values()) {
				if (s.isOpen() && !s.equals(excludeSession)) {
					s.getRemote().sendString(message.toJson());
				}
			}
		}
	}
}
