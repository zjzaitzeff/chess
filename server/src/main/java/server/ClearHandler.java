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
        try {
            var serializer = new Gson();
            context.contentType("application/json");
            ClearResult clearResult = clearService.clear();
            var whatToExport = serializer.toJson(clearResult);
            context.status(200).json(whatToExport);
        } catch (DataAccessException exception) {
            var error = Map.of("message", exception.getMessage());
            context.status(500);
            context.json(error);
        }
    }
}
