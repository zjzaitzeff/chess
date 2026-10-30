package dataaccess;
import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemAuthDAO implements AuthDAO {
    private final Map<String, AuthData> authTokens = new HashMap<>();

    @Override
    public void createAuth(AuthData a) {
        authTokens.put(a.authToken(), a);
    }

    @Override
    public void deleteAuth(String authToken) {
        authTokens.remove(authToken);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authTokens.get(authToken);
    }
//
//    @Override
//    public void updateAuth(String authToken, AuthData a) {
//        authTokens.put(authToken, a);
//    }

    @Override
    public void clear() {
        authTokens.clear();
    }
}
