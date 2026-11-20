package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {

    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        System.out.println("Websocket connected");
        wsConnectContext.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        try {
            UserGameCommand action = new Gson().fromJson(wsMessageContext.message(), UserGameCommand.class);
            switch (action.getCommandType()) {
                case UserGameCommand.CommandType.CONNECT -> enter(action.getAuthToken(), wsMessageContext.session, action.getGameID());
                case UserGameCommand.CommandType.RESIGN -> resign(action.getAuthToken(), wsMessageContext.session, action.getGameID());
                case UserGameCommand.CommandType.LEAVE -> exit(action.getAuthToken(), wsMessageContext.session, action.getGameID());
                case UserGameCommand.CommandType.MAKE_MOVE -> makeMove(action.getAuthToken(), wsMessageContext.session, action.getGameID(), action.getChessMove());
            }

        } catch (IOException ex) {
            throw new IOException("Websocket failed");
        }
    }

    private void resign(String authToken, Session session, int gameID) throws IOException {
        // still need to mark game as complete
        try {
            String username = authDAO.getAuth(authToken).username();
            var message = String.format("%s resigned. Game over\n", username);
            connections.broadcast(null, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void enter(String authToken, Session session, int gameID) throws IOException {
        try {
            connections.add(session);
            if (session.isOpen()) {
                LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME ,gameDAO.getGame(gameID).chess());
                session.getRemote().sendString(new Gson().toJson(loadGameMessage));
            }
            String color = getColor(authToken, gameID);
            var message = String.format("%s joined the game as %s\n", authDAO.getAuth(authToken).username(), color);
            connections.broadcast(session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION , message));
        } catch (IOException exception) {
            throw new IOException("Websocket failed\n");

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String getColor(String authToken, int gameID) throws DataAccessException {
        try {
            String username = authDAO.getAuth(authToken).username();
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData != null) {
                if (username.equals(gameData.whiteUsername())) {
                    return "WHITE";
                } else if (username.equals(gameData.blackUsername())) {
                    return "BLACK";
                }
            }
            return "OBSERVER";
        } catch (DataAccessException exception) {
            throw new DataAccessException("Websocket failed");
        }
    }

    private void exit(String authToken, Session session, int gameID) throws IOException {
        try {
            String username = authDAO.getAuth(authToken).username();
            var message = String.format("%s left the game", username);
            connections.broadcast(session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
            connections.remove(session);
            GameData game = gameDAO.getGame(gameID);
            String color = getColor(authToken, gameID);
            if (game != null) {
                if (color.equals("WHITE")) {
                    gameDAO.updateGame(gameID, new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.chess()));
                } else if (color.equals("BLACK")) {
                    gameDAO.updateGame(gameID, new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.chess()));
                }
            }
        } catch (IOException exception) {
            throw new IOException("Websocket failed");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void makeMove(String authToken, Session session, int gameID, ChessMove chessMove) {
        //should mark game as complete if move puts into checkmate

    }
}
