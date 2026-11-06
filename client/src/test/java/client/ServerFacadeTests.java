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
    void registerTest() throws Exception {
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

    private UserService.RegisterResult register() throws Exception {
        UserService.RegisterRequest registerRequest = new UserService.RegisterRequest("player1", "password", "p1@email.com");
        return facade.register(registerRequest);
    }

    @Test
    void loginTest() throws Exception {
        register();
        LoginResult loginResult = facade.login(new LoginRequest("player1", "password"));
        assertEquals("player1", loginResult.username());
        assertNotNull(loginResult.authToken());
    }

    @Test
    void loginFail() throws Exception {
        register();
        LoginResult loginResult = facade.login(new LoginRequest("player1", "password"));
        assertThrows(Exception.class, () -> facade.login(new LoginRequest("player1", "moo")));
    }

    @Test
    void clear() throws Exception {

    }

    private LoginResult login() throws Exception {
        return facade.login(new LoginRequest("player1", "password"));
    }

    @Test
    void logout () throws Exception {
        UserService.RegisterResult rr = register();
        LoginResult lr = login();
        LogoutRequest logoutRequest = new LogoutRequest(lr.authToken());
        LogoutResult logoutResult = facade.logout(logoutRequest);
        assertNotNull(logoutResult);
    }

    @Test
    void logoutFail() throws Exception {
        UserService.RegisterResult rr = register();
        LoginResult lr = login();
        LogoutRequest logoutRequestTest = new LogoutRequest("fakeAuthToken");
        assertThrows(Exception.class, () -> facade.logout(logoutRequestTest));
    }


}
