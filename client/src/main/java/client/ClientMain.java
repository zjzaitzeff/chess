package client;

public class ClientMain {
    public static void main(String[] args) {
        String serverURL = "http://localhost:8080";
        if (args.length == 1) {
            serverURL = args[0];
        }
        try {
            new ChessClient(serverURL).run();
        } catch(Throwable exception) {
            System.out.print("Unable to start server: %s%n");
            throw exception;
        }
    }
}
