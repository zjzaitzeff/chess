package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData a) throws DataAccessException;
    void deleteAuth(String username) throws DataAccessException;
    AuthData getAuth(AuthData a) throws DataAccessException;
    void updateAuth(AuthData a) throws DataAccessException;
    void clear() throws DataAccessException;
}
