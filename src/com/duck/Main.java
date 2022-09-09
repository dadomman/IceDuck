package com.duck;

import com.duck.chess.Board;
import com.duck.chess.Constants;
import com.duck.engine.HCE;
import com.duck.engine.Perft;
import com.duck.engine.Searcher;

public class Main {
    public static void main(String[] args) {
        var board = new Board(Constants.FEN_DEFAULT);
        board.display();

        var searcher = new Searcher();

    System.out.println(searcher.Negamax(board, 5, -99999, 99999));
    }
}
