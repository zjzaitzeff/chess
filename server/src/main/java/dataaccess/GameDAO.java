package dataaccess;

import model.GameData;

public interface GameDAO {
    void createGame(GameData g) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void deleteGame(int gameID) throws DataAccessException;
    void updateGame(int gameID) throws DataAccessException;
    void clear() throws DataAccessException;
}
