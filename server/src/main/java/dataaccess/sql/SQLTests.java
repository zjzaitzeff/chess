package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
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

    public MySqlDataAccessUser createSQL() throws DataAccessException {
        MySqlDataAccessUser sql = new MySqlDataAccessUser();
        sql.clear();
        return sql;
    }

    @Test
    @DisplayName("createUser Success")
    public void createUserSuccess() throws DataAccessException {
        UserData user = new UserData("cow", "moo", "moo@gmail.com");
        MySqlDataAccessUser sql = createSQL();
        sql.createUser(user);
        UserData tester = sql.getUser("cow");
        assertNotNull(tester);
        assertEquals("cow", tester.username());
        assertEquals("moo@gmail.com", tester.email());
        assertNotEquals("moo", tester.password());
        assertTrue(BCrypt.checkpw("moo", tester.password()));
    }

    @Test
    @DisplayName("createUser Fail")
    public void createUserFail() throws DataAccessException {
        UserData user = null;
        MySqlDataAccessUser sql = createSQL();
        sql.createUser(user);
        UserData tester = sql.getUser("cow");
        assertNull(tester);
    }

}