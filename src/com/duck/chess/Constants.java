package com.duck.chess;

// Contains various constants for the board
public final class Constants {
    /*
    COLORS
     */
    public static final int COLOR_WHITE = 0;
    public static final int COLOR_BLACK = 1;
    // COLOR_OPPONENT[color] returns the color of the opponent
    public static final int[] COLOR_OPPONENT = new int[]{COLOR_BLACK, COLOR_WHITE};

    /*
    Piece Types
     */
    public static final int PIECE_TYPE_NONE = 0;
    public static final int PIECE_TYPE_PAWN = 1;
    public static final int PIECE_TYPE_KNIGHT = 2;
    public static final int PIECE_TYPE_BISHOP = 3;
    public static final int PIECE_TYPE_ROOK = 4;
    public static final int PIECE_TYPE_QUEEN = 5;
    public static final int PIECE_TYPE_KING = 6;
    // Upper bound of piece types
    public static final int PIECE_TYPE_ALL = 7;

    /*
    Pieces
     */
    public static final int PIECE_NONE = 0;
    public static final int PIECE_WHITE_PAWN = 1;
    public static final int PIECE_WHITE_KNIGHT = 2;
    public static final int PIECE_WHITE_BISHOP = 3;
    public static final int PIECE_WHITE_ROOK = 4;
    public static final int PIECE_WHITE_QUEEN = 5;
    public static final int PIECE_WHITE_KING = 6;
    public static final int PIECE_BLACK_PAWN = 7;
    public static final int PIECE_BLACK_KNIGHT = 8;
    public static final int PIECE_BLACK_BISHOP = 9;
    public static final int PIECE_BLACK_ROOK = 10;
    public static final int PIECE_BLACK_QUEEN = 11;
    public static final int PIECE_BLACK_KING = 12;
    // Upper bound of pieces
    public static final int PIECE_ALL = 13;

    public static boolean isWhite(int pc) {
        return pc > PIECE_NONE && pc <= PIECE_WHITE_KING;
    }

    public static boolean isBlack(int pc) {
        return pc >= PIECE_BLACK_PAWN;
    }

    // Gets the type of the piece
    public static int piece_type_of_piece(int pc) {
        return (pc - 1) % 6 + 1;
    }

    // Gets the color of the piece
    public static int color_of_piece(int pc) {
        return (pc - 1) / 6;
    }
}
