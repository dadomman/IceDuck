package com.duck;

import com.duck.chess.Board;
import com.duck.engine.Perft;

public class Main {
    public static void main(String[] args) {
        var board = new Board("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ");
        board.display();
        Perft.divPerft(board, 6);
    }
}
