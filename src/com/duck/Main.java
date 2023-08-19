package com.duck;

import com.duck.chess.Board;
import com.duck.chess.Constants;
import com.duck.chess.Move;
import com.duck.engine.Searcher;

import java.util.Scanner;

public class Main {
    static void playHuman() {
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
            searcher.IterativeDeepening(board, 20, 10000);
            board.makeMove(searcher.bestMove);
        }
        scanner.close();
    }

    static void startUCI() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Iceduck Version 0.0.0");

        Board board = new Board(Constants.FEN_DEFAULT);
        Searcher searcher = new Searcher();
        Thread thread = null;

        while (true) {
            String[] input = scanner.nextLine().trim().split(" ");
            if (input.length == 0) continue;
            String command = input[0];
            if (command.equals("quit")) {
                break;
            }
            if (command.equals("uci")) {
                System.out.println("id name Iceduck 0.0.0");
                System.out.println("id author dadomman and SnowballSH");
                System.out.println("uciok");
                continue;
            }
            if (command.equals("ucinewgame")) {
                board = new Board(Constants.FEN_DEFAULT);
                searcher.ClearSearch();
                searcher.transpositionTable.clear();
                continue;
            }
            if (command.equals("isready")) {
                System.out.println("readyok");
                continue;
            }
            if (command.equals("d")) {
                board.display();
                continue;
            }
            if (command.equals("position")) {
                if (input.length <= 2) continue;
                int i = 2;
                if (input[1].equals("fen")) {
                    StringBuilder fen = new StringBuilder();
                    while (i < input.length && !input[i].equals("moves")) {
                        fen.append(input[i]).append(" ");
                        i += 1;
                    }
                    board = new Board(fen.toString());
                } else if (input[1].equals("startpos")) {
                    board = new Board(Constants.FEN_DEFAULT);
                }
                if (i < input.length && input[i].equals("moves")) {
                    i += 1;
                    while (i < input.length) {
                        var moves = board.genLegalMoves();
                        boolean ok = false;
                        for (Move move : moves) {
                            if (move.toUCI().equals(input[i])) {
                                board.makeMove(move);
                                ok = true;
                            }
                        }
                        if (!ok) break;
                        i += 1;
                    }
                }
                continue;
            }
            if (command.equals("go")) {
                int movetime = 10000;
                if (input.length >= 3) {
                    if (input[1].equals("movetime")) {
                        movetime = Math.max(10, Integer.parseInt(input[2]) - 20);  // overhead
                    }
                }
                int searchTime = movetime;
                Board finalBoard = board;
                if (thread != null) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                thread = new Thread(() -> {
                    searcher.IterativeDeepening(finalBoard, 20, searchTime);
                });
                thread.setDaemon(true);
                thread.start();
                // TODO
                continue;
            }
            if (command.equals("stop")) {
                searcher.stop = true;
                continue;
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        startUCI();
    }
}
