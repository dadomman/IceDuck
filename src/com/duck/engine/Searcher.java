package com.duck.engine;

import com.duck.chess.Board;
public class Searcher {
    public int Negamax(Board board, int depth, int alpha, int beta) {
        if (depth == 0) {
            return HCE.evaluateForSTM(board);
        }
        var moves = board.genLegalMoves();
        final int mateValue = 999999;
        int bestValue = -mateValue;

        for(var i = 0; i < moves.size(); i++) {
            board.makeMove(moves.get(i));
            bestValue = Math.max(bestValue, - Negamax(board, depth - 1, -beta, -alpha));
            board.unmakeMove();
        }

        return alpha;
    }
}