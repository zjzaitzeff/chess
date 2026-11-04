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
        String input =" ";
        if (!registered) {
            System.out.print("Welcome to 240 chess. Type Help to get started!\n");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print(prefix);
                input = scanner.nextLine();
                if (input.equals("quit")) {
                    System.out.print("Quiting Game");
                    System.exit(0);
                } else if (input.equals("help")) {
                    help();
                } else if (input.equals("login")) {
                    System.out.print("login\n");
                    //login(username, password);
                } else if (input.equals("register")) {
                    System.out.print("register\n");
                    //register();
                } else {
                    System.out.print("incorrect input, please use command Help to see correct inputs\n");
                }
            }
        }
    }

    public static void help() {
        System.out.print("register - create a user.\nlogin - to play chess.\n quit - Stop playing chess.\nhelp - review this message again.\n");
    }

    public static void login(String username, String password) {
        System.out.print("login\n");
    }

    public static void register(String username, String password, String email) {
        System.out.print("register\n");
    }
}
