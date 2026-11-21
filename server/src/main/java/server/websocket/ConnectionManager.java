package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<Session, Session>> connections = new ConcurrentHashMap<>();

    public void add(Session session, Integer gameID) {
        connections.putIfAbsent(gameID, new ConcurrentHashMap<>());
        connections.get(gameID).put(session, session);
    }

    public void remove(Session session, Integer gameID) {
        var sessions = connections.get(gameID);
        sessions.remove(session);
    }

    public void broadcast(Session excludeSession, NotificationMessage notificationMessage, Integer gameID) throws IOException {
        String msg = new Gson().toJson(notificationMessage);
        var sessions = connections.get(gameID);
        for (Session c : sessions.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
    public void broadcastLoadGameAllPlayers(LoadGameMessage loadGameMessage, Integer gameID) throws IOException {
        String message = new Gson().toJson(loadGameMessage);
        var sessions = connections.get(gameID);
        for (Session c : sessions.values()) {
            if (c.isOpen()) {
                c.getRemote().sendString(message);
            }
        }
    }
}
