package service;
import model.GameData;
import java.util.Collection;
//games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""} ]
public record ListGamesResult(Collection<GameData> games) {
}
