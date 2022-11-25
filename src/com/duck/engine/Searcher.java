package com.duck.engine;

import com.duck.chess.Board;
import com.duck.chess.Constants;
import com.duck.chess.Move;

import java.util.ArrayList;

// Searcher Class
public class Searcher {
    public static int MateValue = 999999;
    public static int MaxDepth = 256;

    public Move bestMove = null;
    public int ply = 0;

    public Move[][] pvTable = new Move[MaxDepth][MaxDepth];
    public int[] pvLength = new int[MaxDepth];

    // Clears PV table
    public void ClearSearch() {
        for (int i = 0; i < MaxDepth; i++) {
            pvLength[i] = 0;
            for (int j = 0; j < MaxDepth; j++) {
                pvTable[i][j] = null;
            }
        }
    }

    // Prints the Principal Variation without newline
    public void PrintPV() {
        for (int i = 0; i < pvLength[ply]; i++) {
            System.out.print(pvTable[0][i].toUCI() + (i == pvLength[ply] - 1 ? "" : " "));
        }
    }

    public void ScoreMoves(ArrayList<Move> moves) {
        for (var move : moves) {
            // TODO
        }
    }

    // Root Search Function
    public int NegamaxRoot(Board board, int depth) {
        ply = 0;

        return Negamax(board, depth, -MateValue, MateValue);
    }

    // Quiescence Search Function
    public int Quiescence(Board board, int alpha, int beta) {
        if (ply >= MaxDepth) {
            return HCE.evaluateForSTM(board);
        }

        var qmoves = board.genCaptureMoves();
        int value = HCE.evaluateForSTM(board);
        for (var i = 0; i < qmoves.size(); i++) {
            var moveget = qmoves.get(i);
            if (!board.makeMove(moveget)) continue;
            ply++;
            int score = -Quiescence(board, -beta, -alpha);
            ply--;
            board.unmakeMove();

            if (score > value) {
                value = score;

                if (value > alpha) {
                    alpha = value;

                    if (alpha >= beta) {
                        break;
                    }
                }
            }
        }

        return value;
    }

    // Recursive Negamax Search Function
    public int Negamax(Board board, int depth, int alpha, int beta) {
        if (ply >= MaxDepth) {
            return HCE.evaluateForSTM(board);
        }

        pvLength[ply] = 0;

        // Mate-Distance Pruning
        {
            var rAlpha = Math.max(alpha, -MateValue + ply);
            var rBeta = Math.min(beta, MateValue - ply - 1);

            if (rAlpha >= rBeta) {
                return rAlpha;
            }
        }

        // Horizon
        if (depth == 0) {
            return Quiescence(board, alpha, beta);
        }

        // Generate Legal Moves
        var moves = board.genLegalMoves();
        int value = -MateValue;

        var movesMade = 0;

        // Loop through all moves
        for (var i = 0; i < moves.size(); i++) {
            var m = moves.get(i);

            if (!board.makeMove(m)) continue;
            ply++;
            movesMade++;
            // Get score from next Ply
            int score = -Negamax(board, depth - 1, -beta, -alpha);
            if (ply == 0) {
                System.out.println(m.toUCI() + ": " + score);
            }
            ply--;
            board.unmakeMove();

            if (score > value) {
                value = score;

                if (value > alpha) {
                    alpha = value;
                    if (ply == 0) {
                        bestMove = m;
                    }

                    // Update Principal Variation
                    pvTable[ply][0] = m;
                    System.arraycopy(pvTable[ply + 1], 0, pvTable[ply], 1, pvLength[ply + 1]);
                    pvLength[ply] = pvLength[ply + 1] + 1;

                    // Alpha-Beta Pruning
                    if (alpha >= beta) {
                        break;
                    }
                }
            }
        }

        // Checkmate or Stalemate
        if (movesMade == 0) {
            if (board.isSquareAttacked(board.kingLocations[board.side_to_move], Constants.COLOR_OPPONENT[board.side_to_move])) {
                return -MateValue + ply;
            } else {
                return 0;
            }
        }

        return value;
    }
}