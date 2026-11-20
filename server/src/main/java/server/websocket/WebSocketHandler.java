package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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
import java.util.ArrayList;
import java.util.Collection;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final ConnectionManager connections = new ConnectionManager();
    private final Collection<Integer> completedGames = new ArrayList<>();

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

    private void makeMove(String authToken, Session session, int gameID, ChessMove chessMove) throws DataAccessException, IOException {
        //verify validity of move
        try {
            if (completedGames.contains(gameID)) {
                sendErrorNotification(session, "Game complete, can not make more moves\n");
            } else {
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendErrorNotification(session, "gameID incorrect\n");
            } else if (authToken == null || authDAO.getAuth(authToken) == null) {
                sendErrorNotification(session, "authToken incorrect\n");
            } else if (checkIfMoveValid(authToken, gameData, chessMove)) {
                sendErrorNotification(session, "Chess Move Incorrect. Either not your turn, not your piece, or invalid move\n");
            } else {
                //update game
                ChessGame game = gameData.chess();
                try {
                    game.makeMove(chessMove);
                    //send LOAD_GAME to all clients
                    connections.broadcastLoadGameAllPlayers(new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game));
                    //send Notification to all OTHER clients informing them what move was made
                    String message = String.format("%s (%s) moved to %s", authDAO.getAuth(authToken).username(), getColor(authToken, gameID), chessMove.toString());
                    connections.broadcast(session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
                    //if move results in check, checkmate, stalemate, Send Notification to all clients
                    checks(authToken, gameID);
                } catch (InvalidMoveException | IOException exception) {
                    sendErrorNotification(session, "Invalid move\n");
                }
            }
        }
        } catch (DataAccessException exception) {
            throw new DataAccessException("database error");
        }
    }

    private void checks(String authToken, int gameID) throws DataAccessException, IOException {
        ChessGame game = gameDAO.getGame(gameID).chess();
        String color = getColor(authToken, gameID);
        ChessGame.TeamColor playerColor = returnTeamColor(color);
        if (playerColor != null) {
            if (game.isInCheck(playerColor)) {
                String message = String.format("%s is in check\n", convertToStringFromTeamObject(game.getTeamTurn()));
                connections.broadcast(null, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
            } else if (game.isInStalemate(playerColor)) {
                String message = String.format("%s is in stalemate\n", convertToStringFromTeamObject(game.getTeamTurn()));
                connections.broadcast(null, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
                completedGames.add(gameID);
            } else if (game.isInCheckmate(playerColor)) {
                String message = String.format("%s is in check", convertToStringFromTeamObject(game.getTeamTurn()));
                connections.broadcast(null, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
                completedGames.add(gameID);
            }
        }
    }

    private String convertToStringFromTeamObject(ChessGame.TeamColor color) {
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            return "WHITE";
        } else if (color.equals(ChessGame.TeamColor.BLACK)) {
            return "BLACK";
        }
        return null;
    }

    private boolean checkIfMoveValid(String authToken, GameData gameData, ChessMove move) throws DataAccessException {
        //wrong color
        String color = getColor(authToken, gameData.gameID());
        ChessGame.TeamColor playerColor = returnTeamColor(color);
        if (playerColor == null) {
            return false;
        }
        ChessGame.TeamColor turnColor = gameData.chess().getTeamTurn();
        if (!turnColor.equals(playerColor)) {
            return false;
        }
        //not able to move to that location
        Collection<ChessMove> validMoves = gameData.chess().validMoves(move.getStartPosition());
        return validMoves.contains(move);
    }

    private ChessGame.TeamColor returnTeamColor(String color) {
        if (color.equals("WHITE")) {
            return ChessGame.TeamColor.WHITE;
        } else if (color.equals("BLACK")) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return null;
        }
    }

    private void sendErrorNotification(Session session, String message) throws IOException {
        if (session.isOpen()) {
            NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Error: " + message);
            session.getRemote().sendString(new Gson().toJson(notificationMessage));
        }
    }
}
