package service;
import dataaccess.*;
import model.UserData;
import model.AuthData;
import org.junit.jupiter.api.Test;
import server.Server;
import server.ServerMain;

import java.util.UUID;

public class UserService {
    private final UserDAO users;
    private final AuthDAO authTokens;
    public UserService(UserDAO userList, AuthDAO authTokenList) {
        this.users = userList;
        this.authTokens = authTokenList;
    }
    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();
        // 400 bad request
        if (username == null || password == null || email == null || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            throw new BadRequestException("Error 400: Bad Request");
        }
        // 403 already taken
        if (users.getUser(username) != null) {
            throw new AlreadyTakenException("Error 403: Already Taken");
        }
        // 200 success
        UserData user = new UserData(username, password, email);
        users.createUser(user);
        String authTokenID = generateToken();
        AuthData authToken = new AuthData(authTokenID, username);
        authTokens.createAuth(authToken);
        return new RegisterResult(username, authTokenID);
    }

    //public LoginResult login(LoginRequest loginRequest) {}
    public void logout(LogoutRequest logoutRequest) {}
}
