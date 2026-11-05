package client;

import chess.ChessGame;
import chess.ChessPiece;
import service.*;

import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class ChessClient {
    private static String authToken = null;
    private static ServerFacade facade;

    public ChessClient(String serverURL) {
        facade = new ServerFacade(serverURL);
    }

    public void run() {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        boolean loggedIn = false;
        boolean notDoneWithProgram = true;
        String prefix = "[LOGGED OUT] >>> ";
        Scanner scanner = new Scanner(System.in);
        System.out.print("Welcome to 240 chess. Type help to get started!\n");
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
        System.exit(0);
    }

    private static void preUI(String prefix, boolean loggedIn, Scanner scanner) {
        while (!loggedIn) {
            System.out.print(prefix);
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
                        String username = loginInfo[0];
                        String password = loginInfo[1];
                        login(username, password);
                        System.out.print("Logged in as: " + username + "\n");
                        loggedIn = true;
                    } else {
                        System.out.print("Incorrect input: <username> <password>\nIf you want to login please type login\n");
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

    public static void helpPre() {
        System.out.print("register - create a user\nlogin - to play chess\nquit - Stop playing chess\nhelp - review this message again\n");
    }

    public static void login(String username, String password) {
        //add login API to server
        try {
            LoginRequest loginRequest = new LoginRequest(username, password);
            LoginResult loginResult = facade.login(loginRequest);
            authToken = loginResult.authToken();
            System.out.print("Successfully logged in: " + loginResult.username());
        } catch (DataAccessException exception) {
            System.out.print("Login failed :(");
        }
    }

    private static boolean loginCheck(String[] info) {
        return info.length == 2;
    }

    public static void register(String username, String password, String email) {
        try {
            UserService.RegisterRequest registerRequest = new UserService.RegisterRequest(username, password, email);
            UserService.RegisterResult registerResult = facade.register(registerRequest);
            authToken = registerResult.authToken();
            System.out.print("Registered: " + registerResult.username());
        } catch (DataAccessException exception) {
            System.out.print("Registration failed, try again");
        }
    }

    private static boolean registerCheck(String[] info) {
        return info.length == 3;
    }

    private static void postUI(String prefix, Scanner scanner, boolean loggedIn) {
        while (loggedIn) {
            System.out.print(prefix);
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
                        int ID = parseInt(strID);
                        String color = joinInfo[1];
                        play(ID, color);
                    } else {
                        System.out.print("Join format incorrect: <ID> [WHITE|BLACK]\n");
                    }
                }
                case "observe" -> {
                    System.out.print("observe format: <ID>\n");
                    String observeInput = scanner.nextLine();
                    String[] observeInfo = observeInput.trim().split("\\s+");
                    if (observeCheck(observeInfo)) {
                        String strID = observeInfo[0];
                        int ID = parseInt(strID);
                        observe(ID);
                    } else {
                        System.out.print("Observe format incorrect: <ID>\n");
                    }
                }
                default -> System.out.print("Incorrect input, please use command Help to see correct inputs\n");
            }
        }
    }

    private static void helpPost() {
        System.out.print("create - Create a game\nlist - list games\njoin - join a game\nobserve - observe a game\nlogout - For when done, access to return to pre-logout menu\nhelp - possible commands\n");
    }

    private static void logout() {
        //authToken input
    }

    private static void create(String gameName) {
        // authToken input
    }

    private static boolean createCheck(String[] info) {
        return info.length == 1;
    }

    private static void list() {
        // auth token input
    }

    private static void play(int ID, String color) {
        // this is the join
        //needs an authoToken

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
        if (!info[1].equals("WHITE") && !info[1].equals("BLACK")) {
            return false;
        }
        return true;
    }

    private static void observe(int ID) {

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
