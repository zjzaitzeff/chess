package service;
import dataaccess.*;
public class ClearService {
    private final AuthDAO authTokens;
    private final UserDAO users;
    private final GameDAO games;

    public ClearService(UserDAO userDAO, AuthDAO authTokenDAO, GameDAO gameDAO) {
        this.authTokens = authTokenDAO;
        this.users = userDAO;
        this.games = gameDAO;
    }
    public ClearResult clear() throws DataAccessException {
        authTokens.clear();
        users.clear();
        games.clear();
        return new ClearResult();
    }
}
