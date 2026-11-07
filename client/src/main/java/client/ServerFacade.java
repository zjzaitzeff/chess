package client;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import com.google.gson.Gson;
import model.LoginResult;
import model.LoginRequest;
import model.ClearResult;
import model.LogoutResult;
import model.LogoutRequest;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.ListGamesRequest;
import model.ListGamesResult;
import model.JoinGameRequest;
import model.JoinGameResult;
import model.RegisterRequest;
import model.RegisterResult;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        var request = buildRequest("POST", "/user", registerRequest);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        var request = buildRequest("POST", "/session", loginRequest);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public ClearResult clear() throws DataAccessException {
        var request = buildRequest("DELETE", "/db", null);
        var response = sendRequest(request);
        return handleResponse(response, ClearResult.class);
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        var request = buildRequestAuth("DELETE", "/session",null, logoutRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response, LogoutResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken) throws DataAccessException {
        var request = buildRequestAuth("POST", "/game", createGameRequest, authToken);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        var request = buildRequestAuth("GET", "/game", null, listGamesRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest, String authToken) throws DataAccessException {
        var request = buildRequestAuth("PUT", "/game", joinGameRequest, authToken);
        var response = sendRequest(request);
        return handleResponse(response, JoinGameResult.class);
    }


    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest buildRequestAuth(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws DataAccessException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new DataAccessException(DataAccessException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw DataAccessException.fromJson(body);
            }

            throw new DataAccessException(DataAccessException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
