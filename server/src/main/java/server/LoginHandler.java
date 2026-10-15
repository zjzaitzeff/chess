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
        try {
            LoginRequest loginRequest = new Gson().fromJson(context.body(), LoginRequest.class);
            LoginResult loginResult = userService.login(loginRequest);
            context.status(200).json(loginResult);
        } catch (BadRequestException exception) {
            context.status(400);
            context.json(Map.of("message", exception.getMessage()));
        } catch (UnauthorizedException exception) {
            context.status(401);
            context.json(Map.of("message", exception.getMessage()));
        }
    }
}
