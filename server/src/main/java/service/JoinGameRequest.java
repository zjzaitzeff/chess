package service;
import model.AuthData;
//{ "playerColor":"WHITE/BLACK", "gameID": 1234 }
public record JoinGameRequest(String authToken, String playerColor, int gameID) {
}
