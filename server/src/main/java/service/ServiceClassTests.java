package service;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
public class ServiceClassTests {

    @Test
    @DisplayName("Register Bad Request")
    public void registerBadRequest() {
        RegisterRequest practiceRequest = new RegisterRequest("",null, null);
        UserDAO userDAO = new MemUserDAO();
        AuthDAO authDAO = new MemAuthDAO();
        UserService userService = new UserService(userDAO,authDAO);
        Assertions.assertThrows(BadRequestException.class, () -> {
            userService.register(practiceRequest);
        });
    }

    @Test
    @DisplayName("Register Already Taken")
    public void registerAlreadyTaken() throws DataAccessException {
        RegisterRequest practiceRequest = new RegisterRequest("Cow","Moo", "Cow@byu.edu");
        RegisterRequest practiceRequestGood = new RegisterRequest("Cow", "Quack", "Liar@byu.edu");
        UserDAO userDAO = new MemUserDAO();
        AuthDAO authDAO = new MemAuthDAO();
        UserService userService = new UserService(userDAO,authDAO);
        RegisterResult registerResult = userService.register(practiceRequestGood);
        Assertions.assertThrows(AlreadyTakenException.class, () -> {
            userService.register(practiceRequest);
        });
    }

    @Test
    @DisplayName("Register Success")
    public void registerSuccess() throws DataAccessException {
        RegisterRequest practiceRequest = new RegisterRequest("Cow","Moo", "Cow@byu.edu");
        UserDAO userDAO = new MemUserDAO();
        AuthDAO authDAO = new MemAuthDAO();
        UserService userService = new UserService(userDAO,authDAO);
        RegisterResult registerResult = userService.register(practiceRequest);
        // user added to users, authToken added to users, registerResult created
        assertNotNull(userDAO.getUser(registerResult.username()));
        assertNotNull(authDAO.getAuth(registerResult.authToken()));
        assertEquals("Cow", registerResult.username());
        assertEquals(authDAO.getAuth(registerResult.authToken()).authToken(), registerResult.authToken());
    }

    @Test
    @DisplayName("Login Bad Request")
    public void loginBadRequest() {
        LoginRequest practiceRequest = new LoginRequest(null, "");
        UserDAO userDAO = new MemUserDAO();
        AuthDAO authDAO = new MemAuthDAO();
        UserService userService = new UserService(userDAO,authDAO);
        Assertions.assertThrows(BadRequestException.class, () -> {
            userService.login(practiceRequest);
        });
    }

    @Test
    @DisplayName("Login Unauthorized")
    public void loginUnauthorized() throws DataAccessException {
        LoginRequest practiceRequest = new LoginRequest("Cow", "Quack");
        RegisterRequest practiceRequestRegister = new RegisterRequest("Cow","Moo", "Cow@byu.edu");
        UserDAO userDAO = new MemUserDAO();
        AuthDAO authDAO = new MemAuthDAO();
        UserService userService = new UserService(userDAO,authDAO);
        RegisterResult registerResult = userService.register(practiceRequestRegister);
        Assertions.assertThrows(UnauthorizedException.class, () -> {
           userService.login(practiceRequest);
        });
    }

    @Test
    @DisplayName("Login Success")
    public void loginSuccess() throws DataAccessException {
        LoginRequest practiceRequest = new LoginRequest("Cow", "Moo");
        RegisterRequest practiceRequestRegister = new RegisterRequest("Cow","Moo", "Cow@byu.edu");
        UserDAO userDAO = new MemUserDAO();
        AuthDAO authDAO = new MemAuthDAO();
        UserService userService = new UserService(userDAO,authDAO);
        RegisterResult registerResult = userService.register(practiceRequestRegister);
        LoginResult loginResult = userService.login(practiceRequest);
        // creates authToken, returns loginResult
        assertNotNull(authDAO.getAuth(loginResult.authToken()));
        assertNotNull(loginResult.authToken());
        assertEquals("Cow", loginResult.username());
    }

    @Test
    @DisplayName("Logout Unauthorized")
    public void logoutUnauthorized() throws DataAccessException {
        LogoutRequest logoutRequest = new LogoutRequest("Evil Cow");
        UserDAO userDAO = new MemUserDAO();
        AuthDAO authDAO = new MemAuthDAO();
        UserService userService = new UserService(userDAO,authDAO);
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> userService.logout(logoutRequest));
        assertEquals("Error 401: Unauthorized", exception.getMessage());
        LogoutRequest logoutRequestTwo = new LogoutRequest(null);
        assertThrows(UnauthorizedException.class, () -> userService.logout(logoutRequestTwo));
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutSuccess() throws DataAccessException {
        LoginRequest practiceRequestLogin = new LoginRequest("Cow", "Moo");
        RegisterRequest practiceRequestRegister = new RegisterRequest("Cow","Moo", "Cow@byu.edu");
        UserDAO userDAO = new MemUserDAO();
        AuthDAO authDAO = new MemAuthDAO();
        UserService userService = new UserService(userDAO,authDAO);
        RegisterResult registerResult = userService.register(practiceRequestRegister);
        LoginResult loginResult = userService.login(practiceRequestLogin);
        // delete token and return LogoutResult
        LogoutResult logoutResult = userService.logout(new LogoutRequest(loginResult.authToken()));
        assertNull(authDAO.getAuth(loginResult.authToken()));
    }

    @Test
    @DisplayName("List Games Unauthorized")
    public void listGamesUnauthorized() throws DataAccessException {
        GameDAO gameDAO = new MemGameDAO();
        AuthDAO authDAO = new MemAuthDAO();
        GameService gameService = new GameService(gameDAO, authDAO);
        ListGamesRequest listGamesRequest = new ListGamesRequest("Evil cow");
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> gameService.listGames(listGamesRequest));
        assertEquals("Error 401: Unauthorized", exception.getMessage());
        ListGamesRequest listGamesRequestTwo = new ListGamesRequest(null);
        assertThrows(UnauthorizedException.class, () -> gameService.listGames(listGamesRequestTwo));
    }

    @Test
    @DisplayName("List Games Success")
    public void listGamesSuccess() throws DataAccessException {
        GameDAO gameDAO = new MemGameDAO();
        AuthDAO authDAO = new MemAuthDAO();
        authDAO.createAuth(new AuthData("0987", "Cow"));
        gameDAO.createGame(new GameData(0, "Cow", "Duck", "Go Read A Book", new ChessGame()));
        GameService gameService = new GameService(gameDAO, authDAO);
        ListGamesRequest listGamesRequest = new ListGamesRequest("0987");
        ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);
        assertEquals(listGamesResult.games(), gameDAO.listGames());
    }

    @Test
    @DisplayName("Create Game Bad Request")
    public void createGameBadRequest() throws DataAccessException {
        GameDAO gameDAO = new MemGameDAO();
        AuthDAO authDAO = new MemAuthDAO();
        GameService gameService = new GameService(gameDAO, authDAO);
        CreateGameRequest createGameRequest = new CreateGameRequest(null, "");
        Assertions.assertThrows(BadRequestException.class, () -> {
            gameService.createGame(createGameRequest);
        });
    }

    @Test
    @DisplayName("Create Game Unauthorized")
    public void createGameUnauthorized() throws DataAccessException {
        GameDAO gameDAO = new MemGameDAO();
        AuthDAO authDAO = new MemAuthDAO();
        GameService gameService = new GameService(gameDAO, authDAO);
        CreateGameRequest createGameRequest = new CreateGameRequest("Evil cow", "Revenge of the Cows");
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> gameService.createGame(createGameRequest));
        assertEquals("Error 401: Unauthorized", exception.getMessage());
        CreateGameRequest createGamesRequestTwo = new CreateGameRequest(null, "Cow");
        assertThrows(UnauthorizedException.class, () -> gameService.createGame(createGamesRequestTwo));
    }

    @Test
    @DisplayName("Create Game Success")
    public void createGameSuccess() throws DataAccessException {
        GameDAO gameDAO = new MemGameDAO();
        AuthDAO authDAO = new MemAuthDAO();
        authDAO.createAuth(new AuthData("Evil cow", "Cow"));
        GameService gameService = new GameService(gameDAO, authDAO);
        CreateGameRequest createGameRequest = new CreateGameRequest("Evil cow", "Revenge of the Cows");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        assertNotNull(createGameResult);
    }

    @Test
    @DisplayName("Clear")
    public void clear() throws DataAccessException {
        GameDAO gameDAO = new MemGameDAO();
        AuthDAO authDAO = new MemAuthDAO();
        UserDAO userDAO = new MemUserDAO();
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        gameDAO.createGame(new GameData(5678, "white", "black", "game2", new ChessGame()));
        authDAO.createAuth(new AuthData("01234", "cow"));
        userDAO.createUser(new UserData("cow", "cow@byu.edu", "moo"));
        assertNotNull(gameDAO.getGame(5678));
        assertNotNull(authDAO.getAuth("01234"));
        assertNotNull(userDAO.getUser("cow"));
        ClearResult clearResult = clearService.clear();
        assertNull(gameDAO.getGame(5678));
        assertNull(authDAO.getAuth("01234"));
        assertNull(userDAO.getUser("Cow"));
    }
}
