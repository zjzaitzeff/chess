package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccessAuth;
import dataaccess.MySqlDataAccessGame;
import dataaccess.MySqlDataAccessUser;
import model.UserData;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

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

    public MySqlDataAccessGame createSQLUGame() throws DataAccessException {
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
        assertNotEquals("moo", tester.password());
        assertTrue(BCrypt.checkpw("moo", tester.password()));
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

    }
}