package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private final String errorMessage;

    public ErrorMessage(ServerMessage.ServerMessageType type, String message) {
        super(type);
        this.errorMessage = message;
    }

    public String getMessage() {
        return errorMessage;
    }
}
