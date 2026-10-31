package server;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import service.*;
import io.javalin.http.Handler;
import com.google.gson.Gson;

import java.util.Map;

public class LogoutHandler implements Handler {
    UserService userService;

    public LogoutHandler(UserService userServiceClass) {
        this.userService = userServiceClass;
    }

    @Override
    public void handle(Context context) throws DataAccessException {
        var serializer = new Gson();
        context.contentType("application/json");
        try {
            String authToken = context.header("authorization");
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            LogoutResult logoutResult = userService.logout(logoutRequest);
            var whatToExport = serializer.toJson(logoutResult);
            context.status(200).json(whatToExport);
        } catch (UnauthorizedException exception) {
            context.status(401);
            var error = Map.of("message", exception.getMessage());
            context.result(serializer.toJson(error));
        } catch (DataAccessException exception) {
            var error = Map.of("message", "Error: " + exception.getMessage());
            context.status(500).result(serializer.toJson(error));
        }
    }
}
