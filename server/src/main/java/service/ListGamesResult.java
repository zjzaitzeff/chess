package service;
import model.GameData;
import java.util.HashMap;
import java.util.Map;
//games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""} ]
public record ListGamesResult(Map<String, GameData> games) {
}
