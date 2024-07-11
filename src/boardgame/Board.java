package boardgame;

public class Board {
    private final int rows;
    private final int columns;
    private final Piece[][] pieces;

    public Board(int rows, int columns) {
        if (rows < 1 || columns < 1) {
            throw new BoardException("Error creating board: there must be at least 1 row and 1 column");
        }

        this.rows = rows;
        this.columns = columns;
        pieces = new Piece[rows][columns];
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Piece piece(int row, int column) {
        validatePositionExists(row, column);
        return pieces[row][column];
    }

    public Piece piece(Position position) {
        validatePositionExists(position);
        return pieces[position.getRow()][position.getColumn()];
    }

    public void placePiece(Piece piece, Position position) {
        validatePositionAvailable(position);

        pieces[position.getRow()][position.getColumn()] = piece;
        piece.position = position;
    }

    private boolean positionExists(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

    private void validatePositionExists(int row, int column) {
        if (!positionExists(row, column)) {
            throw new BoardException("Position does not exist");
        }
    }

    public boolean positionExists(Position position) {
        return positionExists(position.getRow(), position.getColumn());
    }

    private void validatePositionExists(Position position) {
        if (!positionExists(position)) {
            throw new BoardException("Position does not exist");
        }
    }

    public boolean thereIsAPiece(Position position) {
        return piece(position) != null;
    }

    private void validatePositionAvailable(Position position) {
        if (thereIsAPiece(position)) {
            throw new BoardException("There is already a piece in position " + position);
        }
    }
}
