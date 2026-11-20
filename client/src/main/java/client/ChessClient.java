package client;

import chess.*;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import model.GameData;
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
import ui.EscapeSequences;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class ChessClient implements NotificationHandler {
    private static String authToken = null;
    private static ServerFacade facade;
    private static WebSocketFacade ws = null;
    private static String currUsername = null;
    private static ChessGame currGame = null;
    private static String currColor = null;

    public ChessClient(String serverURL) {
        facade = new ServerFacade(serverURL);
        ws = new WebSocketFacade(serverURL, this);
    }

    public void run() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_BLACK + "♕ 240 Chess Client: ");
        boolean loggedIn = false;
        String prefix = "[LOGGED OUT] >>> ";
        Scanner scanner = new Scanner(System.in);
        System.out.print("Type help to get started!\n");
        while (true) {
            if (!loggedIn) {
                preUI(prefix, loggedIn, scanner);
                prefix = "[LOGGED IN] >>> ";
                loggedIn = true;
            } else {
                postUI(prefix, scanner, loggedIn);
                prefix = "[LOGGED OUT] >>> ";
                loggedIn = false;
            }
        }
    }
    private static void quit(Scanner scanner) {
        System.out.print("Quiting Game");
        scanner.close();
        clearDataBase();
        System.exit(0);
    }

    private static void preUI(String prefix, boolean loggedIn, Scanner scanner) {
        while (!loggedIn) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_BLACK +prefix);
            String input = scanner.nextLine();
            switch (input) {
                case "quit" -> {
                    quit(scanner);
                }
                case "help" -> helpPre();
                case "login" -> {
                    System.out.print("login format: <username> <password>\n");
                    String loginInput = scanner.nextLine();
                    String[] loginInfo = loginInput.trim().split("\\s+");
                    if (loginCheck(loginInfo)) {
                        loggedIn = loginHelper(loginInfo);
                    }
                }
                case "register" -> {
                    System.out.print("register format: <username> <password> <email>\n");
                    String registerInput = scanner.nextLine();
                    String[] registerInfo = registerInput.trim().split("\\s+");
                    if (registerCheck(registerInfo)) {
                        String username = registerInfo[0];
                        String password = registerInfo[1];
                        String email = registerInfo[2];
                        register(username, password, email);
                        if (login(username, password)) {
                            System.out.print("Logged in as: " + username + "\n");
                            loggedIn = true;
                        } else {
                            System.out.print("Registered but failed to login\n");
                        }
                    } else {
                        System.out.print("Incorrect input: <username> <password> <email>\nIf you want to register please type register\n");
                    }
                }
                default -> System.out.print("incorrect input, please use command Help to see correct inputs\n");
            }
        }
    }

    private static boolean loginHelper(String[] loginInfo) {
        if (loginCheck(loginInfo)) {
            String username = loginInfo[0];
            String password = loginInfo[1];
            if (login(username, password)) {
                System.out.print("Logged in as: " + username + "\n");
                return true;
            } else {
                System.out.print("Login failed due to token issue, make sure you are registered\n");
                return false;
            }
        } else {
            System.out.print("Incorrect input: <username> <password>\nIf you want to login please type login\n");
            return false;
        }
    }

    public static void helpPre() {
        System.out.print("register - create a user\nlogin - to play chess\nquit - Stop playing chess\nhelp - review this message again\n");
    }

    public static boolean login(String username, String password) {
        try {
            LoginRequest loginRequest = new LoginRequest(username, password);
            LoginResult loginResult = facade.login(loginRequest);
            authToken = loginResult.authToken();
            currUsername = username;
            return true;
        } catch (DataAccessException exception) {
            return false;
        }
    }

    private static boolean loginCheck(String[] info) {
        return info.length == 2;
    }

    public static void register(String username, String password, String email) {
        try {
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);
            RegisterResult registerResult = facade.register(registerRequest);
            authToken = registerResult.authToken();
            System.out.print("Registered: " + registerResult.username() + "\n");
        } catch (DataAccessException exception) {
            System.out.print("Registration failed, try again\n");
        }
    }

    private static boolean registerCheck(String[] info) {
        return info.length == 3;
    }

    private static void postUI(String prefix, Scanner scanner, boolean loggedIn) {
        while (loggedIn) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_BLACK + prefix);
            String input = scanner.nextLine();
            switch (input) {
                case "help" -> helpPost();
                case "logout" -> {
                    //needs an authToken probably
                    logout();
                    loggedIn = false;
                }
                case "create" -> {
                    System.out.print("Create format: <NAME>\n");
                    String createInput = scanner.nextLine();
                    String[] createInfo = createInput.trim().split("\\s+");
                    if (createCheck(createInfo)) {
                        String gameName = createInfo[0];
                        create(gameName);
                    } else {
                        System.out.print("Create format incorrect: <NAME>\n");
                    }
                }
                case "list" -> list();
                case "join" -> {
                    System.out.print("join format: <ID> [WHITE|BLACK]\n");
                    String joinInput = scanner.nextLine();
                    String[] joinInfo = joinInput.trim().split("\\s+");
                    if (joinCheck(joinInfo)) {
                        String strID = joinInfo[0];
                        int id = parseInt(strID);
                        String color = joinInfo[1];
                        play(id, color, scanner);
                    } else {
                        System.out.print("Join format incorrect: <ID> [WHITE|BLACK]\n");
                    }
                }
                case "observe" -> {
                    System.out.print("observe format: <ID>\n");
                    String observeInput = scanner.nextLine();
                    String[] observeInfo = observeInput.trim().split("\\s+");
                    if (observeCheck(observeInfo)) {
                        String strId = observeInfo[0];
                        int id = parseInt(strId);
                        observe(id);
                    } else {
                        System.out.print("Observe format incorrect: <ID>\n");
                    }
                }
                default -> System.out.print("Incorrect input, please use command Help to see correct inputs\n");
            }
        }
    }

    private static void helpPost() {
        System.out.print("create - Create a game\nhelp - possible commands\n");
        System.out.print("list - list games\n");
        System.out.print("join - join a game\n");
        System.out.print("observe - observe a game\n");
        System.out.print("logout - For when done, access to return to pre-login menu\n");
        System.out.print("help - possible commands\n");

    }

    private static void logout() {
        try {
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            LogoutResult logoutResult = facade.logout(logoutRequest);
            authToken = null;
            System.out.print("Successfully logged out" +"\n");
        } catch (DataAccessException exception) {
            System.out.print("Logout failed try again"+ "\n");
        }
    }

    private static void create(String gameName) {
        try {
            CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
            CreateGameResult createGameResult = facade.createGame(createGameRequest, authToken);
            System.out.print("Successfully created game! Game ID: " + createGameResult.gameID()+ "\n");
        } catch (DataAccessException exception) {
            System.out.print("Create game failed, try again"+"\n");
        }
    }

    private static boolean createCheck(String[] info) {
        return info.length == 1;
    }

    private static void list() {
        try {
            ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
            ListGamesResult listGamesResult = facade.listGames(listGamesRequest);
            listHelper(listGamesResult);
        } catch (DataAccessException exception) {
            System.out.print("List games failed, try again"+"\n");
        }
    }

    private static void listHelper(ListGamesResult lgr) {
        for (GameData game : lgr.games()) {
            String whiteUser;
            String blackUser;
            if (game.whiteUsername() == null) {
                whiteUser = "Open Spot";
            } else {
                whiteUser = game.whiteUsername();
            }
            if (game.blackUsername() == null) {
                blackUser = "Open Spot";
            } else {
                blackUser = game.blackUsername();
            }
            System.out.print("GameID: " + game.gameID() + EscapeSequences.SET_TEXT_COLOR_GREEN + " Game Name: " + game.gameName() + EscapeSequences.SET_TEXT_COLOR_BLUE +" White Player: " + whiteUser + EscapeSequences.SET_TEXT_COLOR_YELLOW + " Black Player: " + blackUser +"\n");
        }
    }

    private static void play(int id, String color, Scanner scanner) {
        try {
            JoinGameRequest joinGameRequest = new JoinGameRequest(color, id);
            JoinGameResult joinGameResult = facade.joinGame(joinGameRequest, authToken);
//            System.out.print("Joined game: initiating gameplay"+"\n");
            ChessGame game = getGame(id);
            currColor = color;
            gameplay(color, game, true, scanner, id);
//            assert game != null;
//            printBoard(game, color);
        } catch (DataAccessException exception) {
            System.out.print("Failed to join game: try again"+"\n");
        }
    }

    private static void gameplay(String color, ChessGame game, boolean isPlayer, Scanner scanner, int gameID) {
        //help - display options
        //redraw chess board - redraws chessboard
        //leave - return to post UI
        // Make Move - make a move
        // Resign - Doesn't leave but ends game
        // Highlight legal Moves - input piece for which they want to highlight legal moves, the selected pieces's current square and all squares it can legally move are highlighted.
        boolean gameOver = false;
        boolean wantsToLeave = false;
        ws.joinGame(authToken, gameID);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_BLACK + "Joined game! Type help for options\n");
        while (!wantsToLeave) {
            String input = scanner.nextLine();
            switch (input) {
                case "help" -> helpGameplay();
                case "redraw" -> printBoard(game, color);
                case "leave" -> {
                    wantsToLeave = true;
                    System.out.print("You have left the game. Returning to menu\n");
                    ws.leaveGame(authToken, gameID);
                    currGame = null;
                    currColor = null;
                }
                case "make move" -> {
                    if (!gameOver) {
                        System.out.print("Move format <row piece><col piece> <row target><col target> <promotion>\nEx: e2 e4 pawn\n");
                        String makeMoveInput = scanner.nextLine();
                        String[] makeMoveInfo = makeMoveInput.trim().split("\\s+");
                        if (makeMoveCheck(makeMoveInfo)) {
                            System.out.print("will make move " + makeMoveInput + "\n");
                            ChessMove chessMove = convertStringToChessMove(makeMoveInfo);
                            ws.makeMove(authToken, gameID, chessMove);
                        } else {
                            System.out.print("Incorrect format, please retry\n");
                        }
                    } else {
                        System.out.print("game is over you can not make a move\n");
                    }
                }
                case "resign" -> {
                    gameOver = true;
                    ws.resign(authToken, gameID);
                }
                case "highlight" -> {
                }
                default -> System.out.print("Incorrect input, type help for option\n");
            }
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_BLACK + ">>> ");
        }
    }

    private static ChessMove convertStringToChessMove(String[] input) {
        String pos1 = input[0];
        String pos2 = input[1];
        String promotion = input[3];
        ChessPosition chessPosition1 = createChessPosition(pos1);
        ChessPosition chessPosition2 = createChessPosition(pos2);
        ChessPiece.PieceType promotionType = generatePromotionType(promotion);
        return new ChessMove(chessPosition1, chessPosition2, promotionType);
    }

    private static ChessPosition createChessPosition(String pos) {
        int col = Integer.parseInt(String.valueOf(pos.charAt(1)));
        String r = String.valueOf(pos.charAt(0));
        int row = switch (r) {
            case "a" -> 1;
            case "b" -> 2;
            case "c" -> 3;
            case "d" -> 4;
            case "e" -> 5;
            case "f" -> 6;
            case "g" -> 7;
            case "h" -> 8;
            default -> 0;
        };
        return new ChessPosition(row,col);
    }

    private static ChessPiece.PieceType generatePromotionType(String promotion) {
        return switch (promotion) {
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "queen" -> ChessPiece.PieceType.QUEEN;
            default -> ChessPiece.PieceType.ROOK;
        };
    }

    private static boolean makeMoveCheck(String[] input) {
        Collection<String> approvedLetters = List.of("a", "b", "c", "d", "e", "f", "g", "h");
        Collection<String> approvedNumbers = List.of("1", "2", "3", "4", "5", "6", "7", "8");
        Collection<String> approvedPromotions = List.of("knight","bishop","queen","rook");
        if (input.length != 3) {
            return false;
        }
        String pos1 = input[0];
        String pos2 = input[1];
        String promotion = input[2];
        if (pos1.length() != 2) {
            return false;
        }
        if (pos2.length() !=2) {
            return false;
        }
        if (!approvedLetters.contains(String.valueOf(pos1.charAt(0)))) {
            return false;
        }
        if (!approvedNumbers.contains(String.valueOf(pos1.charAt(1)))) {
            return false;
        }
        if (!approvedLetters.contains(String.valueOf(pos2.charAt(0)))) {
            return false;
        }
        if (!approvedNumbers.contains(String.valueOf(pos2.charAt(1)))) {
            return false;
        }
        if (!approvedPromotions.contains(promotion)) {
            return false;
        }
        return true;
    }

    private static void helpGameplay() {
        System.out.print("help - Display possible options\n");
        System.out.print("redraw - redraws chessboard\n");
        System.out.print("leave - leave the game and return to previous menu\n");
        System.out.print("make move - make a move\n");
        System.out.print("resign - resign *does not leave gameplay*\n");
        System.out.print("highlight- indicate legal moves\n");
    }

    private static ChessGame getGame(int id) {
        try {
            ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
            ListGamesResult listGamesResult = facade.listGames(listGamesRequest);
            for (GameData game : listGamesResult.games()) {
                if (game.gameID() == id) {
                    return game.chess();
                }
            }
        } catch (DataAccessException exception) {
            System.out.print("gameID was incorrect"+"\n");
            return null;
        }
        return null;
    }

    private static void printBoard(ChessGame game, String color) {
        ChessBoard board = game.getBoard();
        if (color.equals("WHITE")) {
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + "  A  B  C  D  E  F  G  H  \n");
            for (int i = 8; i > 0; i--) {
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + i);
                if (i % 2 == 0) {
                    printWhiteRowWhite(board, i);
                } else {
                    printBlackRowWhite(board, i);
                }
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + i + "\n");
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + "  A  B  C  D  E  F  G  H  \n");
        } else {
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + "  H  G  F  E  D  C  B  A  \n");
            for (int i = 1; i < 9; i++) {
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + i);
                if (i % 2 == 0) {
                    printBlackRowBlack(board, i);
                } else {
                    printWhiteRowBlack(board, i);
                }
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + i + "\n");
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + "  H  G  F  E  D  C  B  A  \n");
        }
    }

    private static void printWhiteRowWhite(ChessBoard board, int r) {
        for (int j = 1; j < 9; j++) {
            whiteRowHelper(board,r,j);
        }
    }

    private static void printBlackRowWhite(ChessBoard board, int r) {
        for (int j = 1; j < 9; j++) {
            blackRowHelper(board, r, j);
        }
    }

    private static void printWhiteRowBlack(ChessBoard board, int r) {
        for (int j = 8; j > 0; j --) {
            whiteRowHelper(board,r,j);
        }
    }

    private static void printBlackRowBlack(ChessBoard board, int r) {
        for (int j = 8; j > 0; j --) {
            blackRowHelper(board, r, j);
        }
    }

    private static void blackRowHelper(ChessBoard board, int r, int j) {
        if (j % 2 == 0) {
            System.out.print(EscapeSequences.SET_BG_COLOR_MAGENTA + getPieceStr(board, r, j));
        } else {
            System.out.print(EscapeSequences.SET_BG_COLOR_RED + getPieceStr(board, r, j));
        }
    }

    private static void whiteRowHelper(ChessBoard board, int r, int j) {
        if (j % 2 == 0) {
            System.out.print(EscapeSequences.SET_BG_COLOR_RED + getPieceStr(board, r, j));
        } else {
            System.out.print(EscapeSequences.SET_BG_COLOR_MAGENTA + getPieceStr(board, r, j));
        }
    }
    private static String getPieceStr(ChessBoard board, int r, int c) {
        ChessPiece p = board.getPiece(new ChessPosition(r,c));
        if (p == null) {
            return "   ";
        } else if (p.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return EscapeSequences.SET_TEXT_COLOR_WHITE + pieceSelect(p);
        } else {
            return EscapeSequences.SET_TEXT_COLOR_BLACK + pieceSelect(p);
        }
    }

    private static String pieceSelect(ChessPiece piece) {
        if (piece == null) {
            return "   ";
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN)) {
            return " P ";
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.ROOK)) {
            return " R ";
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.KNIGHT)) {
            return " N ";
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.BISHOP)) {
            return " B ";
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.QUEEN)) {
            return " Q ";
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.KING)) {
            return " K ";
        } else {
            return "   ";
        }
    }

    private static void clearDataBase() {
        try {
            ClearResult clearResult = facade.clear();
            //System.out.print("Successfully cleared the databases");
        } catch (DataAccessException exception) {
            System.out.print("Clear game failed, try again"+"\n");
        }
    }

    private static boolean joinCheck(String[] info) {
        if (info.length != 2) {
            return false;
        }
        boolean isNum;
        try {
            parseInt(info[0]);
            isNum = true;
        } catch (NumberFormatException exception) {
            isNum = false;
        }
        if (!isNum) {
            return false;
        }
        return info[1].equals("WHITE") || info[1].equals("BLACK");
    }

    private static void observe(int id) {
        ChessGame game = getGame(id);
        if (game == null) {
            System.out.print("Game ID incorrect, create another game or use list to see available games\n");

        }
        else {
            printBoard(game, "WHITE");
        }
    }

    private static boolean observeCheck(String[] info) {
        if (info.length != 1) {
            return false;
        }
        try {
            parseInt(info[0]);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        if (serverMessage instanceof LoadGameMessage loadGameMessage) {
            currGame = loadGameMessage.returnGame();
            printBoard(currGame, currColor);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_BLACK + ">>> ");
        } else if (serverMessage instanceof NotificationMessage notificationMessage) {
            String message = notificationMessage.getMessage();
            System.out.print("Notification: " + message);
        } else if (serverMessage instanceof ErrorMessage errorMessage) {
            String message = errorMessage.getMessage();
            System.out.print("Error: " + message);
        }
    }
}
