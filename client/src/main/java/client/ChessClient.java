package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
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
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class ChessClient {
    private static String authToken = null;
    private static ServerFacade facade;

    public ChessClient(String serverURL) {
        facade = new ServerFacade(serverURL);
    }

    public void run() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_BLACK + "♕ 240 Chess Client: ");
        boolean loggedIn = false;
        String prefix = "[LOGGED OUT] >>> ";
        Scanner scanner = new Scanner(System.in);
        System.out.print("Type help to get started!\n");
//        printBoard(new ChessGame(), "WHITE");
//        printBoard(new ChessGame(), "BLACK");
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
                        play(id, color);
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
        System.out.print("create - Create a game\nlist - list games\njoin - join a game\nobserve - observe a game\nlogout - For when done, access to return to pre-login menu\nhelp - possible commands\n");
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
            System.out.print("GameID: " + game.gameID() + " White Player: " + whiteUser + " Black Player: " + blackUser +"\n");
        }
    }

    private static void play(int id, String color) {
        try {
            JoinGameRequest joinGameRequest = new JoinGameRequest(color, id);
            JoinGameResult joinGameResult = facade.joinGame(joinGameRequest, authToken);
            System.out.print("Joined game: initiating gameplay"+"\n");
            ChessGame game = getGame(id);
            assert game != null;
            printBoard(game, color);
        } catch (DataAccessException exception) {
            System.out.print("Failed to join game: try again"+"\n");
        }
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
            Integer.parseInt(info[0]);
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
        assert game != null;
        printBoard(game, "WHITE");
    }

    private static boolean observeCheck(String[] info) {
        if (info.length != 1) {
            return false;
        }
        try {
            Integer.parseInt(info[0]);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }
}
