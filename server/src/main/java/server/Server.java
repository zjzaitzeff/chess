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
    private final GameService gameService = new GameService(gameDAO, authDAO);
    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here
        javalin.delete("/db", new ClearHandler(clearService));
        javalin.post("/user", new RegisterHandler(userService));
        javalin.post("/session", new LoginHandler(userService));
        javalin.delete("/session", new LogoutHandler(userService));
        javalin.get("/game", new ListGamesHandler(gameService));
        javalin.post("/game", new CreateGameHandler(gameService));
        javalin.put("/game", new JoinGameHandler(gameService));



    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
