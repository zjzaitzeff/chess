package websocket;

import com.google.gson.Gson;

public record Action(Type type, String visitorName, String color, boolean isPlayer) {
    public enum Type {
        ENTER,
        EXIT,
        RESIGN,
        MAKE_MOVE
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}

