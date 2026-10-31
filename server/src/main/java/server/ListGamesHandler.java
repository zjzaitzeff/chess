package server;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import service.*;
import io.javalin.http.Handler;

import java.util.Map;

public class ListGamesHandler implements Handler {
    GameService gameService;

    public ListGamesHandler(GameService gameServiceClass) {
        this.gameService = gameServiceClass;
    }

    @Override
    public void handle(Context context) throws DataAccessException {
        var serializer = new Gson();
        context.contentType("application/json");
        try {
            String authToken = context.header("authorization");
            ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
            ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);
            var whatToExport = serializer.toJson(listGamesResult);
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
