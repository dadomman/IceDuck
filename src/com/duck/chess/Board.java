package com.duck.chess;

// 0x88 Representation of a chess board
public class Board {
    // TODO: Implement the board
    // Board Declaration
    int [] board = new int[128];
    //Uses integers to represent one side or the other having the turn to move 0 (white) & 1 (black)
    int side_to_move = 0;
    //Next 2 lines represent if the kings of both sides can castle, 1 (castling is available) & 0 (Castling is unavailable)
    int wht_K_Cstl, wht_Q_Cstl, blk_K_Cstl, blk_Q_Cstl;
    wht_K_Cstl = wht_Q_Cstl = blk_K_Cstl = blk_Q_Cstl = 1;
    
}
