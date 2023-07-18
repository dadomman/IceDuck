package com.duck.engine;

import com.duck.chess.Move;

public class TTEntry {
    int score;
    // 0 = Exact
    // 1 = Lower
    // 2 = Upper
    int flag;
    int depth;
    Move bestMove;

    TTEntry(int ttScore, int ttFlag, int ttdepth, Move bm) {
        score = ttScore;
        flag = ttFlag;
        depth = ttdepth;
        bestMove = bm;
    }
}
