package com.duck.chess;

// 0x88 Representation of a chess board
public class Board {
    // Board Declaration
    public int[] board = new int[128];
    // Side to move, 0 = white, 1 = black
    public int side_to_move = 0;
    // Castle rights, four bits KQkq
    public int castle_rights = 0b1111;
    // En Passant square
    public Integer ep = null;

    public Board() {
    }
}
