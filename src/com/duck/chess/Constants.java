package com.duck.chess;

// Contains various constants for the board
@SuppressWarnings("unused")
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
    // File characters
    public static final char[] FILES = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    // Rank characters
    public static final char[] RANKS = new char[]{'1', '2', '3', '4', '5', '6', '7', '8'};
    // FENs
    public static final String FEN_DEFAULT = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    // Maps piece to character
    public static final String[] PIECE_TO_CHAR = new String[13];
    // Squares
    public static final int A1 = 0;
    public static final int B1 = 1;
    public static final int C1 = 2;
    public static final int D1 = 3;
    public static final int E1 = 4;
    public static final int F1 = 5;
    public static final int G1 = 6;
    public static final int H1 = 7;
    public static final int A2 = 16;
    public static final int B2 = 17;
    public static final int C2 = 18;
    public static final int D2 = 19;
    public static final int E2 = 20;
    public static final int F2 = 21;
    public static final int G2 = 22;
    public static final int H2 = 23;
    public static final int A3 = 32;
    public static final int B3 = 33;
    public static final int C3 = 34;
    public static final int D3 = 35;
    public static final int E3 = 36;
    public static final int F3 = 37;
    public static final int G3 = 38;
    public static final int H3 = 39;
    public static final int A4 = 48;
    public static final int B4 = 49;
    public static final int C4 = 50;
    public static final int D4 = 51;
    public static final int E4 = 52;
    public static final int F4 = 53;
    public static final int G4 = 54;
    public static final int H4 = 55;
    public static final int A5 = 64;
    public static final int B5 = 65;
    public static final int C5 = 66;
    public static final int D5 = 67;
    public static final int E5 = 68;
    public static final int F5 = 69;
    public static final int G5 = 70;
    public static final int H5 = 71;
    public static final int A6 = 80;
    public static final int B6 = 81;
    public static final int C6 = 82;
    public static final int D6 = 83;
    public static final int E6 = 84;
    public static final int F6 = 85;
    public static final int G6 = 86;
    public static final int H6 = 87;
    public static final int A7 = 96;
    public static final int B7 = 97;
    public static final int C7 = 98;
    public static final int D7 = 99;
    public static final int E7 = 100;
    public static final int F7 = 101;
    public static final int G7 = 102;
    public static final int H7 = 103;
    public static final int A8 = 112;
    public static final int B8 = 113;
    public static final int C8 = 114;
    public static final int D8 = 115;
    public static final int E8 = 116;
    public static final int F8 = 117;
    public static final int G8 = 118;
    public static final int H8 = 119;
    public static final String[] SQUARE_TO_STRING = new String[]{
            "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1", "", "", "", "", "", "", "", "",
            "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", "", "", "", "", "", "", "", "",
            "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", "", "", "", "", "", "", "", "",
            "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", "", "", "", "", "", "", "", "",
            "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", "", "", "", "", "", "", "", "",
            "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", "", "", "", "", "", "", "", "",
            "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7", "", "", "", "", "", "", "", "",
            "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8", "", "", "", "", "", "", "", "",
    };
    public static final int NORTH = 16;
    public static final int SOUTH = -16;
    public static final int EAST = 1;
    public static final int WEST = -1;
    public static final boolean[] IS_SLIDER = new boolean[]{
            false,
            false, false, true, true, true, false,
    };
    public static final int[][] VECTORS = new int[][]{
            {},
            // Pawn
            {},
            // Knight
            {
                    NORTH + NORTH + WEST, NORTH + NORTH + EAST,
                    SOUTH + SOUTH + WEST, SOUTH + SOUTH + EAST,
                    WEST + WEST + NORTH, WEST + WEST + SOUTH,
                    EAST + EAST + NORTH, EAST + EAST + SOUTH,
            },
            // Bishop
            {
                    NORTH + WEST, NORTH + EAST,
                    SOUTH + WEST, SOUTH + EAST,
            },
            // Rook
            {
                    NORTH, SOUTH,
                    WEST, EAST,
            },
            // Queen
            {
                    NORTH + WEST, NORTH + EAST,
                    SOUTH + WEST, SOUTH + EAST,
                    NORTH, SOUTH,
                    WEST, EAST,
            },
            // King
            {
                    NORTH + WEST, NORTH + EAST,
                    SOUTH + WEST, SOUTH + EAST,
                    NORTH, SOUTH,
                    WEST, EAST,
            },
    };
    // Castle right bits
    public static int CASTLE_WHITE_K = 0b1000;
    public static int CASTLE_WHITE_Q = 0b0100;
    public static int CASTLE_BLACK_K = 0b0010;
    public static int CASTLE_BLACK_Q = 0b0001;

    static {
        PIECE_TO_CHAR[0] = " ";

        PIECE_TO_CHAR[PIECE_WHITE_PAWN] = "P";
        PIECE_TO_CHAR[PIECE_WHITE_KNIGHT] = "N";
        PIECE_TO_CHAR[PIECE_WHITE_BISHOP] = "B";
        PIECE_TO_CHAR[PIECE_WHITE_ROOK] = "R";
        PIECE_TO_CHAR[PIECE_WHITE_QUEEN] = "Q";
        PIECE_TO_CHAR[PIECE_WHITE_KING] = "K";

        PIECE_TO_CHAR[PIECE_BLACK_PAWN] = "p";
        PIECE_TO_CHAR[PIECE_BLACK_KNIGHT] = "n";
        PIECE_TO_CHAR[PIECE_BLACK_BISHOP] = "b";
        PIECE_TO_CHAR[PIECE_BLACK_ROOK] = "r";
        PIECE_TO_CHAR[PIECE_BLACK_QUEEN] = "q";
        PIECE_TO_CHAR[PIECE_BLACK_KING] = "k";
    }

    public static int pieceFromTypeAndColor(int pt, int color) {
        return pt + color * 6;
    }

    public static boolean isWhite(int pc) {
        return pc > PIECE_NONE && pc <= PIECE_WHITE_KING;
    }

    public static boolean isBlack(int pc) {
        return pc >= PIECE_BLACK_PAWN;
    }

    // Gets the type of the piece
    public static int pieceTypeOfPiece(int pc) {
        return (pc - 1) % 6 + 1;
    }

    // Gets the color of the piece
    public static int colorOfPiece(int pc) {
        return (pc - 1) / 6;
    }

    // Square utils
    public static int squareFile(int sq) {
        return sq & 7;
    }

    public static int squareRank(int sq) {
        return sq >> 4;
    }

    public static boolean isLegalSquare(int sq) {
        return (sq & 0x88) == 0;
    }
}

