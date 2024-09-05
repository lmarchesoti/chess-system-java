package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

    private ChessMatch chessMatch;

    public King(Board board, Color color, ChessMatch chessMatch) {
        super(board, color);
        this.chessMatch = chessMatch;
    }

    @Override
    public boolean[][] possibleMoves() {
        int rows = getBoard().getRows();
        int cols = getBoard().getColumns();
        boolean[][] moves = new boolean[rows][cols];

        possibleMoveAbove(moves);
        possibleMoveBelow(moves);
        possibleMoveLeft(moves);
        possibleMoveRight(moves);
        possibleMoveNW(moves);
        possibleMoveNE(moves);
        possibleMoveSW(moves);
        possibleMoveSE(moves);
        possibleMoveCastling(moves);

        return moves;
    }

    private void possibleMoveCastling(boolean[][] moves) {
        if (getMoveCount() == 0 && !chessMatch.getCheck()) {
            possibleMoveShortCastling(moves);
            possibleMoveLongCastling(moves);
        }
    }

    private void possibleMoveLongCastling(boolean[][] moves) {
        Position rookPosition = new Position(position.getRow(), position.getColumn() + 3);
        if (testRookCastling(rookPosition)) {
            Position p1 = new Position(position.getRow(), position.getColumn() + 1);
            Position p2 = new Position(position.getRow(), position.getColumn() + 2);
            if (!getBoard().thereIsAPiece(p1) && !getBoard().thereIsAPiece(p2)) {
                moves[position.getRow()][position.getColumn() + 2] = true;
            }
        }
    }

    private void possibleMoveShortCastling(boolean[][] moves) {
        Position rookPosition = new Position(position.getRow(), position.getColumn() - 4);
        if (testRookCastling(rookPosition)) {
            Position p1 = new Position(position.getRow(), position.getColumn() - 1);
            Position p2 = new Position(position.getRow(), position.getColumn() - 2);
            Position p3 = new Position(position.getRow(), position.getColumn() - 3);
            if (!getBoard().thereIsAPiece(p1) && !getBoard().thereIsAPiece(p2) && !getBoard().thereIsAPiece(p3)) {
                moves[position.getRow()][position.getColumn() - 2] = true;
            }
        }
    }

    private boolean testRookCastling(Position position) {
        ChessPiece p = (ChessPiece) getBoard().piece(position);
        return p instanceof Rook && p.getColor().equals(getColor()) && p.getMoveCount() == 0;
    }

    private void possibleMoveAbove(boolean[][] moves) {
        possibleMoveSpot(-1, 0, moves);
    }

    private void possibleMoveBelow(boolean[][] moves) {
        possibleMoveSpot(1, 0, moves);
    }

    private void possibleMoveLeft(boolean[][] moves) {
        possibleMoveSpot(0, -1, moves);
    }

    private void possibleMoveRight(boolean[][] moves) {
        possibleMoveSpot(0, 1, moves);
    }

    private void possibleMoveNW(boolean[][] moves) {
        possibleMoveSpot(-1, -1, moves);
    }

    private void possibleMoveNE(boolean[][] moves) {
        possibleMoveSpot(-1, 1, moves);
    }

    private void possibleMoveSW(boolean[][] moves) {
        possibleMoveSpot(1, -1, moves);
    }

    private void possibleMoveSE(boolean[][] moves) {
        possibleMoveSpot(1, 1, moves);
    }

    @Override
    public String toString() {
        return "K";
    }
}
