package chess.pieces;

import boardgame.Board;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {
    public King(Board board, Color color) {
        super(board, color);
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

        return moves;
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
