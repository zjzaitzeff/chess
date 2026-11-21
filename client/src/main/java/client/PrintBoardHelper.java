package client;

import chess.*;
import ui.EscapeSequences;

import java.util.Collection;

public class PrintBoardHelper {


    public static void printBoard(ChessGame game, String color, ChessPosition chessPosition) {
        ChessBoard board = game.getBoard();
        Collection<ChessMove> validMoves;
        if (chessPosition != null) {
            validMoves = game.validMoves(chessPosition);
        } else {
            validMoves = null;
        }
        if (color.equals("WHITE")) {
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + "  A  B  C  D  E  F  G  H  \n");
            for (int i = 8; i > 0; i--) {
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + i);
                if (i % 2 == 0) {
                    printWhiteRowWhite(board, i, validMoves, chessPosition);
                } else {
                    printBlackRowWhite(board, i,  validMoves, chessPosition);
                }
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + i + "\n");
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + "  A  B  C  D  E  F  G  H  \n");
        } else {
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + "  H  G  F  E  D  C  B  A  \n");
            for (int i = 1; i < 9; i++) {
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + i);
                if (i % 2 == 0) {
                    printBlackRowBlack(board, i, validMoves, chessPosition);
                } else {
                    printWhiteRowBlack(board, i, validMoves, chessPosition);
                }
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + i + "\n");
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + "  H  G  F  E  D  C  B  A  \n");
        }
    }

    private static void printWhiteRowWhite(ChessBoard board, int r, Collection<ChessMove> validMoves, ChessPosition piecePosition) {
        for (int j = 1; j < 9; j++) {
            whiteRowHelper(board,r,j, validMoves, piecePosition);
        }
    }

    private static void printBlackRowWhite(ChessBoard board, int r, Collection<ChessMove> validMoves, ChessPosition piecePosition) {
        for (int j = 1; j < 9; j++) {
            blackRowHelper(board, r, j, validMoves, piecePosition);
        }
    }

    private static void printWhiteRowBlack(ChessBoard board, int r, Collection<ChessMove> validMoves, ChessPosition piecePosition) {
        for (int j = 8; j > 0; j --) {
            whiteRowHelper(board,r,j,validMoves, piecePosition);
        }
    }

    private static void printBlackRowBlack(ChessBoard board, int r, Collection<ChessMove> validMoves, ChessPosition piecePosition) {
        for (int j = 8; j > 0; j --) {
            blackRowHelper(board, r, j, validMoves, piecePosition);
        }
    }

    private static void blackRowHelper(ChessBoard board, int r, int j, Collection<ChessMove> validMoves, ChessPosition piecePosition) {
        ChessPosition chessPosition = new ChessPosition(r, j);
        if (j % 2 == 0) {
            printOddHelper(r,j,validMoves,piecePosition,board,chessPosition);
        } else {
            printEvenHelper(r,j,validMoves,piecePosition,board,chessPosition);
        }
    }

    private static void whiteRowHelper(ChessBoard board, int r, int j, Collection<ChessMove> validMoves, ChessPosition piecePosition) {
        ChessPosition chessPosition = new ChessPosition(r, j);
        if (j % 2 == 0) {
            printEvenHelper(r,j,validMoves,piecePosition,board,chessPosition);
        } else {
            printOddHelper(r,j,validMoves,piecePosition,board,chessPosition);
        }
    }

    private static void printEvenHelper(int r, int j, Collection<ChessMove> vm, ChessPosition pPos, ChessBoard b, ChessPosition cPos) {
        if (vm != null && checkIfInValidMoves(vm,cPos)) {
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN + getPieceStr(b, r, j));
        } else if (pPos != null && pPos.equals(cPos)) {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLUE + getPieceStr(b, r, j));
        } else {
            System.out.print(EscapeSequences.SET_BG_COLOR_RED + getPieceStr(b, r, j));
        }
    }

    private static void printOddHelper(int r, int j, Collection<ChessMove> vm, ChessPosition pPos, ChessBoard b, ChessPosition cPos) {
        if (vm != null && checkIfInValidMoves(vm,cPos)) {
            System.out.print(EscapeSequences.SET_BG_COLOR_GREEN + getPieceStr(b, r, j));
        } else if (pPos != null && pPos.equals(cPos)) {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLUE + getPieceStr(b, r, j));
        } else {
            System.out.print(EscapeSequences.SET_BG_COLOR_MAGENTA + getPieceStr(b, r, j));
        }
    }

    private static boolean checkIfInValidMoves(Collection<ChessMove> validMoves, ChessPosition chessPosition) {
        for (ChessMove move : validMoves) {
            if (chessPosition.equals(move.getEndPosition())) {
                return true;
            }
        }
        return false;
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
}
