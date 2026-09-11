package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor color;
    PieceType type_piece;
    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        color = pieceColor;
        type_piece = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type_piece;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
//        int r = myPosition.getRow();
//        int c = myPosition.getColumn();
//        PieceType type = board.getPiece(myPosition).getPieceType();
//        // pawn
//        if (type.equals(PieceType.PAWN)) {
//        return pawn_moves(board, myPosition);}
//        // rook
//        else if (type.equals(PieceType.ROOK)) {
//            return rook_moves(board, myPosition);
//        }
//        // knight
//        else if (type.equals(PieceType.KNIGHT)) {
//            return rook_moves(board, myPosition);
//        }
//        // bishop
//        else if (type.equals(PieceType.BISHOP)) {
//            return rook_moves(board, myPosition);
//        }
//        // queen
//        else if (type.equals(PieceType.QUEEN)) {
//            return rook_moves(board, myPosition);
//        }
//        // king
//        else if (type.equals(PieceType.KING)) {
//            return rook_moves(board, myPosition);
//        }
        throw new RuntimeException("Not implemented");
    }

//    public Collection<ChessMove> pawn_moves(ChessBoard board, ChessPosition myPosition) {
//        Collection<ChessMove> valid_moves = new ArrayList<>();
//        return valid_moves;
//    }
//    public Collection<ChessMove> rook_moves(ChessBoard board, ChessPosition myPosition) {
//        Collection<ChessMove> valid_moves;
//
//        return valid_moves;
//    }
//    public Collection<ChessMove> knight_moves(ChessBoard board, ChessPosition myPosition) {
//        Collection<ChessMove> valid_moves;
//
//        return valid_moves;
//    }
//    public Collection<ChessMove> bishop_moves(ChessBoard board, ChessPosition myPosition) {
//        Collection<ChessMove> valid_moves;
//
//        return valid_moves;
//    }
//    public Collection<ChessMove> queen_moves(ChessBoard board, ChessPosition myPosition) {
//        Collection<ChessMove> valid_moves;
//
//        return valid_moves;
//    }
//    public Collection<ChessMove> king_moves(ChessBoard board, ChessPosition myPosition) {
//        Collection<ChessMove> valid_moves;
//
//        return valid_moves;
//    }
}
