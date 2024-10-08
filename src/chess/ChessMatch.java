package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

import java.util.ArrayList;
import java.util.List;

public class ChessMatch {

    private int turn;
    private Color currentPlayer;
    private final Board board;
    private boolean check;
    private boolean checkMate;
    private ChessPiece enPassantVulnerable;
    private ChessPiece promoted;

    private final List<Piece> piecesOnTheBoard = new ArrayList<>();
    private final List<Piece> capturedPieces = new ArrayList<>();

    public ChessMatch() {
        this.board = new Board(8, 8);
        this.turn = 1;
        this.currentPlayer = Color.WHITE;
        initialSetup();
    }

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean getCheck() {
        return check;
    }

    public boolean getCheckMate() {
        return checkMate;
    }

    public ChessPiece getEnPassantVulnerable() {
        return enPassantVulnerable;
    }

    public ChessPiece getPromoted() {
        return promoted;
    }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] matrix = new ChessPiece[board.getRows()][board.getColumns()];

        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                matrix[i][j] = (ChessPiece) board.piece(i, j);
            }
        }

        return matrix;
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();

        validateSourcePosition(source);
        validateTargetPosition(source, target);

        Piece capturedPiece = makeMove(source, target);

        ChessPiece movedPiece = (ChessPiece) board.piece(target);

        promoted = null;
        if (movedPiece instanceof Pawn) {
            int backrow = Color.WHITE.equals(movedPiece.getColor()) ? 0 : 7;
            if (target.getRow() == backrow) {
                promoted = movedPiece;
                promoted = replacePromotedPiece("Q");
            }
        }

        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece);
            throw new ChessException("You can't put yourself in check");
        }

        check = testCheck(opponent(currentPlayer));

        if (check) {
            checkMate = testCheckMate(opponent(currentPlayer));
        }

        if (!checkMate) {
            nextTurn();
        }

        if (movedPiece instanceof Pawn && ((targetPosition.getRow() == sourcePosition.getRow() - 2) || (targetPosition.getRow() == sourcePosition.getRow() + 2))) {
            enPassantVulnerable = movedPiece;
        } else {
            enPassantVulnerable = null;
        }

        return (ChessPiece) capturedPiece;
    }

    public ChessPiece replacePromotedPiece(String type) {
        if (promoted == null) {
            throw new IllegalStateException("There is no piece to be promoted");
        }

        Position pos = promoted.getChessPosition().toPosition();
        Piece p = board.removePiece(pos);
        piecesOnTheBoard.remove(p);

        ChessPiece newPiece = newPromotedPiece(type);
        board.placePiece(newPiece, pos);
        piecesOnTheBoard.add(newPiece);

        return newPiece;
    }

    private ChessPiece newPromotedPiece(String type) {
        return switch (type) {
            case "Q" -> new Queen(board, promoted.getColor());
            case "B" -> new Bishop(board, promoted.getColor());
            case "N" -> new Knight(board, promoted.getColor());
            case "R" -> new Rook(board, promoted.getColor());
            default -> promoted;
        };
    }

    private Piece makeMove(Position source, Position target) {
        ChessPiece piece = (ChessPiece) board.removePiece(source);
        piece.increaseMoveCount();
        Piece capturedPiece = board.removePiece(target);

        board.placePiece(piece, target);

        if (capturedPiece != null) {
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }

        optionalDoCastling(source, target, piece);

        if (piece instanceof Pawn && source.getColumn() != target.getColumn() && capturedPiece == null) {
            capturedPiece = doEnPassant(source, target);
        }

        return capturedPiece;
    }

    private Piece doEnPassant(Position source, Position target) {
        Piece capturedPiece;
        Position pawnPosition = new Position(source.getRow(), target.getColumn());
        capturedPiece = board.removePiece(pawnPosition);
        capturedPieces.add(capturedPiece);
        piecesOnTheBoard.remove(capturedPiece);
        return capturedPiece;
    }

    private void undoEnPassant(Position source, Position target) {
        ChessPiece pawn = (ChessPiece) board.removePiece(target);
        Position pawnPosition = new Position(source.getRow(), target.getColumn());
        board.placePiece(pawn, pawnPosition);
    }

    private void optionalDoCastling(Position source, Position target, ChessPiece piece) {
        // small castling
        if (piece instanceof King && target.getColumn() == (source.getColumn() + 2)) {
            Position rookSourcePosition = new Position(source.getRow(), source.getColumn() + 3);
            Position rookTargetPosition = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece) board.removePiece(rookSourcePosition);
            board.placePiece(rook, rookTargetPosition);
            rook.increaseMoveCount();
        }

        // large castling
        if (piece instanceof King && target.getColumn() == (source.getColumn() - 2)) {
            Position rookSourcePosition = new Position(source.getRow(), source.getColumn() - 4);
            Position rookTargetPosition = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece) board.removePiece(rookSourcePosition);
            board.placePiece(rook, rookTargetPosition);
            rook.increaseMoveCount();
        }
    }

    private void undoMove(Position source, Position target, Piece capturedPiece) {
        ChessPiece piece = (ChessPiece) board.removePiece(target);
        piece.decreaseMoveCount();
        board.placePiece(piece, source);

        if (capturedPiece != null) {
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
            board.placePiece(capturedPiece, target);
        }

        optionalUndoCastling(source, target, piece);

        if (piece instanceof Pawn && source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
            undoEnPassant(source, target);
        }
    }

    private void optionalUndoCastling(Position source, Position target, ChessPiece piece) {
        // small castling
        if (piece instanceof King && target.getColumn() == (source.getColumn() + 2)) {
            Position rookSourcePosition = new Position(source.getRow(), source.getColumn() + 3);
            Position rookTargetPosition = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece) board.removePiece(rookTargetPosition);
            board.placePiece(rook, rookSourcePosition);
            rook.decreaseMoveCount();
        }

        // large castling
        if (piece instanceof King && target.getColumn() == (source.getColumn() - 2)) {
            Position rookSourcePosition = new Position(source.getRow(), source.getColumn() - 4);
            Position rookTargetPosition = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece) board.removePiece(rookTargetPosition);
            board.placePiece(rook, rookSourcePosition);
            rook.decreaseMoveCount();
        }
    }

    private void validateSourcePosition(Position position) {
        if (!board.thereIsAPiece(position)) {
            throw new ChessException("There is no piece in source position");
        }

        ChessPiece piece = (ChessPiece) board.piece(position);
        if (!piece.getColor().equals(currentPlayer)) {
            throw new ChessException("The chosen piece is not yours");
        }

        if (!board.piece(position).isThereAnyPossibleMove()) {
            throw new ChessException("There is no available move for this piece");
        }
    }

    private void validateTargetPosition(Position source, Position target) {
        if (!board.piece(source).possibleMove(target)) {
            throw new ChessException("This piece can't move to the target position");
        }
    }

    private void nextTurn() {
        turn++;
        currentPlayer = Color.WHITE.equals(currentPlayer) ? Color.BLACK : Color.WHITE;
    }

    private Color opponent(Color color) {
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king(Color color) {
        List<Piece> list = getPieces(color);
        for (Piece p : list) {
            if (p instanceof King) {
                return (ChessPiece) p;
            }
        }
        throw new IllegalStateException("There is no " + color + " king on the board.");
    }

    private List<Piece> getPieces(Color color) {
        return piecesOnTheBoard.stream().filter(p -> ((ChessPiece) p).getColor() == color).toList();
    }

    private boolean testCheck(Color color) {
        Position kingPosition = king(color).getChessPosition().toPosition();

        List<Piece> opponentPieces = getPieces(opponent(color));

        for (Piece p : opponentPieces) {
            boolean[][] mat = p.possibleMoves();
            if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
                return true;
            }
        }

        return false;
    }

    private boolean testCheckMate(Color color) {
        List<Piece> pieces = getPieces(color);

        for (Piece p : pieces) {
            boolean[][] moves = p.possibleMoves();
            for (int i = 0; i < moves.length; i++) {
                for (int j = 0; j < moves.length; j++) {
                    if (moves[i][j] && !testMoveForCheck(color, p, i, j)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean testMoveForCheck(Color color, Piece piece, int i, int j) {
        Position source = ((ChessPiece) piece).getChessPosition().toPosition();
        Position target = new Position(i, j);
        Piece capturedPiece = makeMove(source, target);

        boolean testCheck = testCheck(color);

        undoMove(source, target, capturedPiece);

        return testCheck;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }

    private void initialSetup() {
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));;
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
    }
}
