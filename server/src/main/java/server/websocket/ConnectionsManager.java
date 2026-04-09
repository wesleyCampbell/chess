package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import util.Debugger;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.Map;

public class ConnectionsManager {
	public class Connection {
		private ConcurrentHashMap<Session, Session> sessions;
		private boolean active;
		
		public Connection() {
			this.sessions = new ConcurrentHashMap<>();
			this.active = true;
		}

		public boolean isActive() {
			return this.active;
		}

		public void setInactive() {
			this.active = false;
		}

		public void addSession(Session session) {
			this.sessions.put(session, session);
		}

		public Session getSession(Session session) {
			return this.sessions.get(session);
		}

		public void removeSession(Session session) {
			this.sessions.remove(session);
		}

		public boolean contains(Session session) {
			return this.sessions.contains(session);
		}

		public ConcurrentHashMap<Session, Session> getSessions() {
			return this.sessions;
		}

		public boolean isEmpty() {
			return this.sessions.isEmpty();
		}	
	}

	public final ConcurrentHashMap<Integer, Connection> connections = new ConcurrentHashMap<>();

	public boolean connExists(int gameID) {
		return connections.containsKey(gameID);
	}

	public void createConn(int gameID) {
		this.connections.put(gameID, new Connection()); 
	}

	public void deleteConn(int gameID) {
		this.connections.remove(gameID);
	}

	public Map<Session, Session> getConn(int gameID) {
		return this.connections.get(gameID).getSessions();
	}

	public void closeAllSessions(Session session) {
		for (int gameID : this.connections.keySet()) {
			if (this.connections.get(gameID).contains(session)) {
				this.remove(gameID, session);
			}
		}
	}

	public void add(int gameID, Session session) {
		if (!this.connExists(gameID)) {
			this.createConn(gameID);
		}

		this.connections.get(gameID).addSession(session);
	}

	public void remove(int gameID, Session session) {
		if (this.connExists(gameID)) {
			this.connections.get(gameID).removeSession(session);

			if (this.connections.get(gameID).isEmpty()) {
				this.deleteConn(gameID);
			}
		}
	}

	public void broadcast(int gameID, Session excludeSession, ServerMessage message) throws IOException {
		if (this.connExists(gameID)) {
			for (Session s : this.connections.get(gameID).getSessions().values()) {
				if (s.isOpen() && !s.equals(excludeSession)) {
					s.getRemote().sendString(message.toJson());
				}
			}
		}
	}

	public void broadcastAll(int gameID, ServerMessage message) throws IOException {
		if (this.connExists(gameID)) {
			for (Session s : this.connections.get(gameID).getSessions().values()) {
				if (s.isOpen()) {
					s.getRemote().sendString(message.toJson());
				}
			}
		}
	}

	public boolean isGameActive(int gameID) {
		return this.connections.get(gameID).isActive();
	}

	public void setGameInactive(int gameID) {
		this.connections.get(gameID).setInactive();
	}
}
