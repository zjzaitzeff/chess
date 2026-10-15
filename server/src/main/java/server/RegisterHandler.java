package server;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.http.Context;
import service.*;
import io.javalin.http.Handler;

import java.util.Map;

public class RegisterHandler implements Handler {
    UserService userService;

    public RegisterHandler(UserService userServiceClass) {
        this.userService = userServiceClass;
    }

    @Override
    public void handle(Context context) throws DataAccessException {
        try {
            RegisterRequest registerRequest = new Gson().fromJson(context.body(), RegisterRequest.class);
            RegisterResult registerResult = userService.register(registerRequest);
            context.status(200).json(registerResult);
        } catch (BadRequestException exception) {
            context.status(400);
            context.json(Map.of("message", exception.getMessage()));
        } catch (AlreadyTakenException exception) {
            context.status(403);
            context.json(Map.of("message", exception.getMessage()));
        }
    }
}
