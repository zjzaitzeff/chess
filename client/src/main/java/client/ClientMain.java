package client;
import java.util.Scanner;
import chess.*;
// prelogin UI
//Help	Displays text informing the user of available actions.
// Quit	Exits the program.
// Login	Prompts the user for login credentials and calls the server login API. Upon success, the client transitions to the Postlogin UI.
// Register	Prompts the user for registration information and calls the server register API. Upon success, the user is logged in and the client transitions to the Postlogin UI.

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        boolean loggedIn = false;
        String prefix = "[LOGGED OUT] >>> ";
        Scanner scanner = new Scanner(System.in);
        System.out.print("Welcome to 240 chess. Type help to get started!\n");
        preUI(prefix, loggedIn, scanner);
        prefix = "[LOGGED IN] >>> ";
        postUI();
        scanner.close();
    }

    private static void preUI(String prefix, boolean loggedIn, Scanner scanner) {
        while (!loggedIn) {
            System.out.print(prefix);
            String input = scanner.nextLine();
            switch (input) {
                case "quit" -> {
                    System.out.print("Quiting Game");
                    System.exit(0);
                }
                case "help" -> help();
                case "login" -> {
                    System.out.print("login format: <username> <password>\n");
                    String loginInput = scanner.nextLine();
                    String[] loginInfo = loginInput.trim().split("\\s+");
                    if (loginCheck(loginInfo)) {
                        String username = loginInfo[0];
                        String password = loginInfo[1];
                        login(username, password);
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

    private static void postUI() {
        //  Help	Displays text informing the user of available actions.
//  Logout	Logs out the user via the server API and transitions the client back to the Prelogin UI.
//  Create Game	Prompts the user for a game name and calls the server create API. This creates the game on the server but does not automatically join the user to it.
//  List Games	Lists all existing games on the server. Calls the server list API and displays the games in a numbered list, including the game name and current players (excluding observers). The list numbering must start at 1 and be independent of the internal game IDs.
//  Play Game	Allows the user to join a game by specifying the list number and their desired color. The client must map this list number back to the correct internal game ID. Calls the server join API.
//  Observe Game	Allows the user to specify a game to observe using the list number. The client must map this list number back to the correct internal game ID. (Full observer functionality will be added in Phase 6).

    }

    public static void help() {
        System.out.print("register - create a user\nlogin - to play chess\nquit - Stop playing chess\nhelp - review this message again\n");
    }

    public static void login(String username, String password) {
        System.out.print("You logged in\n");
        System.out.print(username);
        System.out.print(password);
    }

    private static boolean loginCheck(String[] info) {
        return info.length == 2;
    }

    public static void register(String username, String password, String email) {
        System.out.print("You registered\n");
        System.out.print(username);
        System.out.print(password);
        System.out.print(email);
    }

    private static boolean registerCheck(String[] info) {
        return info.length == 3;
    }
}
