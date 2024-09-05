package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

    private ChessMatch chessMatch;

    public Pawn(Board board, Color color, ChessMatch chessMatch) {
        super(board, color);
        this.chessMatch = chessMatch;
    }

    @Override
    public boolean[][] possibleMoves() {
        int rows = getBoard().getRows();
        int cols = getBoard().getColumns();
        boolean[][] moves = new boolean[rows][cols];

        int moveDirection = Color.WHITE.equals(getColor()) ? -1 : 1;

        possibleMovesForward(moveDirection, moves);
        possibleMovesCapture(moveDirection, moves);

        possibleMoveEnPassant(moves);

        return moves;
    }

    private void possibleMoveEnPassant(boolean[][] moves) {
        int left = -1;
        int right = 1;
        int enPassantRow = Color.WHITE.equals(getColor()) ? 3 : 4;
        int forward = Color.WHITE.equals(getColor()) ? -1 : 1;
        possibleMoveEnPassantSingle(moves, enPassantRow, left, forward);
        possibleMoveEnPassantSingle(moves, enPassantRow, right, forward);
    }

    private void possibleMoveEnPassantSingle(boolean[][] moves, int enPassantRow, int side, int forward) {
        if (position.getRow() == enPassantRow) {
            Position sidePosition = new Position(position.getRow(), position.getColumn() + side);
            Position forwardSidePosition = new Position(position.getRow() + forward, position.getColumn() + side);
            if (getBoard().positionExists(sidePosition) && isThereOpponentPiece(sidePosition) && getBoard().piece(sidePosition) == chessMatch.getEnPassantVulnerable() && !getBoard().thereIsAPiece(forwardSidePosition)) {
                moves[sidePosition.getRow() + forward][sidePosition.getColumn()] = true;
            }
        }
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
