package dataaccess;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    @Override
    public void createGame(GameData g) {
        games.put(g.gameID(),g);
    }
    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }
    @Override
    public void deleteGame(int gameID) {
        games.remove(gameID);
    }
    @Override
    public void updateGame(int gameID, GameData g) {
        games.put(gameID, g);
    }
    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }
    @Override
    public void clear() {
        games.clear();
    }
}
