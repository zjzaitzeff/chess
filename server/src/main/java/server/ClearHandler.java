package server;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.*;
import io.javalin.http.Handler;

import java.util.Map;

public class ClearHandler implements Handler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearServiceClass) {
        this.clearService = clearServiceClass;
    }

    @Override
    public void handle(Context context) throws DataAccessException {
        var serializer = new Gson();
        context.contentType("application/json");
        try {
            ClearResult clearResult = clearService.clear();
            context.status(200).result(serializer.toJson(clearResult));
        } catch (DataAccessException exception) {
            var error = Map.of("message", "Error: " + exception.getMessage());
            context.status(500);
            context.result(serializer.toJson(error));
        }
    }
}
