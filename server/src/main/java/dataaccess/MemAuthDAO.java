package dataaccess;
import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemAuthDAO {
    private final Map<String, AuthData> authTokens = new HashMap<>();
    public void createAuth(AuthData a) {
        authTokens.put(a.username(), a);
    }
    public void deleteAuth(String username) {
        authTokens.remove(username);
    }
    public AuthData getAuth(String username) {
        return authTokens.get(username);
    }
    public void updateAuth(AuthData a) {
        authTokens.put(a.username(), a);
    }
    public void clear() {
        authTokens.clear();
    }
}
