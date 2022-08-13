package com.duck;

import com.duck.chess.Board;
import com.duck.engine.HCE;
import com.duck.engine.Perft;

public class Main {
    public static void main(String[] args) {
        var board = new Board("r2q1rk1/3nbppp/p2pbn2/4p1P1/1p2P3/1NN1BP2/PPPQ3P/2KR1B1R w - - 0 13");
        board.display();
        System.out.println("Eval (White): " + HCE.evaluate(board));

        board = new Board("r2qnrk1/3Pbppp/3p4/6P1/p2NP3/4B3/pPPQ3P/2KR1B1R b - - 0 19");
        board.display();
        System.out.println("Eval (White): " + HCE.evaluate(board));

        board = new Board("rnbq1bnr/pppppk1p/5p2/P2NP1p1/2PP1B2/3B1N1P/1PQ2PP1/R3R1K1 w Q - 2 4");
        board.display();
        System.out.println("Eval (White): " + HCE.evaluate(board));
    }
}
