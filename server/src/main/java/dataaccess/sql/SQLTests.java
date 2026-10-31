package dataaccess.sql;

import chess.ChessGame;
import dataaccess.*;
import model.UserData;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SQLTests {

    public MySqlDataAccessUser createSQLUser() throws DataAccessException {
        MySqlDataAccessUser sql = new MySqlDataAccessUser();
        sql.clear();
        return sql;
    }

    public MySqlDataAccessAuth createSQLAuth() throws DataAccessException {
        MySqlDataAccessAuth sql = new MySqlDataAccessAuth();
        sql.clear();
        return sql;
    }

    public MySqlDataAccessGame createSQLGame() throws DataAccessException {
        MySqlDataAccessGame sql = new MySqlDataAccessGame();
        sql.clear();
        return sql;
    }

    @Test
    @DisplayName("createUser Success")
    public void createUserSuccess() throws DataAccessException {
        UserData user = new UserData("cow", "moo", "moo@gmail.com");
        MySqlDataAccessUser sql = createSQLUser();
        sql.createUser(user);
        UserData tester = sql.getUser("cow");
        assertNotNull(tester);
        assertEquals("cow", tester.username());
        assertEquals("moo@gmail.com", tester.email());
        assertEquals("moo", tester.password());
    }

    @Test
    @DisplayName("clearUser")
    public void clearUser() throws DataAccessException {
        UserData user = new UserData("cow", "moo", "moo@gmail.com");
        MySqlDataAccessUser sql = createSQLUser();
        sql.createUser(user);
        UserData tester = sql.getUser("cow");
        assertNotNull(tester);
        sql.clear();
        UserData tester2 = sql.getUser("Cow");
        assertNull(tester2);
    }

    @Test
    @DisplayName("Create Auth")
    public void createAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("1234", "cow");
        MySqlDataAccessAuth sql = createSQLAuth();
        sql.createAuth(auth);
        AuthData tester = sql.getAuth("1234");
        assertNotNull(tester);
        assertEquals("1234", tester.authToken());
        assertEquals("cow",tester.username());
    }

    @Test
    @DisplayName("Delete Auth")
    public void deleteAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("1234", "cow");
        MySqlDataAccessAuth sql = createSQLAuth();
        sql.createAuth(auth);
        AuthData tester = sql.getAuth("1234");
        assertNotNull(tester);
        assertEquals("1234", tester.authToken());
        assertEquals("cow",tester.username());
        sql.deleteAuth("1234");
        AuthData tester2 = sql.getAuth("1234");
        assertNull(tester2);
    }

    @Test
    @DisplayName("Clear Auth")
    public void clearAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("1234", "cow");
        MySqlDataAccessAuth sql = createSQLAuth();
        sql.createAuth(auth);
        AuthData tester = sql.getAuth("1234");
        assertNotNull(tester);
        assertEquals("1234", tester.authToken());
        assertEquals("cow",tester.username());
        sql.clear();
        AuthData tester2 = sql.getAuth("1234");
        assertNull(tester2);
    }

    @Test
    @DisplayName("Create Game")
    public void createGameSuccess() throws DataAccessException {
        var g2 = new GameData(5678, "white", "black", "game2", new ChessGame());
       //var g3 = new GameData(9012, "whiter", "blacker", "game3", new ChessGame());
        MySqlDataAccessGame sql = createSQLGame();
        int gameID = sql.createGame(g2);
        GameData tester = sql.getGame(gameID);
        assertNotNull(tester);
        assertEquals("white", tester.whiteUsername());
        assertEquals("black", tester.blackUsername());
        assertEquals("game2",tester.gameName());
        assertEquals(new ChessGame(), tester.chess());
    }

    @Test
    @DisplayName("Delete Game")
    public void deleteGameSuccess() throws DataAccessException {
        var g2 = new GameData(5678, "white", "black", "game2", new ChessGame());
        //var g3 = new GameData(9012, "whiter", "blacker", "game3", new ChessGame());
        MySqlDataAccessGame sql = createSQLGame();
        int gameID = sql.createGame(g2);
        GameData tester = sql.getGame(gameID);
        assertNotNull(tester);
        assertEquals("white", tester.whiteUsername());
        assertEquals("black", tester.blackUsername());
        assertEquals("game2",tester.gameName());
        assertEquals(new ChessGame(), tester.chess());
        sql.deleteGame(gameID);
        GameData tester2 = sql.getGame(gameID);
        assertNull(tester2);
    }

    @Test
    @DisplayName("Clear Game")
    public void clearGameSuccess() throws DataAccessException {
        var g2 = new GameData(5678, "white", "black", "game2", new ChessGame());
        //var g3 = new GameData(9012, "whiter", "blacker", "game3", new ChessGame());
        MySqlDataAccessGame sql = createSQLGame();
        int gameID = sql.createGame(g2);
        GameData tester = sql.getGame(gameID);
        assertNotNull(tester);
        assertEquals("white", tester.whiteUsername());
        assertEquals("black", tester.blackUsername());
        assertEquals("game2",tester.gameName());
        assertEquals(new ChessGame(), tester.chess());
        sql.clear();
        GameData tester2 = sql.getGame(gameID);
        assertNull(tester2);
    }

    @Test
    @DisplayName("List Games")
    public void listGames() throws DataAccessException {
        MySqlDataAccessGame sql = createSQLGame();
        var g2 = new GameData(2, "white", "black", "game2", new ChessGame());
        var g3 = new GameData(3, "whiter", "blacker", "game3", new ChessGame());
        int gameID1 = sql.createGame(g2);
        int gameID2 = sql.createGame(g3);
        var expectedGame1 = new GameData(gameID1, "white", "black", "game2", new ChessGame());
        var expectedGame2 = new GameData(gameID2, "whiter", "blacker", "game3", new ChessGame());
        Collection<GameData> compare = new HashSet<>(sql.listGames());
        Collection<GameData> games = Set.of(expectedGame1, expectedGame2);
        assertEquals(games, compare);
    }

    @Test
    @DisplayName("Update Game")
    public void upadteGameSuccess() throws DataAccessException {
        var g2 = new GameData(5678, "white", "black", "game2", new ChessGame());
        MySqlDataAccessGame sql = createSQLGame();
        int gameID = sql.createGame(g2);
        GameData tester = sql.getGame(gameID);
        assertNotNull(tester);
        assertEquals("white", tester.whiteUsername());
        assertEquals("black", tester.blackUsername());
        assertEquals("game2",tester.gameName());
        assertEquals(new ChessGame(), tester.chess());
        var g3 = new GameData(9012, "whiter", "blacker", "game3", new ChessGame());
        sql.updateGame(gameID, g3);
        GameData tester2 = sql.getGame(gameID);
        assertNotNull(tester2);
        assertEquals("whiter", tester2.whiteUsername());
        assertEquals("blacker", tester2.blackUsername());
        assertEquals("game3",tester2.gameName());
        assertEquals(new ChessGame(), tester2.chess());
    }

}