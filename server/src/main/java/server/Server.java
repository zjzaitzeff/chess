package server;
import io.javalin.*;
import dataaccess.*;
import server.websocket.WebSocketHandler;
import service.*;

public class Server {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final Javalin javalin;
    private final UserService userService;
    private final ClearService clearService;
    private final GameService gameService;
    private final WebSocketHandler webSocketHandler;
    public Server() {
        try {
            userDAO = new MySqlDataAccessUser();
            authDAO = new MySqlDataAccessAuth();
            gameDAO = new MySqlDataAccessGame();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Failed to generate sql or other problems that I don't completely understand but this somehow fixes it");
        }
        userService = new UserService(userDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        gameService = new GameService(gameDAO, authDAO);
        webSocketHandler = new WebSocketHandler();
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.delete("/db", new ClearHandler(clearService));
        javalin.post("/user", new RegisterHandler(userService));
        javalin.post("/session", new LoginHandler(userService));
        javalin.delete("/session", new LogoutHandler(userService));
        javalin.get("/game", new ListGamesHandler(gameService));
        javalin.post("/game", new CreateGameHandler(gameService));
        javalin.put("/game", new JoinGameHandler(gameService));
        javalin.ws("/ws", ws -> {
            ws.onConnect(webSocketHandler);
            ws.onMessage(webSocketHandler);
            ws.onClose(webSocketHandler);
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
