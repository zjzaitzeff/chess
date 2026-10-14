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
    @DisplayName("Clear")
    public void clear() {
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
}
