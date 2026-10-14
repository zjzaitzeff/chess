package service;
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import dataaccess.*;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public class GameService {
    private final GameDAO games;
    private final AuthDAO authTokens;

    public GameService(GameDAO gamesDAO, AuthDAO authTokenDAO) {
        this.games = gamesDAO;
        this.authTokens = authTokenDAO;
    }
   private int generateGameID() {
        return ThreadLocalRandom.current().nextInt(1000, 10000);
   }
// listGames, createGame, joinGame
    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        AuthData authToken = listGamesRequest.authToken();
        //401 unauthorized
        if (authTokens.getAuth(authToken.authToken()) == null) {
            throw new UnauthorizedException("Error 401: Unauthorized");
        }
        Collection<GameData> gameList = games.listGames();
    return new ListGamesResult(gameList);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        String gameName = createGameRequest.gameName();
        AuthData authToken = createGameRequest.authToken();
        // 400 bad request
        if (gameName == null || gameName.isEmpty()) {
            throw new BadRequestException("Error 400: Bad Request");
        }
        // 401 unauthorized
        if (authTokens.getAuth(authToken.authToken()) == null) {
            throw new UnauthorizedException("Error 401: Unauthorized");
        }
        //GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game)
        int gameID = generateGameID();
        GameData game = new GameData(gameID, null,null,gameName,new ChessGame());
        games.createGame(game);
        return new CreateGameResult(gameID);
    }
}
