package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;

public abstract class ChessPiece extends Piece {
    private final Color color;
    private int moveCount;

    public ChessPiece(Board board, Color color) {
        super(board);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public ChessPosition getChessPosition() {
        return ChessPosition.fromPosition(position);
    }

    public int getMoveCount() {
        return moveCount;
    }

    protected void increaseMoveCount() {
        this.moveCount++;
    }

    protected void decreaseMoveCount() {
        this.moveCount--;
    }

    protected boolean isThereOpponentPiece(Position position) {
        ChessPiece piece = (ChessPiece) getBoard().piece(position);
        return piece != null && piece.getColor() != color;
    }

    protected void possibleMovesAbove(boolean[][] moves) {
        possibleMovesInDirection(-1, 0, moves);
    }

    protected void possibleMovesBelow(boolean[][] moves) {
        possibleMovesInDirection(1, 0, moves);
    }

    protected void possibleMovesLeft(boolean[][] moves) {
        possibleMovesInDirection(0, -1, moves);
    }

    protected void possibleMovesRight(boolean[][] moves) {
        possibleMovesInDirection(0, 1, moves);
    }

    private void possibleMovesInDirection(int rowStep, int colStep, boolean[][] moves) {
        Position p = new Position();
        p.setValues(position.getRow() + rowStep, position.getColumn() + colStep);
        while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;
            p.setValues(p.getRow() + rowStep, p.getColumn() + colStep);
        }
        if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;
        }
    }

    protected void possibleMoveSpot(int rowOffset, int colOffset, boolean[][] moves) {
        int row = position.getRow() + rowOffset;
        int column = position.getColumn() + colOffset;
        Position p = new Position(row, column);

        if (getBoard().positionExists(p)) {
            moves[row][column] = canMove(p);
        }
    }

    private boolean canMove(Position p) {
        return !getBoard().thereIsAPiece(p) || isThereOpponentPiece(p);
    }
}
