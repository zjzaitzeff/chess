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
        var serializer = new Gson();
        context.contentType("application/json");
        try {
            String authToken = context.header("authorization");
            var jsonInfo = context.body();
            CreateGameRequest createGameRequest = serializer.fromJson(jsonInfo, CreateGameRequest.class);
            CreateGameResult createGameResult = gameService.createGame(authToken, createGameRequest);
            var whatToExport = serializer.toJson(createGameResult);
            context.status(200).json(whatToExport);
        } catch (BadRequestException exception) {
            var error = Map.of("message", exception.getMessage());
            context.status(400).result(serializer.toJson(error));
        } catch (UnauthorizedException exception) {
            context.status(401);
            var error = Map.of("message", exception.getMessage());
            context.result(serializer.toJson(error));
        }
    }
}
