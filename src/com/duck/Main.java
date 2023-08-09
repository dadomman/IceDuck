package com.duck;

import com.duck.chess.Board;
import com.duck.chess.Constants;
import com.duck.chess.Move;
import com.duck.engine.Searcher;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        /*
        var board = new Board(Constants.FEN_DEFAULT);
        board.display();

        var searcher = new Searcher();

        for (int depth = 1; depth <= 10; depth++) {
            searcher.ClearSearch();
            System.out.println("Depth: " + depth);
            int score = searcher.NegamaxRoot(board, depth);
            System.out.println("Score: " + score);
            System.out.println("Best Move: " + searcher.bestMove.toUCI());
            System.out.print("PV: ");
            searcher.PrintPV();
            System.out.println();
            System.out.println("Nodes: " + searcher.nodesSearched);
            System.out.println();
        }
        */

        Scanner scanner = new Scanner(System.in);
        var searcher = new Searcher();
        var board = new Board(Constants.FEN_DEFAULT);

        while (true) {
            board.display();

            System.out.println("Input a legal UCI notation (e.g. e2e4 or b7b8q)");

            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("break")) {
                break;
            }
            var moves = board.genLegalMoves();
            boolean ok = false;
            Move realMove = null;
            for (Move m : moves) {
                if (m.toUCI().equals(input)) {
                    ok = true;
                    realMove = m;
                    break;
                }
            }
            if (!ok) {
                System.out.println(input + " is not a legal move. Please try again.");
                continue;
            }

            board.makeMove(realMove);
            board.display();
            searcher.IterativeDeepening(board, 6);
            board.makeMove(searcher.bestMove);
        }
        scanner.close();
    }
}
