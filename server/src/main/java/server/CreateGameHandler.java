package server;
import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import service.*;
import io.javalin.http.Handler;

import java.util.Map;

public class CreateGameHandler implements Handler {
    GameService gameService;

    public CreateGameHandler(GameService gameServiceClass) {
        this.gameService = gameServiceClass;
    }

    @Override
    public void handle(Context context) throws DataAccessException {
        try {
            String authToken = context.header("authorization");
            String gameName = new Gson().fromJson(context.body(), String.class);
            CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);
            CreateGameResult createGameResult = gameService.createGame(createGameRequest);
            context.status(200).json(createGameResult);
        } catch (BadRequestException exception) {
            context.status(400);
            context.json(Map.of("message", exception.getMessage()));
        } catch (UnauthorizedException exception) {
            context.status(401);
            context.json(Map.of("message", exception.getMessage()));
        }
    }
}
