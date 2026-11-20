package client.websocket;


import websocket.Notification;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage serverMessage);
}