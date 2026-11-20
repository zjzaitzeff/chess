package client.websocket;

import client.DataAccessException;
import com.google.gson.Gson;
import websocket.Action;
import websocket.Notification;

import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

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
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    ServerMessage.ServerMessageType type = serverMessage.getServerMessageType();
                    if (type.equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
                        LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                        notificationHandler.notify(loadGameMessage);
                    } else if (type.equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
                        NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
                        notificationHandler.notify(notificationMessage);
                    } else if (type.equals(ServerMessage.ServerMessageType.ERROR)) {
                        ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                        notificationHandler.notify(errorMessage);
                    }
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

    public void joinGame(String authToken, Integer gameID) {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException exception) {
            throw new DataAccessException(DataAccessException.Code.ServerError, exception.getMessage());
        }
    }

    public void leaveGame(String authToken, Integer gameID) {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException exception) {
            throw new DataAccessException(DataAccessException.Code.ServerError, exception.getMessage());
        }
    }
}
