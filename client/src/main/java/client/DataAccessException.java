package client;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class DataAccessException extends RuntimeException {
    public enum Code {
        ServerError,
        ClientError,
    }

    final private Code code;

    public DataAccessException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public static DataAccessException fromJson(String json) {
        var map = new Gson().fromJson(json, HashMap.class);
//        var status = Code.valueOf(map.get("status").toString());
        String message = map.get("message").toString();
        return new DataAccessException(Code.ServerError, message);
    }

    public static Code fromHttpStatusCode(int httpStatusCode) {
        return switch (httpStatusCode) {
            case 500 -> Code.ServerError;
            case 400 -> Code.ClientError;
            default -> throw new IllegalArgumentException("Unknown HTTP status code: " + httpStatusCode);
        };
    }
}
