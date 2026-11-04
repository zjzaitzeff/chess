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
        boolean registered = false;
        boolean loggedIn = false;
        String prefix = "[LOGGED OUT] >>> ";
        String input;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Welcome to 240 chess. Type Help to get started!\n");
        while (!registered) {
            System.out.print(prefix);
            input = scanner.nextLine();
            if (input.equals("quit")) {
                System.out.print("Quiting Game");
                System.exit(0);
            } else if (input.equals("help")) {
                help();
            } else if (input.equals("login")) {
                System.out.print("login format: <username> <password>");
                String loginInput = scanner.nextLine();
                String[] loginInfo = loginInput.trim().split("\\s+");
                if (loginCheck(loginInfo)) {
                    String username = loginInfo[0];
                    String password = loginInfo[1];
                    login(username, password);
                    loggedIn = true;
                    prefix = "[LOGGED IN] >>> ";
                } else {
                    System.out.print("Incorrect input: <username> <password>\nIf you want to login please type login");
                }
            } else if (input.equals("register")) {
                System.out.print("register\n");
                String registerInput = scanner.nextLine();
                String[] registerInfo = registerInput.trim().split("\\s+");
                if (registerCheck(registerInfo)) {
                    String username = registerInfo[0];
                    String password = registerInfo[1];
                    String email = registerInfo[2];
                    register(username, password, email);
                    registered = true;
                } else {
                    System.out.print("Incorrect input: <username> <password> <email>");
                }
            } else {
                System.out.print("incorrect input, please use command Help to see correct inputs\n");
            }

        }
        scanner.close();
    }

    public static void help() {
        System.out.print("register - create a user.\nlogin - to play chess.\n quit - Stop playing chess.\nhelp - review this message again.\n");
    }

    public static void login(String username, String password) {
        System.out.print("login\n");
    }

    private static boolean loginCheck(String[] info) {
        return info.length == 2;
    }

    public static void register(String username, String password, String email) {
        System.out.print("register\n");
    }

    private static boolean registerCheck(String[] info) {
        return info.length == 3;
    }
}
