package com.duck.engine;

import com.duck.chess.Board;
import com.duck.chess.Move;
public class Searcher {
    public static int MateValue = 999999;

    public Move bestMove = null;

    public int NegamaxRoot(Board board, int depth) {
        int alpha = -MateValue;
        int beta = MateValue;

        if (depth == 0) {
            return HCE.evaluateForSTM(board);
        }
        var moves = board.genLegalMoves();
        final int mateValue = MateValue;
        int bestValue = -mateValue;

        for(var i = 0; i < moves.size(); i++) {
            Move m = moves.get(i);
            board.makeMove(m);
            int value = -Negamax(board, depth - 1, -beta, -alpha);
            System.out.println(m.toString() + ":  "+ value);
            board.unmakeMove();

            // alpha = Math.max(alpha, bestValue);
            if (value > bestValue) {
                bestValue = value;
                bestMove = m;
            }
            if (bestValue > alpha) {
                alpha = bestValue;
            }
            if (alpha >= beta){
                break;
            }
        }

        return bestValue;
    }
        
    public int Negamax(Board board, int depth, int alpha, int beta) {
        if (depth == 0) {
            return HCE.evaluateForSTM(board);
        }
        var moves = board.genLegalMoves();
        final int mateValue = MateValue;
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