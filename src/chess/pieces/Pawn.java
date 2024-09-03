package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

    public Pawn(Board board, Color color) {
        super(board, color);
    }

    @Override
    public boolean[][] possibleMoves() {
        int rows = getBoard().getRows();
        int cols = getBoard().getColumns();
        boolean[][] moves = new boolean[rows][cols];

        int moveDirection = Color.WHITE.equals(getColor()) ? -1 : 1;

        possibleMovesForward(moveDirection, moves);
        possibleMovesCapture(moveDirection, moves);

        return moves;
    }

    private void possibleMovesCapture(int moveDirection, boolean[][] moves) {
        Position p = new Position(0, 0);
        p.setValues(position.getRow() + moveDirection, position.getColumn() - 1);

        if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;
        }

        p.setValues(position.getRow() + moveDirection, position.getColumn() + 1);

        if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;
        }
    }

    private void possibleMovesForward(int moveDirection, boolean[][] moves) {
        Position p = new Position(0, 0);

        p.setValues(position.getRow() + moveDirection, position.getColumn());

        if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;

            p.setValues(p.getRow() + moveDirection, p.getColumn());
            if (getMoveCount() == 0 && getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
                moves[p.getRow()][p.getColumn()] = true;
            }
        }
    }

    @Override
    public String toString() {
        return "P";
    }
}
