package com.duck;

import com.duck.chess.Board;
import com.duck.chess.Constants;
import com.duck.chess.Move;

import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        var board = new Board("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
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
