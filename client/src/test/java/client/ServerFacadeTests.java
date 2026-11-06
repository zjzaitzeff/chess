import client.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;
import service.*;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:"+port);
    }

    @AfterAll
    static void stopServer() {
        facade.clear();
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    void register() throws Exception {
        UserService.RegisterRequest registerRequest = new UserService.RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(registerRequest);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerFail() throws Exception {
        UserService.RegisterRequest registerRequest = new UserService.RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(registerRequest);
        UserService.RegisterRequest registerRequestTwo = new UserService.RegisterRequest("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () -> facade.register(registerRequestTwo));
    }



}
