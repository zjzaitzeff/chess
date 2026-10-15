package server;
import io.javalin.*;
import dataaccess.*;
import service.*;

public class Server {
    private final UserDAO userDAO = new MemUserDAO();
    private final AuthDAO authDAO = new MemAuthDAO();
    private final GameDAO gameDAO = new MemGameDAO();
    private final Javalin javalin;
    private final UserService userService = new UserService(userDAO, authDAO);
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here
        // clear, register, login, logout, listGames, createGame, joinGame
        javalin.delete("/db", new ClearHandler(clearService));

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
