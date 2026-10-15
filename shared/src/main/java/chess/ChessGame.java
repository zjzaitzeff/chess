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
    TeamColor whoseTurn;
    ChessBoard theBoard;
    public ChessGame() {
        theBoard = new ChessBoard();
        theBoard.resetBoard();
        whoseTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return whoseTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        whoseTurn = team;
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
        Collection<ChessMove> vm = new ArrayList<>();
        ChessPiece p = theBoard.getPiece((startPosition));
        if (p == null) {
            return null;
        } else {
            Collection<ChessMove> p_moves = p.pieceMoves(theBoard, startPosition);
            for (ChessMove m : p_moves) {
                ChessPiece target_piece = theBoard.getPiece(m.getEndPosition());
                if (target_piece == null) {
                    validMovesHelper(target_piece, m, p, vm);
                } else if (!target_piece.getPieceType().equals(ChessPiece.PieceType.KING)) {
                    validMovesHelper(target_piece, m, p, vm);
                }
            }
        }
        return vm;
    }
    private void validMovesHelper(ChessPiece targetPiece, ChessMove m, ChessPiece p, Collection<ChessMove> vm) {
        ChessPosition sp = m.getStartPosition();
        ChessPosition ep = m.getEndPosition();
        theBoard.addPiece(ep, p);
        theBoard.addPiece(sp, null);
        if (!isInCheck(p.getTeamColor())) {
            vm.add(m);
        }
        theBoard.addPiece(sp,p);
        theBoard.addPiece(ep,targetPiece);
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
        ChessPiece.PieceType promotion = move.getPromotionPiece();
        if (theBoard.getPiece(sp) == null) {
            throw new InvalidMoveException("Invalid move");
        }
        if (!whoseTurn.equals(theBoard.getPiece(sp).getTeamColor())) {
            throw new InvalidMoveException("Invalid move, not your turn");
        }
        Collection<ChessMove> valid_moves = validMoves(sp);
        if (valid_moves == null) {
            throw new InvalidMoveException("No piece at that start location");
        }
        if (valid_moves.contains(move)) {
            if (promotion != null) {
                theBoard.addPiece(ep, new ChessPiece(theBoard.getPiece(sp).getTeamColor(), promotion));
                theBoard.addPiece(sp, null);
            } else {
                theBoard.addPiece(ep, theBoard.getPiece(sp));
                theBoard.addPiece(sp,null);
            }
        } else {
            throw new InvalidMoveException("Invalid move" + move);
        }

        //change turn
        if (whoseTurn.equals(TeamColor.WHITE)) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
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
                ChessPiece p = theBoard.getPiece(current_position);
                if (p != null && p.getPieceType() == ChessPiece.PieceType.KING && p.getTeamColor() == teamColor) {
                    king_position = current_position;
                }
            }
        }
        for (int i=1;i<9;i++) {
            for (int j=1;j<9;j++) {
                ChessPosition current_position = new ChessPosition(i, j);
                ChessPiece p = theBoard.getPiece(current_position);
                if (p != null && (!teamColor.equals(p.getTeamColor()))) {
                    if (isInCheckHelper(p, current_position, king_position)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean isInCheckHelper(ChessPiece p, ChessPosition current_position, ChessPosition king_position) {
        Collection<ChessMove> moves = p.pieceMoves(theBoard, current_position);
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(king_position)) {
                return true;
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
        if (!isInCheck(teamColor)) {
            return false;
        }
        return stalemateAndCheckmateHelper(teamColor);
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
            return false;
        }
        return stalemateAndCheckmateHelper(teamColor);
    }
    private boolean stalemateAndCheckmateHelper(TeamColor teamColor) {
        for (int i=1; i < 9; i++) {
            for (int j=1; j < 9; j++) {
                ChessPosition current_position = new ChessPosition(i,j);
                ChessPiece p = theBoard.getPiece(current_position);
                if (p!=null) {
                    if (p.getTeamColor().equals(teamColor)) {
                        Collection<ChessMove> vm = validMoves(current_position);
                        if (vm != null) {
                            if (!vm.isEmpty()) {
                                return false;
                            }
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
        theBoard = board.copyBoard();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return theBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return whoseTurn == chessGame.whoseTurn && Objects.equals(theBoard, chessGame.theBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(whoseTurn, theBoard);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "whoseTurn=" + whoseTurn +
                ", theBoard=" + theBoard +
                '}';
    }
}
