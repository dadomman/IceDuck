package com.duck;

import com.duck.chess.Board;
import com.duck.chess.Constants;
import com.duck.engine.Searcher;

public class Main {
    public static void main(String[] args) {
        var board = new Board(Constants.FEN_DEFAULT);
        board.display();

        var searcher = new Searcher();

        System.out.println(searcher.NegamaxRoot(board, 8));
        System.out.println(searcher.bestMove.toUCI());
        searcher.PrintPV();
        System.out.println();
    }
}
