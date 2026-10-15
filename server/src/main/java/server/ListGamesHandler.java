package server;
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
        try {
            String authToken = context.header("authorization");
            ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
            ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);
            context.status(200).json(listGamesResult);
        } catch (UnauthorizedException exception) {
            context.status(401);
            context.json(Map.of("message", exception.getMessage()));
        }
    }
}
