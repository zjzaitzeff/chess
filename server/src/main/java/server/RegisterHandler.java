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
        var serializer = new Gson();
        context.contentType("application/json");
        try {
            var jsonInfo = context.body();
            RegisterRequest registerRequest = serializer.fromJson(jsonInfo, RegisterRequest.class);
            RegisterResult registerResult = userService.register(registerRequest);
            var whatToExport = serializer.toJson(registerResult);
            context.status(200).json(whatToExport);
        } catch (BadRequestException exception) {
            var error = Map.of("message", exception.getMessage());
            context.status(400).result(serializer.toJson(error));
        } catch (AlreadyTakenException exception) {
            var error = Map.of("message", exception.getMessage());
            context.status(403).result(serializer.toJson(error));
        }
    }
}
