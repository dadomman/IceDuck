package com.duck.chess;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import static com.duck.chess.Constants.*;

// 0x88 Representation of a chess board
/*
8 | 112 113 114 115 116 117 118 119
7 | 096 097 098 099 100 101 102 103
6 | 080 081 082 083 084 085 086 087
5 | 064 065 066 067 068 069 070 071
4 | 048 049 050 051 052 053 054 055
3 | 032 033 034 035 036 037 038 039
2 | 016 017 018 019 020 021 022 023
1 | 000 001 002 003 004 005 006 007
   ---------------------------------
     A   B   C   D   E   F   G   H
 */
public class Board {
    // Board Declaration
    public int[] board = new int[128];
    // King Locations
    public int[] kingLocations = new int[2];
    // Side to move, 0 = white, 1 = black
    public int side_to_move = 0;
    // Castle rights, four bits KQkq
    public int castle_rights = 0b1111;
    // En Passant square
    public int ep = -1;
    // Integer representing the move number
    public int fifty = 1;
    // Move Stack for undoing moves
    public Stack<History> moveStack = new Stack<>();

    // Zobrist tables
    private final long[][] pieceSquareTable;
    private final long[] sideToMoveTable;
    private final long[] castlingRightTable;
    private final long[] enPassantTable;

    // Empty board
    public Board() {
        final Random rng = new Random(1234567);
        pieceSquareTable = new long[12][128];  // 12 for each type of piece (6 white and 6 black), 128 for each square
        sideToMoveTable = new long[2];  // 2 for white or black
        castlingRightTable = new long[16];  // 16 for all combinations of castling rights
        enPassantTable = new long[128];  // 128 for each square

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 128; j++) {
                pieceSquareTable[i][j] = rng.nextLong();
            }
        }
        for (int i = 0; i < 2; i++) {
            sideToMoveTable[i] = rng.nextLong();
        }
        for (int i = 0; i < 16; i++) {
            castlingRightTable[i] = rng.nextLong();
        }
        for (int i = 0; i < 128; i++) {
            enPassantTable[i] = rng.nextLong();
        }
    }


    // Parse FEN
    public Board(String fen) {
        this();
        var tokens = fen.split(" ");

        var board_string = tokens[0];

        var sq = A8;
        var inc = 0;
        for (var c : board_string.toCharArray()) {
            if (sq < 0) {
                break;
            }
            if (c == '/') {
                sq -= 16;
                inc = 0;
            } else if (c >= '1' && c <= '8') {
                inc += c - '0';
            } else {
                switch (c) {
                    case 'P' -> board[sq + inc] = PIECE_WHITE_PAWN;
                    case 'N' -> board[sq + inc] = PIECE_WHITE_KNIGHT;
                    case 'B' -> board[sq + inc] = PIECE_WHITE_BISHOP;
                    case 'R' -> board[sq + inc] = PIECE_WHITE_ROOK;
                    case 'Q' -> board[sq + inc] = PIECE_WHITE_QUEEN;
                    case 'K' -> {
                        board[sq + inc] = PIECE_WHITE_KING;
                        kingLocations[0] = sq + inc;
                    }
                    case 'p' -> board[sq + inc] = PIECE_BLACK_PAWN;
                    case 'n' -> board[sq + inc] = PIECE_BLACK_KNIGHT;
                    case 'b' -> board[sq + inc] = PIECE_BLACK_BISHOP;
                    case 'r' -> board[sq + inc] = PIECE_BLACK_ROOK;
                    case 'q' -> board[sq + inc] = PIECE_BLACK_QUEEN;
                    case 'k' -> {
                        board[sq + inc] = PIECE_BLACK_KING;
                        kingLocations[1] = sq + inc;
                    }
                    default -> {
                    }
                }
                inc++;
            }
        }

        var color = tokens[1].charAt(0);
        if (color == 'w') {
            side_to_move = COLOR_WHITE;
        } else {
            side_to_move = COLOR_BLACK;
        }

        var castle_rights_string = tokens[2];
        castle_rights = 0;
        for (var c : castle_rights_string.toCharArray()) {
            switch (c) {
                case 'K' -> castle_rights |= CASTLE_WHITE_K;
                case 'Q' -> castle_rights |= CASTLE_WHITE_Q;
                case 'k' -> castle_rights |= CASTLE_BLACK_K;
                case 'q' -> castle_rights |= CASTLE_BLACK_Q;
            }
        }
    }

    // Prettily print the board
    /*
        A B C D E F G H
       -----------------
    8 | r n b q k b n r | 8
    7 | p p p p p p p p | 7
    6 |                 | 6
    5 |                 | 5
    4 |                 | 4
    3 |                 | 3
    2 | P P P P P P P P | 2
    1 | R N B Q K B N R | 1
       -----------------
        A B C D E F G H

    Side to move: White
     */
    public void display() {
        System.out.println("    A B C D E F G H");
        System.out.println("   -----------------");
        for (int i = 7; i >= 0; i--) {
            System.out.print(Character.toUpperCase(RANKS[i]));
            System.out.print(" | ");
            for (int j = 0; j < 8; j++) {
                System.out.print(PIECE_TO_CHAR[board[i * 16 + j]] + " ");
            }
            System.out.print("| ");
            System.out.print(Character.toUpperCase(RANKS[i]));
            System.out.println();
        }
        System.out.println("   -----------------");
        System.out.println("    A B C D E F G H");

        System.out.println();
        System.out.println("Side to move: " + (side_to_move == COLOR_WHITE ? "White" : "Black"));
    }

    public long computeZobristHash() {
        long hash = 0L;

        for (int i = 0; i < 128; i++) {
            if (board[i] != 0) {  // if a piece is on the square
                hash ^= pieceSquareTable[board[i] - 1][i];
            }
        }

        hash ^= sideToMoveTable[side_to_move];
        hash ^= castlingRightTable[castle_rights];

        if (ep != -1) {  // if en passant is possible
            hash ^= enPassantTable[ep];
        }

        return hash;
    }

    public boolean in_check() {
        return isSquareAttacked(kingLocations[side_to_move], COLOR_OPPONENT[side_to_move]);
    }

    void removePiece(int piece, int source) {
        board[source] = 0;

        // Hashing
    }

    void addPiece(int piece, int source) {
        board[source] = piece;

        // Hashing
    }

    // Returns whether there are non-pawn pieces on the board
    public boolean hasNonPawns() {
        for (int i = 0; i < 128; i++) {
            if (board[i] != 0 && board[i] != PIECE_WHITE_PAWN && board[i] != PIECE_BLACK_PAWN) {
                return true;
            }
        }
        return false;
    }
    
    // Null Move Function
    public void nullMove() {
        side_to_move = COLOR_OPPONENT[side_to_move];
    }

    public boolean makeMove(Move move) {
        fifty++;

        var source = move.getSource();
        var target = move.getTarget();
        var promoted = move.getPromotionPiece();
        var piece = move.getPiece();
        var captured = board[target];

        moveStack.add(new History(move, 0, side_to_move, ep, castle_rights, fifty));

        side_to_move = COLOR_OPPONENT[side_to_move];

        if (move.isCapture() && captured != 0) {
            moveStack.peek().capturedPiece = captured;
            removePiece(captured, target);
            fifty = 0;
        } else if (pieceTypeOfPiece(piece) == PIECE_TYPE_PAWN) {
            fifty = 0;
        }

        removePiece(piece, source);

        if (promoted != 0) {
            addPiece(promoted, target);
        } else {
            addPiece(piece, target);
        }

        // Reset ep
        ep = -1;

        if (piece == PIECE_WHITE_KING) {
            kingLocations[0] = target;
        } else if (piece == PIECE_BLACK_KING) {
            kingLocations[1] = target;
        }

        switch (source) {
            case H1 -> castle_rights &= ~CASTLE_WHITE_K;
            case E1 -> castle_rights &= ~(CASTLE_WHITE_K | CASTLE_WHITE_Q);
            case A1 -> castle_rights &= ~CASTLE_WHITE_Q;
            case H8 -> castle_rights &= ~CASTLE_BLACK_K;
            case E8 -> castle_rights &= ~(CASTLE_BLACK_K | CASTLE_BLACK_Q);
            case A8 -> castle_rights &= ~CASTLE_BLACK_Q;
        }

        switch (target) {
            case H1 -> castle_rights &= ~CASTLE_WHITE_K;
            case E1 -> castle_rights &= ~(CASTLE_WHITE_K | CASTLE_WHITE_Q);
            case A1 -> castle_rights &= ~CASTLE_WHITE_Q;
            case H8 -> castle_rights &= ~CASTLE_BLACK_K;
            case E8 -> castle_rights &= ~(CASTLE_BLACK_K | CASTLE_BLACK_Q);
            case A8 -> castle_rights &= ~CASTLE_BLACK_Q;
        }

        if (move.isCastle()) {
            switch (target) {
                case G1 -> {
                    removePiece(PIECE_WHITE_ROOK, H1);
                    addPiece(PIECE_WHITE_ROOK, F1);
                }
                case C1 -> {
                    removePiece(PIECE_WHITE_ROOK, A1);
                    addPiece(PIECE_WHITE_ROOK, D1);
                }
                case G8 -> {
                    removePiece(PIECE_BLACK_ROOK, H8);
                    addPiece(PIECE_BLACK_ROOK, F8);
                }
                case C8 -> {
                    removePiece(PIECE_BLACK_ROOK, A8);
                    addPiece(PIECE_BLACK_ROOK, D8);
                }
            }
        }

        if (pieceTypeOfPiece(piece) == PIECE_TYPE_PAWN) {
            ep = (source + target) / 2;
        }

        if (move.isEnPassant()) {
            if (COLOR_OPPONENT[side_to_move] == COLOR_WHITE) {
                removePiece(PIECE_BLACK_PAWN, target + SOUTH);
            } else {
                removePiece(PIECE_WHITE_PAWN, target + NORTH);
            }
        }

        if (isSquareAttacked(kingLocations[COLOR_OPPONENT[side_to_move]], side_to_move)) {
            unmakeMove();
            return false;
        }

        return true;
    }

    public void unmakeMove() {
        var history = moveStack.pop();
        var source = history.move.getSource();
        var target = history.move.getTarget();
        var promoted = history.move.getPromotionPiece();
        var piece = history.move.getPiece();
        var captured = history.capturedPiece;

        fifty = history.fifty;

        ep = history.ep;

        side_to_move = history.color;

        castle_rights = history.castle;

        if (piece == PIECE_WHITE_KING) {
            kingLocations[0] = source;
        } else if (piece == PIECE_BLACK_KING) {
            kingLocations[1] = source;
        }

        if (promoted != 0) {
            removePiece(promoted, target);
        } else {
            removePiece(piece, target);
        }
        addPiece(piece, source);

        if (history.move.isCapture() && captured != 0 && !history.move.isEnPassant()) {
            addPiece(captured, target);
        }

        if (history.move.isCastle()) {
            switch (target) {
                case G1 -> {
                    removePiece(PIECE_WHITE_ROOK, F1);
                    addPiece(PIECE_WHITE_ROOK, H1);
                }
                case C1 -> {
                    removePiece(PIECE_WHITE_ROOK, D1);
                    addPiece(PIECE_WHITE_ROOK, A1);
                }
                case G8 -> {
                    removePiece(PIECE_BLACK_ROOK, F8);
                    addPiece(PIECE_BLACK_ROOK, H8);
                }
                case C8 -> {
                    removePiece(PIECE_BLACK_ROOK, D8);
                    addPiece(PIECE_BLACK_ROOK, A8);
                }
            }
        }

        if (history.move.isEnPassant()) {
            if (side_to_move == COLOR_WHITE) {
                addPiece(PIECE_BLACK_PAWN, target + SOUTH);
            } else {
                addPiece(PIECE_WHITE_PAWN, target + NORTH);
            }
        }
    }

    void genPawnQuietMoves(int i, ArrayList<Move> moves) {
        var piece = pieceFromTypeAndColor(PIECE_TYPE_PAWN, side_to_move);
        if (side_to_move == COLOR_WHITE) {
            if (board[i + NORTH] == 0) {
                if (squareRank(i) == 6) {
                    for (var promote_piece = PIECE_WHITE_KNIGHT; promote_piece <= PIECE_WHITE_QUEEN; promote_piece++) {
                        moves.add(new Move(
                                i, i + NORTH, piece,
                                false, false, false,
                                promote_piece, false, false)
                        );
                    }
                } else {
                    // Single Push
                    moves.add(new Move(
                            i, i + NORTH, piece,
                            false, false, false,
                            0, false, true)
                    );

                    if (squareRank(i) == 1 && board[i + NORTH + NORTH] == 0) {
                        // Double Push
                        moves.add(new Move(
                                i, i + NORTH + NORTH, piece,
                                true, false, false,
                                0, false, true)
                        );
                    }
                }
            }
        } else {
            if (board[i + SOUTH] == 0) {
                if (squareRank(i) == 1) {
                    for (var promote_piece = PIECE_BLACK_KNIGHT; promote_piece <= PIECE_BLACK_QUEEN; promote_piece++) {
                        moves.add(new Move(
                                i, i + SOUTH, piece,
                                false, false, false,
                                promote_piece, false, true)
                        );
                    }
                } else {
                    // Single Push
                    moves.add(new Move(
                            i, i + SOUTH, piece,
                            false, false, false,
                            0, false, true)
                    );

                    if (squareRank(i) == 6 && board[i + SOUTH + SOUTH] == 0) {
                        // Double Push
                        moves.add(new Move(
                                i, i + SOUTH + SOUTH, piece,
                                true, false, false,
                                0, false, true)
                        );
                    }
                }
            }
        }
    }

    void genPawnCaptureMoves(int i, ArrayList<Move> moves) {
        var piece = pieceFromTypeAndColor(PIECE_TYPE_PAWN, side_to_move);
        if (side_to_move == COLOR_WHITE) {
            var targetSquareLeft = i + NORTH + WEST;
            var targetSquareRight = i + NORTH + EAST;
            if (isLegalSquare(targetSquareLeft)) {
                if (targetSquareLeft == ep) {
                    moves.add(new Move(
                            i, targetSquareLeft, piece,
                            false, true, true,
                            0, false, true));
                } else if (board[targetSquareLeft] != 0
                        && colorOfPiece(board[targetSquareLeft]) == COLOR_BLACK) {
                    if (squareRank(i) == 6) {
                        for (var promote_piece = PIECE_WHITE_KNIGHT; promote_piece <= PIECE_WHITE_QUEEN; promote_piece++) {
                            moves.add(new Move(
                                    i, targetSquareLeft, piece,
                                    false, true, false,
                                    promote_piece, false, true)
                            );
                        }
                    } else {
                        moves.add(new Move(
                                i, targetSquareLeft, piece,
                                false, true, false,
                                0, false, true));
                    }
                }
            }
            if (isLegalSquare(targetSquareRight)) {
                if (targetSquareRight == ep) {
                    moves.add(new Move(
                            i, targetSquareRight, piece,
                            false, true, true,
                            0, false, true));
                } else if (board[targetSquareRight] != 0
                        && colorOfPiece(board[targetSquareRight]) == COLOR_BLACK) {
                    if (squareRank(i) == 6) {
                        for (var promote_piece = PIECE_WHITE_KNIGHT; promote_piece <= PIECE_WHITE_QUEEN; promote_piece++) {
                            moves.add(new Move(
                                    i, targetSquareRight, piece,
                                    false, true, false,
                                    promote_piece, false, true)
                            );
                        }
                    } else {
                        moves.add(new Move(
                                i, targetSquareRight, piece,
                                false, true, false,
                                0, false, true));
                    }
                }
            }
        } else {
            var targetSquareLeft = i + SOUTH + WEST;
            var targetSquareRight = i + SOUTH + EAST;
            if (isLegalSquare(targetSquareLeft)) {
                if (targetSquareLeft == ep) {
                    moves.add(new Move(
                            i, targetSquareLeft, piece,
                            false, true, true,
                            0, false, true));
                } else if (board[targetSquareLeft] != 0
                        && colorOfPiece(board[targetSquareLeft]) == COLOR_WHITE) {
                    if (squareRank(i) == 1) {
                        for (var promote_piece = PIECE_BLACK_KNIGHT; promote_piece <= PIECE_BLACK_QUEEN; promote_piece++) {
                            moves.add(new Move(
                                    i, targetSquareLeft, piece,
                                    false, true, false,
                                    promote_piece, false, true)
                            );
                        }
                    } else {
                        moves.add(new Move(
                                i, targetSquareLeft, piece,
                                false, true, false,
                                0, false, true));
                    }
                }
            }
            if (isLegalSquare(targetSquareRight)) {
                if (targetSquareRight == ep) {
                    moves.add(new Move(
                            i, targetSquareRight, piece,
                            false, true, true,
                            0, false, true));
                } else if (board[targetSquareRight] != 0
                        && colorOfPiece(board[targetSquareRight]) == COLOR_WHITE) {
                    if (squareRank(i) == 1) {
                        for (var promote_piece = PIECE_BLACK_KNIGHT; promote_piece <= PIECE_BLACK_QUEEN; promote_piece++) {
                            moves.add(new Move(
                                    i, targetSquareRight, piece,
                                    false, true, false,
                                    promote_piece, false, true)
                            );
                        }
                    } else {
                        moves.add(new Move(
                                i, targetSquareRight, piece,
                                false, true, false,
                                0, false, true));
                    }
                }
            }
        }
    }

    void genSliderMoves(int i, ArrayList<Move> moves) {
        for (var dir : VECTORS[pieceTypeOfPiece(board[i])]) {
            if (dir == 0) {
                break;
            }
            var targetSquare = i + dir;
            while (isLegalSquare(targetSquare)) {
                if (board[targetSquare] == 0) {
                    moves.add(new Move(
                            i, targetSquare, board[i],
                            false, false, false,
                            0, false, true));
                } else {
                    if (colorOfPiece(board[targetSquare]) != side_to_move) {
                        moves.add(new Move(
                                i, targetSquare, board[i],
                                false, true, false,
                                0, false, true));
                    }
                    break;
                }
                targetSquare += dir;
            }
        }
    }

    void genJumperMoves(int i, ArrayList<Move> moves) {
        for (var dir : VECTORS[pieceTypeOfPiece(board[i])]) {
            if (dir == 0) {
                break;
            }
            var targetSquare = i + dir;
            if (isLegalSquare(targetSquare)) {
                if (board[targetSquare] == 0) {
                    moves.add(new Move(
                            i, targetSquare, board[i],
                            false, false, false,
                            0, false, true));
                } else if (colorOfPiece(board[targetSquare]) != side_to_move) {
                    moves.add(new Move(
                            i, targetSquare, board[i],
                            false, true, false,
                            0, false, true));
                }
            }
        }
    }

    public ArrayList<Move> genLegalMoves() {
        var moves = new ArrayList<Move>(32);
        for (int i = 0; i < 120; i++) {
            if (!isLegalSquare(i) || board[i] == PIECE_NONE || colorOfPiece(board[i]) != side_to_move) {
                continue;
            }

            var pt = pieceTypeOfPiece(board[i]);

            if (pt == PIECE_TYPE_PAWN) {
                genPawnQuietMoves(i, moves);
                genPawnCaptureMoves(i, moves);
            } else if (IS_SLIDER[pt]) {
                genSliderMoves(i, moves);
            } else {
                genJumperMoves(i, moves);

                if (pt == PIECE_TYPE_KING) {
                    // Castling
                    var ks = kingLocations[side_to_move];

                    if (side_to_move == COLOR_WHITE) {
                        if ((castle_rights & CASTLE_WHITE_K) != 0) {
                            if (board[ks + EAST] == PIECE_NONE && board[ks + EAST + EAST] == PIECE_NONE) {

                                if (!isSquareAttacked(ks, COLOR_BLACK) && !isSquareAttacked(ks + EAST, COLOR_BLACK)) {
                                    moves.add(new Move(
                                            ks, ks + EAST + EAST, board[i],
                                            false, false, false,
                                            0, true, true));
                                }
                            }
                        }

                        if ((castle_rights & CASTLE_WHITE_Q) != 0) {
                            if (board[ks + WEST] == PIECE_NONE && board[ks + WEST + WEST] == PIECE_NONE && board[ks + WEST + WEST + WEST] == PIECE_NONE) {

                                if (!isSquareAttacked(ks, COLOR_BLACK) && !isSquareAttacked(ks + WEST, COLOR_BLACK)) {
                                    moves.add(new Move(
                                            ks, ks + WEST + WEST, board[i],
                                            false, false, false,
                                            0, true, true));
                                }
                            }
                        }
                    } else {
                        if ((castle_rights & CASTLE_BLACK_K) != 0) {
                            if (board[ks + EAST] == PIECE_NONE && board[ks + EAST + EAST] == PIECE_NONE) {

                                if (!isSquareAttacked(ks, COLOR_WHITE) && !isSquareAttacked(ks + EAST, COLOR_WHITE)) {
                                    moves.add(new Move(
                                            ks, ks + EAST + EAST, board[i],
                                            false, false, false,
                                            0, true, true));
                                }
                            }
                        }

                        if ((castle_rights & CASTLE_BLACK_Q) != 0) {
                            if (board[ks + WEST] == PIECE_NONE && board[ks + WEST + WEST] == PIECE_NONE && board[ks + WEST + WEST + WEST] == PIECE_NONE) {

                                if (!isSquareAttacked(ks, COLOR_WHITE) && !isSquareAttacked(ks + WEST, COLOR_WHITE)) {
                                    moves.add(new Move(
                                            ks, ks + WEST + WEST, board[i],
                                            false, false, false,
                                            0, true, true));
                                }
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }

    void genCaptureSliderMoves(int i, ArrayList<Move> moves) {
        for (var dir : VECTORS[pieceTypeOfPiece(board[i])]) {
            if (dir == 0) {
                break;
            }
            var targetSquare = i + dir;
            while (isLegalSquare(targetSquare)) {
                if (board[targetSquare] != 0) {
                    if (colorOfPiece(board[targetSquare]) != side_to_move) {
                        moves.add(new Move(
                                i, targetSquare, board[i],
                                false, true, false,
                                0, false, false));
                    }
                    break;
                }
                targetSquare += dir;
            }
        }
    }

    void genCaptureJumperMoves(int i, ArrayList<Move> moves) {
        for (var dir : VECTORS[pieceTypeOfPiece(board[i])]) {
            if (dir == 0) {
                break;
            }
            var targetSquare = i + dir;
            if (isLegalSquare(targetSquare)) {
                if (board[targetSquare] != 0) {
                    if (colorOfPiece(board[targetSquare]) != side_to_move) {
                        moves.add(new Move(
                                i, targetSquare, board[i],
                                false, true, false,
                                0, false, false));
                    }
                }
            }
        }
    }

    //genCaptureMoves
    public ArrayList<Move> genCaptureMoves() {
        var moves = new ArrayList<Move>(32);
        for (int i = 0; i < 120; i++) {
            if (!isLegalSquare(i) || board[i] == PIECE_NONE || colorOfPiece(board[i]) != side_to_move) {
                continue;
            }

            var pt = pieceTypeOfPiece(board[i]);

            if (pt == PIECE_TYPE_PAWN) {
                genPawnCaptureMoves(i, moves);
            } else if (IS_SLIDER[pt]) {
                genCaptureSliderMoves(i, moves);
            } else {
                genCaptureJumperMoves(i, moves);
            }
        }
        return moves;
    }

    // Function checks if square i is attacked by color
    public boolean isSquareAttacked(int i, int color) {
        for (var pt = PIECE_TYPE_PAWN; pt <= PIECE_TYPE_KING; pt++) {
            switch (pt) {
                case PIECE_TYPE_PAWN:
                    // Different based on color
                    int[] pawnTarget =
                            (color == COLOR_WHITE) ?
                                    new int[]{SOUTH + EAST, SOUTH + WEST} :
                                    new int[]{NORTH + EAST, NORTH + WEST};

                    for (var dir : pawnTarget) {
                        var targetSquare = i + dir;
                        if (isLegalSquare(targetSquare) && board[targetSquare] == pieceFromTypeAndColor(pt, color)) {
                            return true;
                        }
                    }

                    break;

                case PIECE_TYPE_KNIGHT:
                case PIECE_TYPE_KING:
                    // Fetches the directions from Vectors in Constants
                    for (var dir : VECTORS[pt]) {
                        if (dir == 0) {
                            break;
                        }
                        var targetSquare = i + dir;
                        if (isLegalSquare(targetSquare) && board[targetSquare] == pieceFromTypeAndColor(pt, color)) {
                            return true;
                        }
                    }
                    break;

                case PIECE_TYPE_BISHOP:
                case PIECE_TYPE_ROOK:
                case PIECE_TYPE_QUEEN:
                    for (var dir : VECTORS[pt]) {
                        if (dir == 0) {
                            break;
                        }
                        var targetSquare = i + dir;
                        while (isLegalSquare(targetSquare)) {
                            if (board[targetSquare] != PIECE_NONE) {
                                if (board[targetSquare] == pieceFromTypeAndColor(pt, color)) {
                                    return true;
                                }
                                break;
                            }

                            targetSquare += dir;
                        }
                    }
                    break;
            }
        }

        return false;
    }
}