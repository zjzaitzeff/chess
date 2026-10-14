package dataaccess;

import model.UserData;
import model.AuthData;
import model.GameData;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MemoryDataAccessTest {
    UserData registerRandomUser() {
        var user = new UserData("cow", "cow@byu.edu", "moo");
        var d = new MemUserDAO();
        d.createUser(user);
        return d.getUser(user.username());
    }
    @Test
    public void clear() {
        var d = new Use();
        var user = registerRandomUser();
        assertNotNull(d.getUser(user.username()));
        d.clear();
        assertNull(d.getUser(user.username()));


    }
    @Test
    public void registerAndGetUser() {
        var user = new UserData("cow", "cow@byu.edu", "moo");
        var d = new MemoryDataAcces();
        d.register(user, null);
        car actualUser = d.getUser(user.username());
        assertEquals(user.username(), actualUser.username());
    }
}
