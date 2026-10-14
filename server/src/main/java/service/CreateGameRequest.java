package service;
import model.AuthData;
public record CreateGameRequest(AuthData authToken, String gameName) {
}
