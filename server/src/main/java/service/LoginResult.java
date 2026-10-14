package service;
import model.AuthData;
public record LoginResult(String username, AuthData authToken) {
}
