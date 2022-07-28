package com.duck;

import com.duck.chess.Board;
import com.duck.chess.Constants;
import com.duck.chess.Move;

import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        var board = new Board("4k2q/3ppp1p/6n1/8/8/2BN4/PPP5/1KR5 w - - 0 1");
        board.display();
        var moves = board.genLegalMoves();
        System.out.println(moves.size());
        System.out.println(
                moves
                        .stream()
                        .map(Move::toString)
                        .collect(
                                Collectors.joining("\n")
                        )
        );
    }
}
