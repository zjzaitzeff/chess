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
    PieceType typePiece;
    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        color = pieceColor;
        typePiece = type;
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
        return typePiece;
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
        return pawnMoves(board, myPosition,r,c,color);}
        // rook x
        else if (type.equals(PieceType.ROOK)) {
            return rookMoves(board, myPosition,r,c,color);
        }
        // knight
        else if (type.equals(PieceType.KNIGHT)) {
            return knightMoves(board, myPosition,r,c,color);
        }
        // bishop
        else if (type.equals(PieceType.BISHOP)) {
            return bishopMoves(board, myPosition,r,c,color);
        }
        // queen
        else if (type.equals(PieceType.QUEEN)) {
            return queenMoves(board, myPosition,r,c,color);
        }
        // king
        else if (type.equals(PieceType.KING)) {
            return kingMoves(board, myPosition,r,c,color);
        }
        throw new RuntimeException("Not implemented");
    }

    private void addPromotionMoves(Collection<ChessMove> validMoves, ChessPosition myPosition, ChessPosition targetPosition) {
        validMoves.add(new ChessMove(myPosition, targetPosition, ChessPiece.PieceType.QUEEN));
        validMoves.add(new ChessMove(myPosition, targetPosition, ChessPiece.PieceType.KNIGHT));
        validMoves.add(new ChessMove(myPosition, targetPosition, ChessPiece.PieceType.ROOK));
        validMoves.add(new ChessMove(myPosition, targetPosition, ChessPiece.PieceType.BISHOP));
    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, int r, int c, ChessGame.TeamColor color) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        // white
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            if (r+1 <= 8) {
                ChessPosition targetPosition = new ChessPosition(r+1, c);
                ChessPiece targetPiece = board.getPiece(targetPosition);
                if (targetPiece == null) {
                    if (r+1 == 8) {
                        addPromotionMoves(validMoves, myPosition, targetPosition);
                    } else {
                        validMoves.add(new ChessMove(myPosition, targetPosition, null));
                        if (r==2) {
                            ChessPosition targetPosition_two = new ChessPosition(r+2, c);
                            ChessPiece targetPiece_two = board.getPiece(targetPosition_two);
                            if (targetPiece_two == null) {
                                validMoves.add(new ChessMove(myPosition,targetPosition_two,null));
                            }
                        }
                    }
                }
            }
            if (r+1 <= 8 && c+1 <= 8) {
                ChessPosition targetPosition = new ChessPosition(r+1,c+1);
                ChessPiece targetPiece = board.getPiece(targetPosition);
                if (targetPiece != null) {
                    if (targetPiece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                        if (r+1 == 8) {
                            addPromotionMoves(validMoves, myPosition, targetPosition);
                        } else {
                            validMoves.add(new ChessMove(myPosition, targetPosition, null));
                    }
                }
            }
        }
            if (r+1 <= 8 && c-1 >= 1) {
                ChessPosition targetPosition = new ChessPosition(r+1,c-1);
                ChessPiece targetPiece = board.getPiece(targetPosition);
                if (targetPiece != null) {
                    if (targetPiece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                        if (r+1 == 8) {
                            addPromotionMoves(validMoves, myPosition, targetPosition);
                        } else {
                            validMoves.add(new ChessMove(myPosition, targetPosition, null));
                        }
                    }
                }
            }
        } // black
        else {
            if (r-1 >= 1) {
                ChessPosition targetPosition = new ChessPosition(r-1, c);
                ChessPiece targetPiece = board.getPiece(targetPosition);
                if (targetPiece == null) {
                    if (r-1 == 1) {
                        addPromotionMoves(validMoves, myPosition, targetPosition);
                    } else {
                        validMoves.add(new ChessMove(myPosition, targetPosition, null));
                        if (r==7) {
                            ChessPosition targetPosition_two = new ChessPosition(r-2, c);
                            ChessPiece targetPiece_two = board.getPiece(targetPosition_two);
                            if (targetPiece_two == null) {
                                validMoves.add(new ChessMove(myPosition,targetPosition_two,null));
                            }
                        }
                    }
                }
            }
            if (r-1 >= 1 && c+1 <= 8) {
                ChessPosition targetPosition = new ChessPosition(r-1,c+1);
                ChessPiece targetPiece = board.getPiece(targetPosition);
                if (targetPiece != null) {
                    if (targetPiece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                        if (r-1 == 1) {
                            addPromotionMoves(validMoves, myPosition, targetPosition);
                        } else {
                            validMoves.add(new ChessMove(myPosition, targetPosition, null));
                        }
                    }
                }
            }
            if (r-1 >= 1 && c-1 >= 1) {
                ChessPosition targetPosition = new ChessPosition(r-1,c-1);
                ChessPiece targetPiece = board.getPiece(targetPosition);
                if (targetPiece != null) {
                    if (targetPiece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                        if (r-1 == 1) {
                            addPromotionMoves(validMoves, myPosition, targetPosition);
                        } else {
                            validMoves.add(new ChessMove(myPosition, targetPosition, null));
                        }
                    }
                }
            }
        }
        return validMoves;
    }
    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, int r, int c, ChessGame.TeamColor color) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPosition targetPosition;
        // up
        for (int i = r+1; i < 9; i++) {
            targetPosition = new ChessPosition(i,c);
            ChessPiece what_is_at_location = board.getPiece(targetPosition);
            if (what_is_at_location == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (color.equals(what_is_at_location.getTeamColor())) {
                break;
            } else {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
                break;
            }
        }
        // down
        for (int i = r-1; i > 0; i--) {
            targetPosition = new ChessPosition(i,c);
            ChessPiece what_is_at_location = board.getPiece(targetPosition);
            if (what_is_at_location == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (color.equals(what_is_at_location.getTeamColor())) {
                break;
            } else {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
                break;
            }
        }
        // right
        for (int i = c + 1; i < 9; i++) {
            targetPosition = new ChessPosition(r,i);
            ChessPiece what_is_at_location = board.getPiece(targetPosition);
            if (what_is_at_location == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (color.equals(what_is_at_location.getTeamColor())) {
                break;
            } else {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
                break;
            }
        }
        // left
        for (int i = c - 1; i > 0; i--) {
            targetPosition = new ChessPosition(r,i);
            ChessPiece what_is_at_location = board.getPiece(targetPosition);
            if (what_is_at_location == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (color.equals(what_is_at_location.getTeamColor())) {
                break;
            } else {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
                break;
            }
        }
        return validMoves;
    }

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, int r, int c, ChessGame.TeamColor color) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (r+1 <= 8 && c+2 <=8) {
            ChessPosition targetPosition = new ChessPosition(r+1, c+2);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (targetPiece.getTeamColor() != color) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            }
        }
        if (r+1 <= 8 && c-2 >=1) {
            ChessPosition targetPosition = new ChessPosition(r+1, c-2);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (targetPiece.getTeamColor() != color) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            }
        }
        if (r+2 <= 8 && c+1 <=8) {
            ChessPosition targetPosition = new ChessPosition(r+2, c+1);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (targetPiece.getTeamColor() != color) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            }
        }
        if (r+2 <= 8 && c-1 >=1) {
            ChessPosition targetPosition = new ChessPosition(r+2, c-1);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (targetPiece.getTeamColor() != color) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            }
        }
        if (r-1 >= 1 && c+2 <=8) {
            ChessPosition targetPosition = new ChessPosition(r-1, c+2);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (targetPiece.getTeamColor() != color) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            }
        }
        if (r-1 >= 1 && c-2 >=1) {
            ChessPosition targetPosition = new ChessPosition(r-1, c-2);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (targetPiece.getTeamColor() != color) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            }
        }
        if (r-2 >= 1 && c+1 <=8) {
            ChessPosition targetPosition = new ChessPosition(r-2, c+1);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (targetPiece.getTeamColor() != color) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            }
        }
        if (r-2 >= 1 && c-1 >=1) {
            ChessPosition targetPosition = new ChessPosition(r-2, c-1);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (targetPiece.getTeamColor() != color) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            }
        }
        return validMoves;
    }
    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, int r, int c, ChessGame.TeamColor color) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        // up up
        for (int i = r + 1, j = c + 1; i <= 8 && j <= 8; i++, j++) {
                ChessPosition targetPosition = new ChessPosition(i,j);
                ChessPiece targetPiece = board.getPiece(targetPosition);
                if (targetPiece == null) {
                    validMoves.add(new ChessMove(myPosition, targetPosition, null));
                } else if (color.equals(targetPiece.getTeamColor())) {
                    break;
                } else {
                    validMoves.add(new ChessMove(myPosition, targetPosition, null));
                    break;
                }
            }
        // up down
        for (int i = r + 1, j = c - 1; i <= 8 && j >= 1; i++, j--) {
            ChessPosition targetPosition = new ChessPosition(i,j);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (color.equals(targetPiece.getTeamColor())) {
                break;
            } else {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
                break;
            }
        }
        // down up
        for (int i = r - 1, j = c + 1; i >= 1 && j <= 8; i--, j++) {
            ChessPosition targetPosition = new ChessPosition(i,j);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (color.equals(targetPiece.getTeamColor())) {
                break;
            } else {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
                break;
            }
        }
        // down down
        for (int i = r - 1, j = c - 1; i >= 1 && j >= 1; i--, j--) {
            ChessPosition targetPosition = new ChessPosition(i,j);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (color.equals(targetPiece.getTeamColor())) {
                break;
            } else {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
                break;
            }
        }
        return validMoves;
    }
    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition, int r, int c, ChessGame.TeamColor color) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> rook_moves = rookMoves(board, myPosition,r,c,color);
        validMoves.addAll(rook_moves);
        Collection<ChessMove> bishop_moves = bishopMoves(board,myPosition,r,c,color);
        validMoves.addAll(bishop_moves);
        return validMoves;
    }
    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition, int r, int c, ChessGame.TeamColor color) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (r+1 <= 8 && c+1 <= 8) {
            ChessPosition targetPosition = new ChessPosition(r+1, c+1);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (color != targetPiece.getTeamColor()) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            }
        }
        if (r+1 <= 8 && c-1 >= 1) {
            ChessPosition targetPosition = new ChessPosition(r+1, c-1);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (color != targetPiece.getTeamColor()) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            }
        }
        if (r-1 >= 1 && c+1 <= 8) {
            ChessPosition targetPosition = new ChessPosition(r-1, c+1);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (color != targetPiece.getTeamColor()) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            }
        }
        if (r-1 >= 1 && c-1 >= 1) {
            ChessPosition targetPosition = new ChessPosition(r-1, c-1);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (color != targetPiece.getTeamColor()) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            }
        }
        if (r+1 <= 8) {
            ChessPosition targetPosition = new ChessPosition(r+1, c);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (color != targetPiece.getTeamColor()) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            }
        }
        if (r-1 >= 1) {
            ChessPosition targetPosition = new ChessPosition(r-1, c);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (color != targetPiece.getTeamColor()) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            }
        }
        if (c+1 <= 8) {
            ChessPosition targetPosition = new ChessPosition(r, c+1);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (color != targetPiece.getTeamColor()) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            }
        }
        if (c-1 >= 1) {
            ChessPosition targetPosition = new ChessPosition(r, c-1);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            } else if (color != targetPiece.getTeamColor()) {
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
            }
        }
        return validMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && typePiece == that.typePiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, typePiece);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + color +
                ", type_piece=" + typePiece +
                '}';
    }
}
