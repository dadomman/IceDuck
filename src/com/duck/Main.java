package com.duck;

import com.duck.chess.Board;
import com.duck.engine.Searcher;

public class Main {
    public static void main(String[] args) {
        var board = new Board("2Bn4/3Nr1r1/8/1P1kp3/P5pp/PN4R1/1K2QP2/8 w - - 0 1");
        board.display();

        var searcher = new Searcher();

        System.out.println(searcher.NegamaxRoot(board, 4));
        System.out.println(searcher.bestMove.toUCI());
        searcher.PrintPV();
        System.out.println();
    }
}
