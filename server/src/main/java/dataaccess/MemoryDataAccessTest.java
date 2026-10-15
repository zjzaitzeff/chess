package dataaccess;

import chess.ChessGame;
import model.UserData;
import model.AuthData;
import model.GameData;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MemoryDataAccessTest {
    UserData registerRandomUser(MemUserDAO d) {
        var user = new UserData("cow", "cow@byu.edu", "moo");
        d.createUser(user);
        return d.getUser(user.username());
    }

    @Test
    @DisplayName("Clear User")
    public void clearUser() {
        var d = new MemUserDAO();
        var user = registerRandomUser(d);
        assertNotNull(d.getUser(user.username()));
        d.clear();
        assertNull(d.getUser(user.username()));
    }

    @Test
    @DisplayName("Create user")
    public void createUser() {
        var d = new MemUserDAO();
        var user = registerRandomUser(d);
        assertNotNull(d.getUser(user.username()));
    }

    @Test
    @DisplayName("Get user")
    public void getUser() {
        var d = new MemUserDAO();
        var user = registerRandomUser(d);
        assertEquals(user, d.getUser(user.username()));
    }

    @Test
    @DisplayName("Create Auth")
    public void createAuth() {
        var authToken = new AuthData("01234", "cow");
        var d = new MemAuthDAO();
        d.createAuth(authToken);
        assertNotNull(d.getAuth(authToken.authToken()));
    }

    @Test
    @DisplayName("Get auth")
    public void getAuth() {
        var authToken = new AuthData("01234", "cow");
        var d = new MemAuthDAO();
        d.createAuth(authToken);
        assertEquals(authToken, d.getAuth(authToken.authToken()));
    }

    @Test
    @DisplayName("Delete auth")
    public void deleteAuth() {
        var authToken = new AuthData("01234", "cow");
        var d = new MemAuthDAO();
        d.createAuth(authToken);
        assertNotNull(d.getAuth(authToken.authToken()));
        d.deleteAuth(authToken.authToken());
        assertNull(d.getAuth(authToken.authToken()));
    }

    @Test
    @DisplayName("Update auth")
    public void updateAuth() {
        var authTokenOne = new AuthData("01234", "cow");
        var authTokenTwo = new AuthData("9876", "moo");
        var d = new MemAuthDAO();
        d.createAuth(authTokenOne);
        d.updateAuth("01234",authTokenTwo);
        assertNotEquals(authTokenOne, d.getAuth(authTokenTwo.authToken()));
    }

    @Test
    @DisplayName("Clear auth")
    public void clearAuth() {
        var authTokenOne = new AuthData("01234", "cow");
        var authTokenTwo = new AuthData("9876", "moo");
        var d = new MemAuthDAO();
        d.createAuth(authTokenOne);
        d.createAuth(authTokenTwo);
        d.clear();
        assertNull(d.getAuth(authTokenOne.authToken()));
        assertNull(d.getAuth(authTokenTwo.authToken()));
    }

    GameData generateAndAddGame(MemGameDAO d) {
        var game = new GameData(1234, "cow", "cyote", "name", new ChessGame());
        d.createGame(game);
        return game;
    }

    // createGame, getGame, deleteGame, updateGame, listGames, clear
    @Test
    @DisplayName("Create Game")
    public void createGame() {
        var d = new MemGameDAO();
        var game = generateAndAddGame(d);
        assertNotNull(d.getGame(game.gameID()));
    }

    @Test
    @DisplayName("Get Game")
    public void getGame() {
        var d = new MemGameDAO();
        var game = generateAndAddGame(d);
        assertEquals(game, d.getGame(game.gameID()));
    }

    @Test
    @DisplayName("delete Game")
    public void deleteGame() {
        var d = new MemGameDAO();
        var game = generateAndAddGame(d);
        assertNotNull(d.getGame(game.gameID()));
        d.deleteGame(game.gameID());
        assertNull(d.getGame(game.gameID()));
    }

    @Test
    @DisplayName("Update Game")
    public void updateGame() {
        var d = new MemGameDAO();
        var game = generateAndAddGame(d);
        var gameTwo = new GameData(5678, "white", "black", "game2", new ChessGame());
        d.createGame(gameTwo);
        d.updateGame(game.gameID(), gameTwo);
        assertEquals(d.getGame(game.gameID()), d.getGame(gameTwo.gameID()));
    }

    @Test
    @DisplayName("List Games")
    public void listGames() {
        var d = new MemGameDAO();
        var g1 = generateAndAddGame(d);
        var g2 = new GameData(5678, "white", "black", "game2", new ChessGame());
        var g3 = new GameData(9012, "whiter", "blacker", "game3", new ChessGame());
        d.createGame(g2);
        d.createGame(g3);
        Collection<GameData> compare = new HashSet<>(d.listGames());
        Collection<GameData> games = Set.of(g1, g2, g3);
        assertEquals(games, compare);
    }
}
