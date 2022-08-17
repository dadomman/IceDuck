package com.duck.engine;

import com.duck.chess.Board;

public class Searcher {
    public int Negamax(Board board, int depth, int alpha, int beta) {
        if (depth == 0) {
            return HCE.evaluateForSTM(board);
        }

        // ... implement Negamax

        return alpha;
    }
}
