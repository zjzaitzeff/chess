package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.Action;
import websocket.Notification;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {

    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        System.out.println("Websocket connected");
        wsConnectContext.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        try {
            Action action = new Gson().fromJson(wsMessageContext.message(), Action.class);
            switch (action.type()) {
                case ENTER -> enter(action.visitorName(), wsMessageContext.session, action.color(), action.isPlayer());
                case EXIT -> exit(action.visitorName(), wsMessageContext.session);
                //add move, resign
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void enter(String visitorName, Session session, String color, boolean isPlayer) throws IOException {
        connections.add(session);
        if (isPlayer) {
            var message = String.format("%s joined the game", visitorName);
            var notification = new Notification(Notification.Type.ARRIVAL, message);
            connections.broadcast(session, notification);
        } else {
            var message = String.format("%s joined the game", visitorName);
            var notification = new Notification(Notification.Type.ARRIVAL, message);
            connections.broadcast(session, notification);
        }
    }

    private void exit(String visitorName, Session session) throws IOException {
        var message = String.format("%s left the game", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(session, notification);
        connections.remove(session);
    }
}
