package chess.pieces;

import boardgame.Board;
import chess.ChessPiece;
import chess.Color;

public class Knight extends ChessPiece {
    public Knight(Board board, Color color) {
        super(board, color);
    }

    @Override
    public boolean[][] possibleMoves() {
        int rows = getBoard().getRows();
        int cols = getBoard().getColumns();
        boolean[][] moves = new boolean[rows][cols];

        possibleMoveSpot(-1, -2, moves);
        possibleMoveSpot(-2, -1, moves);
        possibleMoveSpot(-1, +2, moves);
        possibleMoveSpot(-2, +1, moves);
        possibleMoveSpot(+1, -2, moves);
        possibleMoveSpot(+2, -1, moves);
        possibleMoveSpot(+1, +2, moves);
        possibleMoveSpot(+2, +1, moves);

        return moves;
    }

    @Override
    public String toString() {
        return "N";
    }
}
