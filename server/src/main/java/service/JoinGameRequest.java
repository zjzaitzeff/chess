package service;
import model.AuthData;
//{ "playerColor":"WHITE/BLACK", "gameID": 1234 }
public record JoinGameRequest(AuthData authToken, String playerColor, int gameID) {
}
