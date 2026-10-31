package service;
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import dataaccess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameService {
    private final GameDAO games;
    private final AuthDAO authTokens;
    private final Collection<Integer> tokens = new ArrayList<>(List.of(0));

    public GameService(GameDAO gamesDAO, AuthDAO authTokenDAO) {
        this.games = gamesDAO;
        this.authTokens = authTokenDAO;
    }

   private int generateGameID() {
        int gameID = 0;
        while (tokens.contains(gameID)) {
            gameID = ThreadLocalRandom.current().nextInt(1000, 10000);
        }
        tokens.add(gameID);
        return gameID;

   }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        String authToken = listGamesRequest.authToken();
        //401 unauthorized
        if (authTokens.getAuth(authToken) == null || authToken == null) {
            throw new UnauthorizedException("Error 401: Unauthorized");
        }
        Collection<GameData> gameList = games.listGames();
    return new ListGamesResult(gameList);
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest createGameRequest) throws DataAccessException {
        String gameName = createGameRequest.gameName();
        // 400 bad request
        if (gameName == null || gameName.isEmpty()) {
            throw new BadRequestException("Error 400: Bad Request");
        }
        // 401 unauthorized
        if (authTokens.getAuth(authToken) == null || authToken == null) {
            throw new UnauthorizedException("Error 401: Unauthorized");
        }
        //GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game)
        int gameID = generateGameID();
        GameData game = new GameData(gameID, null,null,gameName,new ChessGame());
        games.createGame(game);
        return new CreateGameResult(gameID);
    }

    public JoinGameResult joinGame(String authToken, JoinGameRequest joinGameRequest) throws DataAccessException {
        String pColor = joinGameRequest.playerColor();
        int gID = joinGameRequest.gameID();
        //400 bad request
        if (pColor == null || (!pColor.equals("WHITE") && !pColor.equals("BLACK")) || gID > 10000 || games.getGame(gID) == null) {
            throw new BadRequestException("Error 400: Bad Request");
        }
        //401 unauthorized
        if (authTokens.getAuth(authToken) == null || authToken == null) {
            throw new UnauthorizedException("Error 401: Unauthorized");
        }
        //403 already taken
        GameData game = games.getGame(gID);
        if (pColor.equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new AlreadyTakenException("Error 403: Already Taken");
            }
        } else {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException("Error 403: Already Taken");
            }
        }
        //200
        String username = authTokens.getAuth(authToken).username();
        if (pColor.equals("WHITE")) {
            GameData updatedGame = new GameData(gID, username, game.blackUsername(), game.gameName(), game.chess());
            games.updateGame(gID, updatedGame);
        } else {
            GameData updatedGame = new GameData(gID, game.whiteUsername(), username, game.gameName(), game.chess());
            games.updateGame(gID, updatedGame);
        }
        return new JoinGameResult();
    }
}
