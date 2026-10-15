package server;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import service.*;
import io.javalin.http.Handler;
import com.google.gson.Gson;

import java.util.Map;

public class JoinGameHandler implements Handler {
    GameService gameService;

    public JoinGameHandler(GameService gameServiceClass) {
        this.gameService = gameServiceClass;
    }

    @Override
    public void handle(Context context) throws DataAccessException {
        try {
            String authToken = context.header("authorization");
            JoinGameRequest joinGameRequest = new Gson().fromJson(context.body(), JoinGameRequest.class);
            JoinGameResult joinGameResult = gameService.joinGame(authToken, joinGameRequest);
            context.status(200).json(joinGameResult);
        } catch (BadRequestException exception) {
            context.status(400);
            context.json(Map.of("message", exception.getMessage()));
        } catch (UnauthorizedException exception) {
            context.status(401);
            context.json(Map.of("message", exception.getMessage()));
        } catch (AlreadyTakenException exception) {
            context.status(403);
            context.json(Map.of("message", exception.getMessage()));
        }
    }
}
