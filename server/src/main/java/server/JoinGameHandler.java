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
        var serializer = new Gson();
        context.contentType("application/json");
        try {
            String authToken = context.header("authorization");
            var jsonInfo = context.body();
            JoinGameRequest joinGameRequest = serializer.fromJson(jsonInfo, JoinGameRequest.class);
            JoinGameResult joinGameResult = gameService.joinGame(authToken, joinGameRequest);
            var whatToExport = serializer.toJson(joinGameResult);
            context.status(200).json(whatToExport);
        } catch (BadRequestException exception) {
            var error = Map.of("message", exception.getMessage());
            context.status(400).result(serializer.toJson(error));
        } catch (UnauthorizedException exception) {
            context.status(401);
            var error = Map.of("message", exception.getMessage());
            context.result(serializer.toJson(error));
        } catch (AlreadyTakenException exception) {
            var error = Map.of("message", exception.getMessage());
            context.status(403).result(serializer.toJson(error));
        }
    }
}
