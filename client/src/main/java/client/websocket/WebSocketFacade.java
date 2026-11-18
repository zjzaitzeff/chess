package client.websocket;

import client.DataAccessException;
import com.google.gson.Gson;
import websocket.Action;
import websocket.Notification;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws DataAccessException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Notification notification = new Gson().fromJson(message, Notification.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new DataAccessException(DataAccessException.Code.ServerError, ex.getMessage());
        }
    }
    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(String username, String color, boolean isPlayer) {
        try {
            var action = new Action(Action.Type.ENTER, username, color, isPlayer);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException exception) {
            throw new DataAccessException(DataAccessException.Code.ServerError, exception.getMessage());
        }
    }

    public void leaveGame(String username) {
        try {
            var action = new Action(Action.Type.EXIT, username, null, true);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException exception) {
            throw new DataAccessException(DataAccessException.Code.ServerError, exception.getMessage());
        }
    }
}
