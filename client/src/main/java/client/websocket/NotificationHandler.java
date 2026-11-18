package client.websocket;


import websocket.Notification;

public interface NotificationHandler {
    void notify(Notification notification);
}