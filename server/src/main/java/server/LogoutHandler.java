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
        try {
            String authToken = context.header("authorization");
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            LogoutResult logoutResult = userService.logout(logoutRequest);
            context.status(200).json(logoutResult);
        } catch (UnauthorizedException exception) {
            context.status(401);
            context.json(Map.of("message", exception.getMessage()));
        }
    }
}
