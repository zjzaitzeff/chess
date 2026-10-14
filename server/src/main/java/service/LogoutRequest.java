package service;
import model.AuthData;
public record LogoutRequest(AuthData authToken) {
}
