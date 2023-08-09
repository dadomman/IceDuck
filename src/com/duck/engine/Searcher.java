package com.duck.engine;

import com.duck.chess.Board;
import com.duck.chess.Constants;
import com.duck.chess.Move;

import java.util.ArrayList;
import java.util.HashMap;

// Searcher Class
public class Searcher {
    public static int MateValue = 999999;
    public static int MaxDepth = 256;
    public Move bestMove = null;
    public int ply = 0;
    public int nodesSearched = 0;
    public Move[][] pvTable = new Move[MaxDepth][MaxDepth];
    public int[] pvLength = new int[MaxDepth];
    //Add KillerArray to store at most 2 "Killer Moves"
    public Move[][] KillerArray = new Move[MaxDepth][2];
    public int[][] HistoryArray = new int[128][128];
    //Uses HashMap class to create new table
    public HashMap<Long, TTEntry> transpositionTable = new HashMap<>(1 << 20);
    public boolean inNull = false;

    public long startTime;
    public int maxMillis;
    public boolean stop = false;

    // Clears PV table
    public void ClearSearch() {
        for (int i = 0; i < MaxDepth; i++) {
            pvLength[i] = 0;
            KillerArray[i][0] = null;
            KillerArray[i][1] = null;
            for (int j = 0; j < MaxDepth; j++) {
                pvTable[i][j] = null;
            }
        }
        for (int i = 0; i < HistoryArray.length; i++) {
            for (int j = 0; j < HistoryArray[0].length; j++) {
                HistoryArray[i][j] = 0;
            }
        }
    }

    // Prints the Principal Variation without newline
    public void PrintPV() {
        for (int i = 0; i < pvLength[0]; i++) {
            System.out.print(pvTable[0][i].toUCI() + (i == pvLength[0] - 1 ? "" : " "));
        }
    }

    public static final int HASHMOVE_SCORE = 10_000_000;
    public static final int KILLER_SCORE = 1_000_000;
    public static final int CAPTURE_SCORE = 500_000;

    //Scores our moves to be sorted to save time
    public void ScoreMoves(ArrayList<Move> moves, Board board, Move hashMove) {
        for (var move : moves) {
            if (move.equals(hashMove)) {
                move.score = HASHMOVE_SCORE;
            } else if (move.equals(KillerArray[ply][0])) {
                move.score = 2 * KILLER_SCORE;
            } else if (move.equals(KillerArray[ply][1])) {
                move.score = KILLER_SCORE;
            } else if (move.isCapture()) {
                if (move.isEnPassant()) {  // special case
                    move.score = CAPTURE_SCORE + 3000;
                } else {
                    float ratio = (float) HCE.Weights[Constants.pieceTypeOfPiece(board.board[move.target]) - 1] / HCE.Weights[Constants.pieceTypeOfPiece(board.board[move.source]) - 1];
                    move.score = CAPTURE_SCORE + (int) (ratio * 200);  // turn it into an integer
                }
            }
            //History Heuristic move scores based upon depth^2
            else {
                move.score = Math.min(CAPTURE_SCORE, HistoryArray[move.getSource()][move.getTarget()]);
            }
        }
    }

    boolean shouldStop() {
        return stop || System.currentTimeMillis() - startTime >= maxMillis;
    }

    public void IterativeDeepening(Board board, int maxDepth, int maxTimeMillis) {
        maxMillis = maxTimeMillis;
        startTime = System.currentTimeMillis();
        stop = false;
        board.display();
        Move lastBest = board.genLegalMoves().get(0);
        // Not completed, currently just for debugging purposes
        ClearSearch();
        for (int depth = 1; depth <= maxDepth; depth++) {
            int score = NegamaxRoot(board, depth);
            if (shouldStop()) {
                System.out.println("bestmove " + lastBest.toUCI());
                stop = true;
                break;
            }
            lastBest = bestMove;
            long elapsed = System.currentTimeMillis() - startTime;
            System.out.print("info depth " + depth + " score " + score + " nodes " + nodesSearched + " time " + elapsed + " nps " + (elapsed == 0 ? nodesSearched : (int) ((float) nodesSearched / ((float) elapsed / 1000.0))) + " pv ");
            PrintPV();
            System.out.println();
        }
        bestMove = lastBest;
    }

    // Root Search Function
    public int NegamaxRoot(Board board, int depth) {
        ply = 0;
        nodesSearched = 0;

        return Negamax(board, depth, -MateValue, MateValue);
    }

    // Quiescence Search Function
    public int Quiescence(Board board, int alpha, int beta) {
        int staticEval = HCE.evaluateForSTM(board);
        if (ply >= MaxDepth) {
            return staticEval;
        }

        int value = staticEval;
        if (value > beta) {
            return beta;
        }
        if (value < alpha - 1000) {
            return alpha;
        }
        if (value > alpha) {
            alpha = value;
        }

        if (shouldStop()) {
            stop = true;
            return 0;
        }

        var hash = board.computeZobristHash();

        TTEntry ttEntry = transpositionTable.get(hash);
        Move hashMove = null;
        if (ttEntry != null) {
            var ttScore = ttEntry.score;
            var ttFlag = ttEntry.flag;
            hashMove = ttEntry.bestMove;

            switch (ttFlag) {
                case 0:
                    return ttScore;
                case 1:
                    if (ttScore >= beta) return ttScore;
                case 2:
                    if (ttScore <= alpha) return ttScore;
            }
        }

        ArrayList<Move> qmoves;

        qmoves = board.genCaptureMoves();
        if (qmoves.isEmpty()) {
            return staticEval;
        }

        ScoreMoves(qmoves, board, hashMove);
        Quicksort(qmoves, 0, qmoves.size() - 1);

        for (var i = 0; i < qmoves.size(); i++) {
            var moveget = qmoves.get(i);
            if (!board.makeMove(moveget)) continue;
            ply++;
            int score = -Quiescence(board, -beta, -alpha);
            ply--;
            board.unmakeMove();

            if (shouldStop()) {
                stop = true;
                return 0;
            }

            if (score > value) {
                value = score;

                if (score > alpha) {
                    if (score >= beta) {
                        return beta;
                    }

                    alpha = score;
                }
            }
        }

        return value;
    }

    // Recursive Negamax Search Function
    public int Negamax(Board board, int depth, int alpha, int beta) {
        int original_alpha = alpha;

        if (ply >= MaxDepth) {
            return HCE.evaluateForSTM(board);
        }

        pvLength[ply] = 0;
        nodesSearched++;

        // Mate-Distance Pruning
        {
            var rAlpha = Math.max(alpha, -MateValue + ply);
            var rBeta = Math.min(beta, MateValue - ply - 1);

            if (rAlpha >= rBeta) {
                return rAlpha;
            }
        }

        if (shouldStop()) {
            stop = true;
            return 0;
        }

        boolean inCheck = board.in_check();

        if (inCheck) {
            depth += 1;
        }
        // Horizon
        if (depth == 0) {
            return Quiescence(board, alpha, beta);
        }

        var hash = board.computeZobristHash();

        TTEntry ttEntry = transpositionTable.get(hash);
        Move hashMove = null;
        if (ttEntry != null) {
            var ttScore = ttEntry.score;
            var ttFlag = ttEntry.flag;
            var ttDepth = ttEntry.depth;
            hashMove = ttEntry.bestMove;
            if (ttScore > MateValue - 100 && ttScore <= MateValue) {
                ttScore -= ply;
            } else if (ttScore < -MateValue + 100 && ttScore >= -MateValue) {
                ttScore += ply;
            }

            if (ply > 0 && ttDepth >= depth) {
                switch (ttFlag) {
                    case 0 -> {
                        return ttScore;
                    }
                    case 1 -> alpha = Math.max(alpha, ttScore);
                    case 2 -> beta = Math.min(beta, ttScore);
                }
                if (alpha >= beta) {
                    return ttScore;
                }
            }
        }

        int staticEval = HCE.evaluateForSTM(board);


        if (!inCheck) {
            // Reverse Futility Pruning
            // If we are at a low depth and the static eval is way higher than beta, we can safely return beta.
            if (depth <= 5 && staticEval - depth * 70 >= beta) {
                return beta;
            }

            // Null Move Pruning
            // We only do NMP if:
            // 1. We did not do a null move previously
            // 2. Depth >= 3
            // 3. Static eval is better than beta
            // 4. We are not in pawn-only endgame, in which case zugzwang greatly affects the result.
            // If satisfied, we search in [-beta, -beta+1] with a reduced depth.
            if (!inNull && depth >= 3 && staticEval >= beta && board.hasNonPawns()) {
                board.nullMove();
                inNull = true;

                // reduced depth
                int r = Math.min(depth, 3 + depth / 3 + Math.min(4, (staticEval - beta) / 200));
                int value = Negamax(board, depth - r, -beta, -beta + 1);

                board.nullMove();
                inNull = false;
                if (value > beta) {
                    // Found a mate, but we can't be sure that's actually possible
                    if (value >= MateValue - 100) value = beta;
                    return value;
                }
            }
        }


        // Generate Legal Moves
        var moves = board.genLegalMoves();
        int value = -MateValue;

        var movesMade = 0;


        //Score and Sort Moves
        ScoreMoves(moves, board, hashMove);
        Quicksort(moves, 0, moves.size() - 1);
//        if (ply == 0) {
//            for (var m : moves) {
//                System.out.println(m.toUCI() + " " + m.score);
//            }
//        }

        // Loop through all moves
        for (var i = 0; i < moves.size(); i++) {
            var m = moves.get(i);

            if (!board.makeMove(m)) continue;
            ply++;
            movesMade++;
            // Introduces Reduction Variable Based upon depth and index
            int reduction = 0;
            // We search the first few moves at full depth
            if (!board.in_check() && depth >= 4 && i >= 4) {
                reduction = (int) Math.floor(0.43 * Math.log(depth) * Math.log(i) + 0.77);
            }
            // Get score from next Ply
            int newDepth = Math.max(0, Math.min(depth - 1, depth - reduction - 1));
            int score = -Negamax(board, newDepth, -beta, -alpha);
            ply--;
//            if (ply == 0) {
//                System.out.println(m.toUCI() + ": " + score);
//            }
            board.unmakeMove();

            if (shouldStop()) {
                stop = true;
                return 0;
            }

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
                        //Killer Move Update-implementation
                        if (!(m.isCapture())) {
                            KillerArray[ply][1] = KillerArray[ply][0];
                            KillerArray[ply][0] = m;
                        }
                        //History Heuristic
                        if (m.isQuiet()) {
                            HistoryArray[m.getSource()][m.getTarget()] += depth * depth;
                            if (HistoryArray[m.getSource()][m.getTarget()] > CAPTURE_SCORE) {
                                for (int u = 0; u < HistoryArray.length; u++) {
                                    for (int v = 0; v < HistoryArray[0].length; v++) {
                                        HistoryArray[u][v] = Math.max(1, HistoryArray[u][v] / 100);
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }


        // Checkmate or Stalemate
        if (movesMade == 0) {
            if (board.in_check()) {
                return -MateValue + ply;
            } else {
                return 0;
            }
        }

        //Store position in transpositionTable
        transpositionTable.put(hash, new TTEntry(value, value >= beta ? 1 : alpha != original_alpha ? 0 : 2, depth, bestMove));

        return value;
    }


    //Partition
    public int partition(ArrayList<Move> b, int low, int high) {
        int pivot = b.get(high).score;
        int i = low - 1;
        for (int j = low; j <= high - 1; j++) {
            if (b.get(j).score > pivot) {
                i++;
                Move v = b.get(i);
                b.set(i, b.get(j));
                b.set(j, v);
            }
        }
        Move v = b.get(i + 1);
        b.set(i + 1, b.get(high));
        b.set(high, v);
        return (i + 1);
    }

    //Quicksort, factor into NegaMax later
    public void Quicksort(ArrayList<Move> b, int low, int high) {
        if (low < high) {
            int p = partition(b, low, high);
            Quicksort(b, low, p - 1);
            Quicksort(b, p + 1, high);
        }
    }

    // Implemented somewhere else
//    //Declare new list of moves listed based on scores, change MVVLVA based on new version
//    Board board2 = new Board();
//
//    //Move Ordering MVV-LVA
//    public ArrayList<Move> MVVLVA() {
//        ArrayList<Move> Capturemoves = board2.genCaptureMoves();
//        //New Array for ratios in order, any swap also swaps Capturemoves
//        float[] Ratiolist = new float[Capturemoves.size()];
//        for (int i = 0; i < Capturemoves.size(); i++) {
//            float ratio = (float) HCE.Weights[Constants.pieceTypeOfPiece(Capturemoves.get(i).source)] / HCE.Weights[Constants.pieceTypeOfPiece(Capturemoves.get(i).target)];
//            Ratiolist[i] = ratio;
//        }
//        //Quicksort enacted on Ratiolist
//        Quicksort(Capturemoves, 0, Capturemoves.size());
//        return Capturemoves;
//    }
}