package service;
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

}
