package com.duck.engine;

import com.duck.chess.Board;
import com.duck.chess.Constants;

public class HCE {
    // Material Weights
    // Note: King probably won't matter because both kings always cancel out.
    public static int[] Weights = new int[]{
            100, 320, 330, 500, 900, 0,
    };

    // The Piece-Square Table
    // Note: Later we will use a separate table for each piece specifically for endgames (i.e. king activity etc.)
    public static int[][] PSQT = new int[][]{
            // @formatter:off
            // Pawn
            {
                      0,  0,  0,  0,  0,  0,  0,  0,   0,0,0,0,0,0,0,0,
                     50, 50, 50, 50, 50, 50, 50, 50,   0,0,0,0,0,0,0,0,
                     10, 10, 20, 30, 30, 20, 10, 10,   0,0,0,0,0,0,0,0,
                      5,  5, 10, 25, 25, 10,  5,  5,   0,0,0,0,0,0,0,0,
                      0,  0,  0, 20, 20,  0,  0,  0,   0,0,0,0,0,0,0,0,
                      5, -5,-10,  0,  0,-10, -5,  5,   0,0,0,0,0,0,0,0,
                      5, 10, 10,-20,-20, 10, 10,  5,   0,0,0,0,0,0,0,0,
                      0,  0,  0,  0,  0,  0,  0,  0,   0,0,0,0,0,0,0,0,
            },
            // Knight
            {
                    -50,-40,-30,-30,-30,-30,-40,-50,   0,0,0,0,0,0,0,0,
                    -40,-20,  0,  0,  0,  0,-20,-40,   0,0,0,0,0,0,0,0,
                    -30,  0, 10, 15, 15, 10,  0,-30,   0,0,0,0,0,0,0,0,
                    -30,  5, 15, 20, 20, 15,  5,-30,   0,0,0,0,0,0,0,0,
                    -30,  0, 15, 20, 20, 15,  0,-30,   0,0,0,0,0,0,0,0,
                    -30,  5, 10, 15, 15, 10,  5,-30,   0,0,0,0,0,0,0,0,
                    -40,-20,  0,  5,  5,  0,-20,-40,   0,0,0,0,0,0,0,0,
                    -50,-40,-30,-30,-30,-30,-40,-50,   0,0,0,0,0,0,0,0,
            },
            // Bishop
            {
                    -20,-10,-10,-10,-10,-10,-10,-20,   0,0,0,0,0,0,0,0,
                    -10,  0,  0,  0,  0,  0,  0,-10,   0,0,0,0,0,0,0,0,
                    -10,  0,  5, 10, 10,  5,  0,-10,   0,0,0,0,0,0,0,0,
                    -10,  5,  5, 10, 10,  5,  5,-10,   0,0,0,0,0,0,0,0,
                    -10,  0, 10, 10, 10, 10,  0,-10,   0,0,0,0,0,0,0,0,
                    -10, 10, 10, 10, 10, 10, 10,-10,   0,0,0,0,0,0,0,0,
                    -10,  5,  0,  0,  0,  0,  5,-10,   0,0,0,0,0,0,0,0,
                    -20,-10,-10,-10,-10,-10,-10,-20,   0,0,0,0,0,0,0,0,
            },
            // Rook
            {
                      0,  0,  0,  0,  0,  0,  0,  0,   0,0,0,0,0,0,0,0,
                      5, 10, 10, 10, 10, 10, 10,  5,   0,0,0,0,0,0,0,0,
                     -5,  0,  0,  0,  0,  0,  0, -5,   0,0,0,0,0,0,0,0,
                     -5,  0,  0,  0,  0,  0,  0, -5,   0,0,0,0,0,0,0,0,
                     -5,  0,  0,  0,  0,  0,  0, -5,   0,0,0,0,0,0,0,0,
                     -5,  0,  0,  0,  0,  0,  0, -5,   0,0,0,0,0,0,0,0,
                     -5,  0,  0,  0,  0,  0,  0, -5,   0,0,0,0,0,0,0,0,
                      0,  0,  0,  5,  5,  0,  0,  0,   0,0,0,0,0,0,0,0,
            },
            // Queen
            {
                    -20,-10,-10, -5, -5,-10,-10,-20,   0,0,0,0,0,0,0,0,
                    -10,  0,  0,  0,  0,  0,  0,-10,   0,0,0,0,0,0,0,0,
                    -10,  0,  5,  5,  5,  5,  0,-10,   0,0,0,0,0,0,0,0,
                     -5,  0,  5,  5,  5,  5,  0, -5,   0,0,0,0,0,0,0,0,
                      0,  0,  5,  5,  5,  5,  0, -5,   0,0,0,0,0,0,0,0,
                    -10,  5,  5,  5,  5,  5,  0,-10,   0,0,0,0,0,0,0,0,
                    -10,  0,  5,  0,  0,  0,  0,-10,   0,0,0,0,0,0,0,0,
                    -20,-10,-10, -5, -5,-10,-10,-20,   0,0,0,0,0,0,0,0,
            },
            // King
            {
                    -30,-40,-40,-50,-50,-40,-40,-30,   0,0,0,0,0,0,0,0,
                    -30,-40,-40,-50,-50,-40,-40,-30,   0,0,0,0,0,0,0,0,
                    -30,-40,-40,-50,-50,-40,-40,-30,   0,0,0,0,0,0,0,0,
                    -30,-40,-40,-50,-50,-40,-40,-30,   0,0,0,0,0,0,0,0,
                    -20,-30,-30,-40,-40,-30,-30,-20,   0,0,0,0,0,0,0,0,
                    -10,-20,-20,-20,-20,-20,-20,-10,   0,0,0,0,0,0,0,0,
                     20, 20,  0,  0,  0,  0, 20, 20,   0,0,0,0,0,0,0,0,
                     20, 30, 10,  0,  0, 10, 30, 20,   0,0,0,0,0,0,0,0,
            }
            // @formatter:on
    };

    public static int evaluate(Board board) {
        int score = 0;
        for (int i = 0; i < 120; i++) {
            if (!Constants.isLegalSquare(i) || board.board[i] == Constants.PIECE_NONE) {
                continue;
            }
            int piece = board.board[i];
            int pt = Constants.pieceTypeOfPiece(piece) - 1;  // -1 for 1-based indexing

            int mat, psqt;
            if (Constants.colorOfPiece(piece) == Constants.COLOR_WHITE) {
                mat = Weights[pt];
                psqt = PSQT[pt][i ^ 56];  // flips due to encoding
            } else {
                mat = -Weights[pt];
                psqt = -PSQT[pt][i];  // flip & flip = no flip
            }

            score += mat + psqt;
        }
        return score;
    }
}
