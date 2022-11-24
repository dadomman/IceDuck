package com.duck;

import com.duck.chess.Board;
import com.duck.chess.Constants;
import com.duck.engine.Searcher;

public class Main {
    public static void main(String[] args) {
        var board = new Board(Constants.FEN_DEFAULT);
        board.display();

        var searcher = new Searcher();

        for (int depth = 1; depth <= 5; depth++) {
            System.out.println("Depth: " + depth);
            System.out.println(searcher.NegamaxRoot(board, depth));
            System.out.println(searcher.bestMove.toUCI());
            searcher.PrintPV();
            System.out.println();
            System.out.println();
        }
    }
}
