package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess {

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        String clearTextPassword = user.password();
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
        int id = executeUpdate(statement, user.username(), hashedPassword, user.email());
    }

    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var hashedPassword = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username,hashedPassword,email);
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE user";
        executeUpdate(statement);
        var statementAuthClear = "TRUNCATE TABLE auth";
        executeUpdate(statementAuthClear);
        var statementGameClear = "TRUNCATE TABLE game";
        executeUpdate(statementGameClear);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(255) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
            `authToken` varchar(256) NOT NULL,
            `username` varchar(256) NOT NULL,
            PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """,
            """
            CREATE TABLE IF NOT EXISTS game (
            `gameID` INT NOT NULL AUTO_INCREMENT,
            `whiteUsername` varchar(256) DEFAULT NULL,
            `blackUsername` varchar(256) DEFAULT NULL,
            `gameName` varchar(256) NOT NULL,
            `chessGame` TEXT NOT NULL,
            PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        int id = executeUpdate(statement, auth.authToken(), auth.username());
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readToken(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private AuthData readToken(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    public void createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)";
        String chessJson = new Gson().toJson(gameData.chess());
        int gameID = executeUpdate(statement, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), chessJson);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        String chessJson = rs.getString("chessGame");
        ChessGame game = new Gson().fromJson(chessJson, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    public void deleteGame(int gameID) throws DataAccessException {
        var statement = "DELETE FROM game WHERE gameID=?";
        executeUpdate(statement, gameID);
    }

    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> gamesList = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        gamesList.add(readGame(rs));
                    }
                }
            }
        } catch (Exception exception) {
            throw new DataAccessException("Unable to read data :(");
        }
        return gamesList;
    }
}
