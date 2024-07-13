package chess.pieces;

import boardgame.Board;
import chess.ChessPiece;
import chess.Color;

public class Rook extends ChessPiece {
    public Rook(Board board, Color color) {
        super(board, color);
    }

    @Override
    public boolean[][] possibleMoves() {
        int rows = getBoard().getRows();
        int cols = getBoard().getColumns();
        boolean[][] moves = new boolean[rows][cols];

        possibleMovesAbove(moves);
        possibleMovesBelow(moves);
        possibleMovesLeft(moves);
        possibleMovesRight(moves);

        return moves;
    }

    @Override
    public String toString() {
        return "R";
    }
}
