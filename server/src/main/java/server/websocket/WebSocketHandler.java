package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
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
    //private final Collection<Integer> completedGames = new ArrayList<>();

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
                case UserGameCommand.CommandType.RESIGN -> resign(action.getAuthToken(), action.getGameID(), wsMessageContext.session);
                case UserGameCommand.CommandType.LEAVE -> exit(action.getAuthToken(), wsMessageContext.session, action.getGameID());
                case UserGameCommand.CommandType.MAKE_MOVE -> {
                    String authToken = action.getAuthToken();
                    Session session = wsMessageContext.session;
                    int gameID = action.getGameID();
                    ChessMove move = action.getChessMove();
                    makeMove(authToken, session , gameID, move);
                }
            }

        } catch (IOException ex) {
            throw new IOException("Websocket failed");
        }
    }

    private boolean checkAuthTokenAndGameIDCorrect(String authToken, int gameID, Session session) throws DataAccessException, IOException {
        try {
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendErrorNotification(session, "gameID incorrect\n");
                return false;
            } else if (authToken == null || authDAO.getAuth(authToken) == null) {
                sendErrorNotification(session, "authToken incorrect\n");
                return false;
            }
            return true;
        } catch (DataAccessException exception) {
            throw new DataAccessException("authToken or gameID incorrect");
        }
    }

    private void resign(String authToken, int gameID, Session session) throws IOException {
        try {
            if (checkAuthTokenAndGameIDCorrect(authToken, gameID, session)) {
                if (!isObserver(authToken, gameID)) {
                    if (gameDAO.getGame(gameID).chess().isGameOver()) {
                        sendErrorNotification(session, "Game over can not resign.\n");
                        return;
                    }
                    String username = authDAO.getAuth(authToken).username();
                    var message = String.format("%s resigned. Game over\n", username);
                    connections.broadcast(null, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message), gameID);
                    GameData gameData = gameDAO.getGame(gameID);
                    ChessGame game = gameData.chess();
                    game.gameNowOver();
                    gameDAO.updateGame(gameID, new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
                } else {
                    sendErrorNotification(session, "observer can not resign\n");
                }
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private boolean isObserver(String authToken, int gameID) throws DataAccessException {
        try {
            String whiteUsername = gameDAO.getGame(gameID).whiteUsername();
            String blackUsername = gameDAO.getGame(gameID).blackUsername();
            String username = authDAO.getAuth(authToken).username();
            return !username.equals(whiteUsername) && !username.equals(blackUsername);
        } catch (DataAccessException e) {
            throw new DataAccessException("database error");
        }

    }

    private void enter(String authToken, Session session, int gameID) throws IOException {
        try {
            if (checkAuthTokenAndGameIDCorrect(authToken, gameID, session)) {
                connections.add(session, gameID);
                if (session.isOpen()) {
                    LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameDAO.getGame(gameID).chess());
                    session.getRemote().sendString(new Gson().toJson(loadGameMessage));
                }
                String color = getColor(authToken, gameID);
                var message = String.format("%s joined the game as %s\n", authDAO.getAuth(authToken).username(), color);
                connections.broadcast(session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message), gameID);
            }
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
            if (checkAuthTokenAndGameIDCorrect(authToken, gameID, session)) {
                String username = authDAO.getAuth(authToken).username();
                var message = String.format("%s left the game\n", username);
                GameData game = gameDAO.getGame(gameID);
                String color = getColor(authToken, gameID);
                if (game != null) {
                    if (color.equals("WHITE")) {
                        gameDAO.updateGame(gameID, new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.chess()));
                    } else if (color.equals("BLACK")) {
                        gameDAO.updateGame(gameID, new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.chess()));
                    }
                }
                connections.broadcast(session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message), gameID);
                connections.remove(session, gameID);
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
            if (gameDAO.getGame(gameID).chess().isGameOver()) {
                sendErrorNotification(session, "Game complete, can not make more moves\n");
            } else {
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendErrorNotification(session, "gameID incorrect\n");
            } else if (authToken == null || authDAO.getAuth(authToken) == null) {
                sendErrorNotification(session, "authToken incorrect\n");
            } else if (!checkIfMoveValid(authToken, gameData, chessMove)) {
                sendErrorNotification(session, "Chess Move Incorrect. Either not your turn, not your piece, or invalid move\n");
            } else {
                //update game
                ChessGame game = gameData.chess();
                try {
                    game.makeMove(chessMove);
                    int currID = gameData.gameID();
                    String wUsername = gameData.whiteUsername();
                    String bUsername = gameData.blackUsername();
                    String name = gameData.gameName();
                    gameDAO.updateGame(currID, new GameData(currID, wUsername, bUsername, name, game));
                    //send LOAD_GAME to all clients
                    connections.broadcastLoadGameAllPlayers(new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game), gameID);
                    //send Notification to all OTHER clients informing them what move was made
                    String message = String.format("%s (%s) moved: %s\n", authDAO.getAuth(authToken).username(), getColor(authToken, gameID), chessMoveString(chessMove));
                    connections.broadcast(session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message), gameID);
                    //if move results in check, checkmate, stalemate, Send Notification to all clients
                    checks(authToken, gameID);
                } catch (InvalidMoveException | IOException exception) {
                    sendErrorNotification(session, "Invalid move\n");
                }
            }
        }
        } catch (DataAccessException exception) {
            throw new DataAccessException("database error\n");
        }
    }

    private String chessMoveString(ChessMove chessMove) {
        ChessPosition startPosition = chessMove.getStartPosition();
        ChessPosition endPosition = chessMove.getEndPosition();
        String startPositionString = generatePosString(startPosition);
        String endPositionString = generatePosString(endPosition);
        String promotionString;
        if (chessMove.getPromotionPiece() != null) {
            promotionString = generatePromotionPieceString(chessMove.getPromotionPiece());
        } else {
            promotionString = "";
        }
        return String.format("%s to %s %s", startPositionString, endPositionString, promotionString);
    }

    private String generatePosString(ChessPosition chessPosition) {
        int r = chessPosition.getRow();
        int c = chessPosition.getColumn();
        String cString;
        if (c == 1) {
            cString = "a";
        } else if (c == 2) {
            cString = "b";
        } else if (c == 3) {
            cString = "c";
        } else if (c==4) {
            cString = "d";
        } else if (c==5) {
            cString = "e";
        } else if (c==6) {
            cString = "f";
        } else if (c==7) {
            cString ="g";
        } else if (c==8) {
            cString = "h";
        } else {;
            cString="something went horribly wrong";
        }
        return String.format("%s%s", cString, r);
    }

    private String generatePromotionPieceString(ChessPiece.PieceType type) {
        if (type.equals(ChessPiece.PieceType.QUEEN)) {
            return "promotion: queen";
        } else if (type.equals(ChessPiece.PieceType.ROOK)) {
            return "promotion: rook";
        } else if (type.equals(ChessPiece.PieceType.KNIGHT)) {
            return "promotion: knight";
        } else {
            return "promotion: bishop";
        }
    }

    private void checks(String authToken, int gameID) throws DataAccessException, IOException {
        ChessGame game = gameDAO.getGame(gameID).chess();
        String color = getColor(authToken, gameID);
        String oppColor = getOppositeColor(color);
        ChessGame.TeamColor playerColor = returnTeamColor(oppColor);
        if (playerColor != null) {
            if (game.isInStalemate(playerColor)) {
                String message = String.format("%s is in stalemate\n", convertToStringFromTeamObject(game.getTeamTurn()));
                connections.broadcast(null, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message), gameID);
                GameData gameData = gameDAO.getGame(gameID);
                game.gameNowOver();
                gameDAO.updateGame(gameID, new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
            } else if (game.isInCheckmate(playerColor)) {
                String message = String.format("%s is in checkmate\n", convertToStringFromTeamObject(game.getTeamTurn()));
                connections.broadcast(null, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message), gameID);
                GameData gameData = gameDAO.getGame(gameID);
                game.gameNowOver();
                gameDAO.updateGame(gameID, new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
            } else if (game.isInCheck(playerColor)) {
                String message = String.format("%s is in check\n", convertToStringFromTeamObject(game.getTeamTurn()));
                connections.broadcast(null, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message), gameID);
            }
        }
    }

    private String getOppositeColor(String color) {
        if (color.equals("WHITE")) {
            return "BLACK";
        } else {
            return "WHITE";
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
        if (validMoves == null) {
            return false;
        }
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
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,"Error: " + message);
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }
}
