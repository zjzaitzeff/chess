package dataaccess;
import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemAuthDAO {
    private final Map<String, AuthData> authTokens = new HashMap<>();
    public void createAuth(AuthData a) {
        authTokens.put(a.authToken(), a);
    }
    public void deleteAuth(String authToken) {
        authTokens.remove(authToken);
    }
    public AuthData getAuth(String authToken) {
        return authTokens.get(authToken);
    }
    public void updateAuth(String authToken, AuthData a) {
        authTokens.put(authToken, a);
    }
    public void clear() {
        authTokens.clear();
    }
}
