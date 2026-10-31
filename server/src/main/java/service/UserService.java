package service;
import dataaccess.*;
import model.UserData;
import model.AuthData;
import org.mindrot.jbcrypt.BCrypt;

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
    public record RegisterResult(String username, String authToken) {}
    public record RegisterRequest(String username, String password, String email) {}

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
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        UserData user = new UserData(username, hashedPassword, email);
        users.createUser(user);
        String authTokenID = generateToken();
        AuthData authToken = new AuthData(authTokenID, username);
        authTokens.createAuth(authToken);
        return new RegisterResult(username, authTokenID);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new BadRequestException("Error 400: Bad Request");
        }
        UserData user = users.getUser(username);
        if (user == null || !BCrypt.checkpw(password, user.password())) {
            throw new UnauthorizedException("Error 401: Unauthorized");
        }
        String authTokenID = generateToken();
        AuthData authToken = new AuthData(authTokenID, username);
        authTokens.createAuth(authToken);
        return new LoginResult(username, authTokenID);
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        String authTokenID = logoutRequest.authToken();
        //401 unauthorized
        if (authTokenID == null || authTokens.getAuth(authTokenID) == null) {
            throw new UnauthorizedException("Error 401: Unauthorized");
        }
        authTokens.deleteAuth(authTokenID);
        return new LogoutResult();
    }
}
