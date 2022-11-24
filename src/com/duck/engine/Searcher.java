package com.duck.engine;

import com.duck.chess.Board;
import com.duck.chess.Constants;
import com.duck.chess.Move;

// Searcher Class
public class Searcher {
    public static int MateValue = 999999;
    public static int MaxDepth = 128;

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

    // Root Search Function
    public int NegamaxRoot(Board board, int depth) {
        pvLength[ply] = 0;

        int alpha = -MateValue;
        int beta = MateValue;

        ply = 0;

        if (depth == 0) {
            return HCE.evaluateForSTM(board);
        }
        var moves = board.genLegalMoves();
        int bestValue = -MateValue;

        // Loop through all moves
        for (var i = 0; i < moves.size(); i++) {
            Move m = moves.get(i);
            if (!board.makeMove(m)) continue;
            ply++;
            int value = -Negamax(board, depth - 1, -beta, -alpha);
            // System.out.println(m + ":  " + value);
            ply--;
            board.unmakeMove();

            if (value > bestValue) {
                // Better move
                bestValue = value;
                bestMove = m;

                if (bestValue > alpha) {
                    alpha = bestValue;

                    pvTable[ply][0] = m;
                    System.arraycopy(pvTable[ply + 1], 0, pvTable[ply], 1, pvLength[ply + 1]);
                    pvLength[ply] = pvLength[ply + 1] + 1;

                    if (alpha >= beta) {
                        break;
                    }
                }
            }
        }

        return bestValue;
    }

    // Quiescence Search Function
    public int Quiescence(Board board, int alpha, int beta) {
        pvLength[ply] = 0;
        var qmoves = board.genCaptureMoves();
        int value = HCE.evaluateForSTM(board);
        for (var i = 0; i < qmoves.size(); i++) {
            var moveget = qmoves.get(i);
            board.makeMove(moveget);
            int score = -Quiescence(board, -beta, -alpha);
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
        pvLength[ply] = 0;

        if (ply >= MaxDepth) {
            return HCE.evaluateForSTM(board);
        }

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
            // TODO: Add quiescence search
            return HCE.evaluateForSTM(board);
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
            ply--;
            board.unmakeMove();

            if (score > value) {
                value = score;

                if (value > alpha) {
                    alpha = value;

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