package chess.pieces;

import boardgame.Board;
import boardgame.Position;
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

    private void possibleMovesAbove(boolean[][] moves) {
        possibleMovesInDirection(-1, 0, moves);
    }

    private void possibleMovesBelow(boolean[][] moves) {
        possibleMovesInDirection(1, 0, moves);
    }

    private void possibleMovesLeft(boolean[][] moves) {
        possibleMovesInDirection(0, -1, moves);
    }

    private void possibleMovesRight(boolean[][] moves) {
        possibleMovesInDirection(0, 1, moves);
    }

    private void possibleMovesInDirection(int rowStep, int colStep, boolean[][] moves) {
        Position p = new Position();
        p.setValues(position.getRow() + rowStep, position.getColumn() + colStep);
        while (getBoard().positionExists(p) && (!getBoard().thereIsAPiece(p) || isThereOpponentPiece(p))) {
            moves[p.getRow()][p.getColumn()] = true;
            p.setValues(p.getRow() + rowStep, p.getColumn() + colStep);
        }
    }

    @Override
    public String toString() {
        return "R";
    }
}
