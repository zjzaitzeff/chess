package dataaccess;

import model.UserData;
import model.AuthData;
import model.GameData;
import static org.junit.jupiter.api.Assertions.*;

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
        assertNotNull(d.getAuth(authToken.username()));
    }
    @Test
    @DisplayName("Get auth")
    public void getAuth() {
        var authToken = new AuthData("01234", "cow");
        var d = new MemAuthDAO();
        d.createAuth(authToken);
        assertEquals(authToken, d.getAuth(authToken.username()));
    }
    @Test
    @DisplayName("Delete auth")
    public void deleteAuth() {
        var authToken = new AuthData("01234", "cow");
        var d = new MemAuthDAO();
        d.createAuth(authToken);
        assertNotNull(d.getAuth(authToken.username()));
        d.deleteAuth(authToken.username());
        assertNull(d.getAuth(authToken.username()));
    }
    @Test
    @DisplayName("Update auth")
    public void updateAuth() {
        var authTokenOne = new AuthData("01234", "cow");
        var authTokenTwo = new AuthData("9876", "cow");
        var d = new MemAuthDAO();
        d.createAuth(authTokenOne);
        d.updateAuth(authTokenTwo);
        assertNotEquals(authTokenOne, d.getAuth(authTokenTwo.username()));
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
        assertNull(d.getAuth(authTokenOne.username()));
        assertNull(d.getAuth(authTokenTwo.username()));
    }

}
