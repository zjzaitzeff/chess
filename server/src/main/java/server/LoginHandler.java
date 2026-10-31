package server;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.http.Context;
import service.*;
import io.javalin.http.Handler;
import java.util.Map;

public class LoginHandler implements Handler {
    UserService userService;

    public LoginHandler (UserService userServiceClass) {
        this.userService = userServiceClass;
    }

    @Override
    public void handle(Context context) throws DataAccessException {
        var serializer = new Gson();
        context.contentType("application/json");
        try {
            var jsonInfo = context.body();
            LoginRequest loginRequest = serializer.fromJson(jsonInfo, LoginRequest.class);
            LoginResult loginResult = userService.login(loginRequest);
            var whatToExport = serializer.toJson(loginResult);
            context.status(200).json(whatToExport);
        } catch (BadRequestException exception) {
            var error = Map.of("message", exception.getMessage());
            context.status(400).result(serializer.toJson(error));
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
