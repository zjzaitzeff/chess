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
                    if (CheckerAndStringHelper.loginCheck(loginInfo)) {
                        loggedIn = loginHelper(loginInfo);
                    } else {
                        System.out.print("login format incorrect, retry\n");
                    }
                }
                case "register" -> {
                    System.out.print("register format: <username> <password> <email>\n");
                    String registerInput = scanner.nextLine();
                    String[] registerInfo = registerInput.trim().split("\\s+");
                    if (CheckerAndStringHelper.registerCheck(registerInfo)) {
                        String username = registerInfo[0];
                        String password = registerInfo[1];
                        String email = registerInfo[2];
                        if (registerHelperNester(username,password,email)) {
                            loggedIn = true;
                        }
                    } else {
                        System.out.print("Incorrect input: <username> <password> <email>\nIf you want to register please type register\n");
                    }
                }
                default -> System.out.print("incorrect input, please use command Help to see correct inputs\n");
            }
        }
    }

    private static boolean registerHelperNester(String username, String password, String email) {
        if (register(username, password, email)) {
            if (login(username, password)) {
                System.out.print("Logged in as: " + username + "\n");
                return true;
            } else {
                System.out.print("Registered but failed to login\n");
                return false;
            }
        }
        return false;
    }

    private static boolean loginHelper(String[] loginInfo) {
        if (CheckerAndStringHelper.loginCheck(loginInfo)) {
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
            return true;
        } catch (DataAccessException exception) {
            return false;
        }
    }

    public static boolean register(String username, String password, String email) {
        try {
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);
            RegisterResult registerResult = facade.register(registerRequest);
            authToken = registerResult.authToken();
            System.out.print("Registered: " + registerResult.username() + "\n");
            return true;
        } catch (DataAccessException exception) {
            System.out.print("Registration failed, try again\n");
            return false;
        }
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
                    if (CheckerAndStringHelper.createCheck(createInfo)) {
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
                    if (CheckerAndStringHelper.joinCheck(joinInfo)) {
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
                        observe(id, scanner);
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

    private static void list() {
        try {
            ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
            ListGamesResult listGamesResult = facade.listGames(listGamesRequest);
            CheckerAndStringHelper.listHelper(listGamesResult);
        } catch (DataAccessException exception) {
            System.out.print("List games failed, try again"+"\n");
        }
    }

    private static void play(int id, String color, Scanner scanner) {
        try {
            JoinGameRequest joinGameRequest = new JoinGameRequest(color, id);
            JoinGameResult joinGameResult = facade.joinGame(joinGameRequest, authToken);
            ChessGame game = getGame(id);
            currColor = color;
            gameplay(color, game, scanner, id);
        } catch (DataAccessException exception) {
            System.out.print("Failed to join game: try again"+"\n");
        }
    }

    private static void gameplay(String color, ChessGame game, Scanner scanner, int gameID) {
        boolean gameOver = CheckerAndStringHelper.determineIfGameComplete(game);
        boolean wantsToLeave = false;
        ws.joinGame(authToken, gameID);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_BLACK + "Joined game! Type help for options\n");
        while (!wantsToLeave) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_BLACK + ">>> ");
            String input = scanner.nextLine();
            switch (input) {
                case "help" -> helpGameplay();
                case "redraw" -> PrintBoardHelper.printBoard(currGame, color, null);
                case "leave" -> {
                    wantsToLeave = true;
                    System.out.print("You have left the game. Returning to menu\n");
                    ws.leaveGame(authToken, gameID);
                    currGame = null;
                    currColor = null;
                }
                case "move" -> {
                    if (!gameOver) {
                        System.out.print("Move format <col piece><row piece> <col target><row target>\n");
                        System.out.print("Ex: e2 e4\n");
                        System.out.print("if you need to promote add <promotion> EX: queen\n");
                        String makeMoveInput = scanner.nextLine();
                        String[] makeMoveInfo = makeMoveInput.trim().split("\\s+");
                        if (CheckerAndStringHelper.makeMoveCheck(makeMoveInfo)) {
                            ChessMove chessMove = CheckerAndStringHelper.convertStringToChessMove(makeMoveInfo);
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
                    System.out.print("Highlight format <col><row>\nEX: e2 \n");
                    String highlightInput = scanner.nextLine();
                    String[] highlightInfo = highlightInput.trim().split("\\s+");
                    if (CheckerAndStringHelper.highlightCheck(highlightInfo)) {
                        ChessPosition chessPosition = CheckerAndStringHelper.createChessPosition(highlightInput);
                        PrintBoardHelper.printBoard(currGame, color, chessPosition);
                    } else {
                        System.out.print("Highlight format incorrect\n");
                    }
                }
                default -> System.out.print("Incorrect input, type help for option\n");
            }
        }
    }

    private static void helpGameplay() {
        System.out.print("help - Display possible options\n");
        System.out.print("redraw - redraws chessboard\n");
        System.out.print("leave - leave the game and return to previous menu\n");
        System.out.print("move - make a move\n");
        System.out.print("resign - resign *does not leave gameplay*\n");
        System.out.print("highlight - indicate legal moves\n");
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

    private static void clearDataBase() {
        try {
            ClearResult clearResult = facade.clear();
            //System.out.print("Successfully cleared the databases");
        } catch (DataAccessException exception) {
            System.out.print("Clear game failed, try again"+"\n");
        }
    }

    private static void observe(int id, Scanner scanner) {
        ChessGame game = getGame(id);
        if (game == null) {
            System.out.print("Game ID incorrect, create another game or use list to see available games\n");

        }
        else {
            currColor = "WHITE";
            ws.joinGame(authToken, id);
            boolean wantsToLeave = false;
            System.out.print("Observing game: " + id +"\n");
            while (!wantsToLeave) {
                System.out.print("type leave to stop observing, type help to see this message again\n");
                String input = scanner.nextLine();
                if (input.equals("leave")) {
                    System.out.print("leaving game\n");
                    wantsToLeave = true;
                    ws.leaveGame(authToken, id);
                    currColor = null;
                }
            }
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
            System.out.print("\n");
            PrintBoardHelper.printBoard(currGame, currColor, null);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_BLACK + ">>> ");
        } else if (serverMessage instanceof NotificationMessage notificationMessage) {
            String message = notificationMessage.getMessage();
            System.out.print("Notification: " + message);
        } else if (serverMessage instanceof ErrorMessage errorMessage) {
            String message = errorMessage.getMessage();
            System.out.print(message);
        }
    }
}
