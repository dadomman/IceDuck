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
            int value = -Negamax(board, depth - 1, -beta, -alpha);
            bestValue = Math.max(bestValue, value);
            board.unmakeMove();

            alpha = Math.max(alpha, bestValue);
            if (alpha >= beta){
                break;
            }
        }

        return bestValue;
    }
}