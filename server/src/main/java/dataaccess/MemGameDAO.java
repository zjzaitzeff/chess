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
    public GameData getGame(GameData g) {
        return games.get(g.gameID());
    }
    public void deleteGame(GameData g) {
        games.remove(g.gameID());
    }
    public void updateGame(GameData g) {
        games.put(g.gameID(), g);
    }
    public Collection<GameData> listGames() {
        return games.values();
    }
    public void clear() {
        games.clear();
    }
}
