package chess;

import java.util.ArrayList;
import java.util.Collection;

import java.util.Objects;

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
        int r = myPosition.getRow();
        int c = myPosition.getColumn();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        PieceType type = board.getPiece(myPosition).getPieceType();
        // pawn
        if (type.equals(PieceType.PAWN)) {
        return pawn_moves(board, myPosition,r,c,color);}
        // rook x
        else if (type.equals(PieceType.ROOK)) {
            return rook_moves(board, myPosition,r,c,color);
        }
        // knight
        else if (type.equals(PieceType.KNIGHT)) {
            return knight_moves(board, myPosition,r,c,color);
        }
        // bishop
        else if (type.equals(PieceType.BISHOP)) {
            return bishop_moves(board, myPosition,r,c,color);
        }
        // queen
        else if (type.equals(PieceType.QUEEN)) {
            return queen_moves(board, myPosition,r,c,color);
        }
        // king
        else if (type.equals(PieceType.KING)) {
            return king_moves(board, myPosition,r,c,color);
        }
        throw new RuntimeException("Not implemented");
    }

    public Collection<ChessMove> pawn_moves(ChessBoard board, ChessPosition myPosition, int r, int c, ChessGame.TeamColor color) {
        Collection<ChessMove> valid_moves = new ArrayList<>();
        // white
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            if (r+1 <= 8) {
                ChessPosition target_position = new ChessPosition(r+1, c);
                ChessPiece target_piece = board.getPiece(target_position);
                if (target_piece == null) {
                    if (r+1 == 8) {
                        valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.QUEEN));
                        valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.KNIGHT));
                        valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.ROOK));
                        valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.BISHOP));
                    } else {
                        valid_moves.add(new ChessMove(myPosition, target_position, null));
                        if (r==2) {
                            ChessPosition target_position_two = new ChessPosition(r+2, c);
                            ChessPiece target_piece_two = board.getPiece(target_position_two);
                            if (target_piece_two == null) {
                                valid_moves.add(new ChessMove(myPosition,target_position_two,null));
                            }
                        }
                    }
                }
            }
            if (r+1 <= 8 && c+1 <= 8) {
                ChessPosition target_position = new ChessPosition(r+1,c+1);
                ChessPiece target_piece = board.getPiece(target_position);
                if (target_piece != null) {
                    if (target_piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                        if (r+1 == 8) {
                            valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.QUEEN));
                            valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.KNIGHT));
                            valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.ROOK));
                            valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.BISHOP));
                        } else {
                            valid_moves.add(new ChessMove(myPosition, target_position, null));
                    }
                }
            }
        }
            if (r+1 <= 8 && c-1 >= 1) {
                ChessPosition target_position = new ChessPosition(r+1,c-1);
                ChessPiece target_piece = board.getPiece(target_position);
                if (target_piece != null) {
                    if (target_piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                        if (r+1 == 8) {
                            valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.QUEEN));
                            valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.KNIGHT));
                            valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.ROOK));
                            valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.BISHOP));
                        } else {
                            valid_moves.add(new ChessMove(myPosition, target_position, null));
                        }
                    }
                }
            }
        } // black
        else {
            if (r-1 >= 1) {
                ChessPosition target_position = new ChessPosition(r-1, c);
                ChessPiece target_piece = board.getPiece(target_position);
                if (target_piece == null) {
                    if (r-1 == 1) {
                        valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.QUEEN));
                        valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.KNIGHT));
                        valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.ROOK));
                        valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.BISHOP));
                    } else {
                        valid_moves.add(new ChessMove(myPosition, target_position, null));
                        if (r==7) {
                            ChessPosition target_position_two = new ChessPosition(r-2, c);
                            ChessPiece target_piece_two = board.getPiece(target_position_two);
                            if (target_piece_two == null) {
                                valid_moves.add(new ChessMove(myPosition,target_position_two,null));
                            }
                        }
                    }
                }
            }
            if (r-1 >= 1 && c+1 <= 8) {
                ChessPosition target_position = new ChessPosition(r-1,c+1);
                ChessPiece target_piece = board.getPiece(target_position);
                if (target_piece != null) {
                    if (target_piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                        if (r-1 == 1) {
                            valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.QUEEN));
                            valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.KNIGHT));
                            valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.ROOK));
                            valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.BISHOP));
                        } else {
                            valid_moves.add(new ChessMove(myPosition, target_position, null));
                        }
                    }
                }
            }
            if (r-1 >= 1 && c-1 >= 1) {
                ChessPosition target_position = new ChessPosition(r-1,c-1);
                ChessPiece target_piece = board.getPiece(target_position);
                if (target_piece != null) {
                    if (target_piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                        if (r-1 == 1) {
                            valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.QUEEN));
                            valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.KNIGHT));
                            valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.ROOK));
                            valid_moves.add(new ChessMove(myPosition, target_position, ChessPiece.PieceType.BISHOP));
                        } else {
                            valid_moves.add(new ChessMove(myPosition, target_position, null));
                        }
                    }
                }
            }
        }
        return valid_moves;
    }
    public Collection<ChessMove> rook_moves(ChessBoard board, ChessPosition myPosition, int r, int c, ChessGame.TeamColor color) {
        Collection<ChessMove> valid_moves = new ArrayList<>();
        ChessPosition target_position;
        // up
        for (int i = r+1; i < 9; i++) {
            target_position = new ChessPosition(i,c);
            ChessPiece what_is_at_location = board.getPiece(target_position);
            if (what_is_at_location == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (color.equals(what_is_at_location.getTeamColor())) {
                break;
            } else {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
                break;
            }
        }
        // down
        for (int i = r-1; i > 0; i--) {
            target_position = new ChessPosition(i,c);
            ChessPiece what_is_at_location = board.getPiece(target_position);
            if (what_is_at_location == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (color.equals(what_is_at_location.getTeamColor())) {
                break;
            } else {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
                break;
            }
        }
        // right
        for (int i = c + 1; i < 9; i++) {
            target_position = new ChessPosition(r,i);
            ChessPiece what_is_at_location = board.getPiece(target_position);
            if (what_is_at_location == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (color.equals(what_is_at_location.getTeamColor())) {
                break;
            } else {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
                break;
            }
        }
        // left
        for (int i = c - 1; i > 0; i--) {
            target_position = new ChessPosition(r,i);
            ChessPiece what_is_at_location = board.getPiece(target_position);
            if (what_is_at_location == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (color.equals(what_is_at_location.getTeamColor())) {
                break;
            } else {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
                break;
            }
        }
        return valid_moves;
    }

    public Collection<ChessMove> knight_moves(ChessBoard board, ChessPosition myPosition, int r, int c, ChessGame.TeamColor color) {
        Collection<ChessMove> valid_moves = new ArrayList<>();
        if (r+1 <= 8 && c+2 <=8) {
            ChessPosition target_position = new ChessPosition(r+1, c+2);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (target_piece.getTeamColor() != color) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            }
        }
        if (r+1 <= 8 && c-2 >=1) {
            ChessPosition target_position = new ChessPosition(r+1, c-2);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (target_piece.getTeamColor() != color) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            }
        }
        if (r+2 <= 8 && c+1 <=8) {
            ChessPosition target_position = new ChessPosition(r+2, c+1);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (target_piece.getTeamColor() != color) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            }
        }
        if (r+2 <= 8 && c-1 >=1) {
            ChessPosition target_position = new ChessPosition(r+2, c-1);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (target_piece.getTeamColor() != color) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            }
        }
        if (r-1 >= 1 && c+2 <=8) {
            ChessPosition target_position = new ChessPosition(r-1, c+2);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (target_piece.getTeamColor() != color) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            }
        }
        if (r-1 >= 1 && c-2 >=1) {
            ChessPosition target_position = new ChessPosition(r-1, c-2);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (target_piece.getTeamColor() != color) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            }
        }
        if (r-2 >= 1 && c+1 <=8) {
            ChessPosition target_position = new ChessPosition(r-2, c+1);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (target_piece.getTeamColor() != color) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            }
        }
        if (r-2 >= 1 && c-1 >=1) {
            ChessPosition target_position = new ChessPosition(r-2, c-1);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (target_piece.getTeamColor() != color) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            }
        }
        return valid_moves;
    }
    public Collection<ChessMove> bishop_moves(ChessBoard board, ChessPosition myPosition, int r, int c, ChessGame.TeamColor color) {
        Collection<ChessMove> valid_moves = new ArrayList<>();
        // up up
        for (int i = r + 1, j = c + 1; i <= 8 && j <= 8; i++, j++) {
                ChessPosition target_position = new ChessPosition(i,j);
                ChessPiece target_piece = board.getPiece(target_position);
                if (target_piece == null) {
                    valid_moves.add(new ChessMove(myPosition, target_position, null));
                } else if (color.equals(target_piece.getTeamColor())) {
                    break;
                } else {
                    valid_moves.add(new ChessMove(myPosition, target_position, null));
                    break;
                }
            }
        // up down
        for (int i = r + 1, j = c - 1; i <= 8 && j >= 1; i++, j--) {
            ChessPosition target_position = new ChessPosition(i,j);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (color.equals(target_piece.getTeamColor())) {
                break;
            } else {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
                break;
            }
        }
        // down up
        for (int i = r - 1, j = c + 1; i >= 1 && j <= 8; i--, j++) {
            ChessPosition target_position = new ChessPosition(i,j);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (color.equals(target_piece.getTeamColor())) {
                break;
            } else {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
                break;
            }
        }
        // down down
        for (int i = r - 1, j = c - 1; i >= 1 && j >= 1; i--, j--) {
            ChessPosition target_position = new ChessPosition(i,j);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (color.equals(target_piece.getTeamColor())) {
                break;
            } else {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
                break;
            }
        }
        return valid_moves;
    }
    public Collection<ChessMove> queen_moves(ChessBoard board, ChessPosition myPosition, int r, int c, ChessGame.TeamColor color) {
        Collection<ChessMove> valid_moves = new ArrayList<>();
        Collection<ChessMove> rook_moves = rook_moves(board, myPosition,r,c,color);
        valid_moves.addAll(rook_moves);
        Collection<ChessMove> bishop_moves = bishop_moves(board,myPosition,r,c,color);
        valid_moves.addAll(bishop_moves);
        return valid_moves;
    }
    public Collection<ChessMove> king_moves(ChessBoard board, ChessPosition myPosition, int r, int c, ChessGame.TeamColor color) {
        Collection<ChessMove> valid_moves = new ArrayList<>();
        if (r+1 <= 8 && c+1 <= 8) {
            ChessPosition target_position = new ChessPosition(r+1, c+1);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (color != target_piece.getTeamColor()) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            }
        }
        if (r+1 <= 8 && c-1 >= 1) {
            ChessPosition target_position = new ChessPosition(r+1, c-1);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (color != target_piece.getTeamColor()) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            }
        }
        if (r-1 >= 1 && c+1 <= 8) {
            ChessPosition target_position = new ChessPosition(r-1, c+1);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (color != target_piece.getTeamColor()) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            }
        }
        if (r-1 >= 1 && c-1 >= 1) {
            ChessPosition target_position = new ChessPosition(r-1, c-1);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (color != target_piece.getTeamColor()) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            }
        }
        if (r+1 <= 8) {
            ChessPosition target_position = new ChessPosition(r+1, c);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (color != target_piece.getTeamColor()) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            }
        }
        if (r-1 >= 1) {
            ChessPosition target_position = new ChessPosition(r-1, c);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (color != target_piece.getTeamColor()) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            }
        }
        if (c+1 <= 8) {
            ChessPosition target_position = new ChessPosition(r, c+1);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (color != target_piece.getTeamColor()) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            }
        }
        if (c-1 >= 1) {
            ChessPosition target_position = new ChessPosition(r, c-1);
            ChessPiece target_piece = board.getPiece(target_position);
            if (target_piece == null) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            } else if (color != target_piece.getTeamColor()) {
                valid_moves.add(new ChessMove(myPosition, target_position, null));
            }
        }
        return valid_moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type_piece == that.type_piece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type_piece);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + color +
                ", type_piece=" + type_piece +
                '}';
    }
}
