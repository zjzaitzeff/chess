package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import model.ListGamesResult;
import ui.EscapeSequences;

import java.util.Collection;
import java.util.List;

import static java.lang.Integer.parseInt;

public class CheckerAndStringHelper {

    public static boolean loginCheck(String[] info) {
        return info.length == 2;
    }

    public static boolean registerCheck(String[] info) {
        return info.length == 3;
    }

    public static boolean createCheck(String[] info) {
        return info.length == 1;
    }

    public static void listHelper(ListGamesResult lgr) {
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
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_BLACK + "GameID: " + game.gameID());
            System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN + " Game Name: " + game.gameName());
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE +" White Player: " + whiteUser);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_YELLOW + " Black Player: " + blackUser);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_BLACK + " Game Status: ");
            System.out.print(getGameStatusString(game.chess()) + "\n");
        }
    }

    private static String getGameStatusString(ChessGame game) {
        if (game.isGameOver()) {
            return "Complete";
        } else {
            return "Not Complete";
        }
    }

    public static boolean determineIfGameComplete(ChessGame game) {
        if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            return true;
        } else if (game.isInStalemate(ChessGame.TeamColor.WHITE)) {
            return true;
        } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            return true;
        } else if (game.isInStalemate(ChessGame.TeamColor.BLACK)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean highlightCheck(String[] info) {
        Collection<String> approvedLetters = List.of("a", "b", "c", "d", "e", "f", "g", "h");
        Collection<String> approvedNumbers = List.of("1", "2", "3", "4", "5", "6", "7", "8");
        if (info.length != 1) {
            return false;
        }
        String position = info[0];
        if (position.length() != 2) {
            return false;
        }
        if (!approvedLetters.contains(String.valueOf(position.charAt(0)))) {
            return false;
        }
        if (!approvedNumbers.contains(String.valueOf(position.charAt(1)))) {
            return false;
        }
        return true;
    }

    public static ChessMove convertStringToChessMove(String[] input) {
        String pos1 = input[0];
        String pos2 = input[1];
        ChessPosition chessPosition1 = createChessPosition(pos1);
        ChessPosition chessPosition2 = createChessPosition(pos2);
        if (input.length == 3) {
            String promotion = input[2];
            ChessPiece.PieceType promotionType = generatePromotionType(promotion);
            return new ChessMove(chessPosition1, chessPosition2, promotionType);
        } else {
            return new ChessMove(chessPosition1, chessPosition2, null);
        }
    }

    public static ChessPosition createChessPosition(String pos) {
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
        return new ChessPosition(col,row);
    }

    private static ChessPiece.PieceType generatePromotionType(String promotion) {
        return switch (promotion) {
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "rook" -> ChessPiece.PieceType.ROOK;
            default -> null;
        };
    }

    public static boolean makeMoveCheck(String[] input) {
        Collection<String> approvedLetters = List.of("a", "b", "c", "d", "e", "f", "g", "h");
        Collection<String> approvedNumbers = List.of("1", "2", "3", "4", "5", "6", "7", "8");
        Collection<String> approvedPromotions = List.of("knight","bishop","queen","rook");
        if (input.length == 3) {
            String pos1 = input[0];
            String pos2 = input[1];
            String promotion = input[2];
            if (!makeMoveCheckPositions(pos1, pos2, approvedLetters, approvedNumbers)) {
                return false;
            }
            if (!approvedPromotions.contains(promotion)) {
                return false;
            }
            return true;
        } else if (input.length == 2) {
            String pos1 = input[0];
            String pos2 = input[1];
            return makeMoveCheckPositions(pos1, pos2, approvedLetters, approvedNumbers);
        } else {
            return false;
        }
    }

    private static boolean makeMoveCheckPositions(String pos1, String pos2, Collection<String> approvedLetters, Collection<String> approvedNumbers) {
        if (pos1.length() != 2) {
            return false;
        }
        if (pos2.length() != 2) {
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
        return true;
    }

    public static boolean joinCheck(String[] info) {
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

}
