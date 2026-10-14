package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData a) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void updateAuth(String authToken, AuthData a) throws DataAccessException;
    void clear() throws DataAccessException;
}
