package com.duck;

import com.duck.chess.Board;
import com.duck.chess.Constants;

public class Main {
    public static void main(String[] args) {
	    var board = new Board(Constants.FEN_DEFAULT);
        board.display();
    }
}
