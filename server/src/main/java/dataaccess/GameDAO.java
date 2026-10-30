package dataaccess;
import model.GameData;
import java.util.Collection;

public interface GameDAO {
    void createGame(GameData g) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void deleteGame(int gameID) throws DataAccessException;

//    void updateGame(int gameID, GameData g) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void clear() throws DataAccessException;
}
