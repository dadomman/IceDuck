package com.duck.engine;

import com.duck.chess.Board;
import com.duck.chess.Constants;

public final class Perft {
    public static void divPerft(Board board, int depth) {
        var moves = board.genLegalMoves();
        long nodes = 0;

        for (var m : moves) {
            board.makeMove(m);
            if (board.isSquareAttacked(board.kingLocations[Constants.COLOR_OPPONENT[board.side_to_move]], board.side_to_move)) {
                board.unmakeMove();
                continue;
            }
            var r = perft(board, depth - 1);
            nodes += r;
            System.out.println(m.toUCI() + ": " + r);
            board.unmakeMove();
        }

        System.out.println("Total: " + nodes);
    }

    public static long perft(Board board, int depth) {
        var moves = board.genLegalMoves();
        long nodes = 0;

        if (depth == 0) {
            return 1;
        }

        for (var m : moves) {
            board.makeMove(m);
            if (board.isSquareAttacked(board.kingLocations[Constants.COLOR_OPPONENT[board.side_to_move]], board.side_to_move)) {
                board.unmakeMove();
                continue;
            }
            nodes += perft(board, depth - 1);
            board.unmakeMove();
        }

        return nodes;
    }
}
