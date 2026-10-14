package dataaccess;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemGameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    public void createGame(GameData g) {
        games.put(g.gameID(),g);
    }
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }
    public void deleteGame(int gameID) {
        games.remove(gameID);
    }
    public void updateGame(int gameID, GameData g) {
        games.put(gameID, g);
    }
    public Collection<GameData> listGames() {
        return games.values();
    }
    public void clear() {
        games.clear();
    }
}
