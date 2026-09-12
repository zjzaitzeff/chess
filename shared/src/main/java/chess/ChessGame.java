package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor whose_turn;
    ChessBoard the_board;
    public ChessGame() {
        the_board = new ChessBoard();
        whose_turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return whose_turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        whose_turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece p = the_board.getPiece(startPosition);
        if (p == null) {
            return null;
        }
        Collection<ChessMove> possible_moves = p.pieceMoves(the_board, startPosition);
        Collection<ChessMove> valid_moves = new ArrayList<>();
        for (ChessMove move : possible_moves) {
            ChessBoard dummy_board = the_board.copy_board();
            the_board.addPiece(move.getEndPosition(), the_board.getPiece(startPosition));
            the_board.addPiece(move.getStartPosition(), null);
            if (!isInCheck(p.getTeamColor())) {
                valid_moves.add(move);
            }
            the_board = dummy_board.copy_board();
        }
        return valid_moves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition sp = move.getStartPosition();
        ChessPosition ep = move.getEndPosition();
        Collection<ChessMove> happy_go_lucky = validMoves(sp);
        if (happy_go_lucky.contains(move)) {
            the_board.addPiece(ep, the_board.getPiece(sp));
            the_board.addPiece(sp, null);
        } else {
            throw new InvalidMoveException("Invalid move" + move);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition king_position = null;
        for (int i=1;i<9;i++) {
            for (int j=1; j<9;j++) {
                ChessPosition current_position = new ChessPosition(i,j);
                ChessPiece p = the_board.getPiece(current_position);
                if (p != null && p.getPieceType() == ChessPiece.PieceType.KING && p.getTeamColor() == teamColor) {
                    king_position = current_position;
                }
            }
        }
        for (int i=1;i<9;i++) {
            for (int j=1;j<9;j++) {
                ChessPosition current_position = new ChessPosition(i,j);
                ChessPiece p = the_board.getPiece(current_position);
                if (!teamColor.equals(p.getTeamColor())) {
                    Collection<ChessMove> moves = p.pieceMoves(the_board, current_position);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(king_position)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    ChessPosition current_position = new ChessPosition(i, j);
                    ChessPiece p = the_board.getPiece(current_position);
                    if (teamColor.equals(p.getTeamColor())) {
                        Collection<ChessMove> moves = validMoves(current_position);
                        for (ChessMove move : moves) {
                            ChessBoard old_board = the_board.copy_board();
                            ChessPosition sp = move.getStartPosition();
                            ChessPosition ep = move.getEndPosition();
                            Collection<ChessMove> valid_moves = validMoves(sp);
                            if (valid_moves.contains(move)) {
                                the_board.addPiece(ep, the_board.getPiece(sp));
                                the_board.addPiece(sp, null);
                            if (!isInCheck(teamColor)) {
                                the_board = old_board.copy_board();
                                return false;
                            }
                            the_board = old_board.copy_board();
                        }
                    }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    ChessPosition current_position = new ChessPosition(i, j);
                    ChessPiece p = the_board.getPiece(current_position);
                    if (teamColor.equals(p.getTeamColor())) {
                        Collection<ChessMove> moves = validMoves(current_position);
                        for (ChessMove move : moves) {
                            ChessBoard old_board = the_board.copy_board();
                            ChessPosition sp = move.getStartPosition();
                            ChessPosition ep = move.getEndPosition();
                            Collection<ChessMove> valid_moves = validMoves(sp);
                            if (valid_moves.contains(move)) {
                                the_board.addPiece(ep, the_board.getPiece(sp));
                                the_board.addPiece(sp, null);
                                if (!isInCheck(teamColor)) {
                                    the_board = old_board.copy_board();
                                    return false;
                                }
                            }
                            the_board = old_board.copy_board();
                        }
                    }
                }
            }
        }
    return true;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        the_board = board.copy_board();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return the_board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return whose_turn == chessGame.whose_turn && Objects.equals(the_board, chessGame.the_board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(whose_turn, the_board);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "whose_turn=" + whose_turn +
                ", the_board=" + the_board +
                '}';
    }
}
