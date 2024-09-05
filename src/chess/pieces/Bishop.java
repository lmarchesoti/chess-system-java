package chess.pieces;

import boardgame.Board;
import chess.ChessPiece;
import chess.Color;

public class Bishop extends ChessPiece {
    public Bishop(Board board, Color color) {
        super(board, color);
    }

    @Override
    public boolean[][] possibleMoves() {
        int rows = getBoard().getRows();
        int cols = getBoard().getColumns();
        boolean[][] moves = new boolean[rows][cols];

        possibleMovesDiagonalSE(moves);
        possibleMovesDiagonalNE(moves);
        possibleMovesDiagonalSW(moves);
        possibleMovesDiagonalNW(moves);

        return moves;
    }

    @Override
    public String toString() {
        return "B";
    }
}
