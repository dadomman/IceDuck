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
    public Move[][] pvTable = new Move[MaxDepth][MaxDepth];
    public int[] pvLength = new int[MaxDepth];
    //Add KillerArray to store at most 2 "Killer Moves"
    public Move[][] KillerArray = new Move[MaxDepth][2];
    public int[][] HistoryArray = new int[128][128];
    //Uses HashMap class to create new table
    HashMap<String, Integer> transpositionTable = new HashMap<String, Integer>();

    // Clears PV table
    public void ClearSearch() {
        for (int i = 0; i < MaxDepth; i++) {
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

    public static final int CAPTURE_SCORE = 100_000;

    //Scores our moves to be sorted to save time
    public void ScoreMoves(ArrayList<Move> moves, Board board) {
        for (var move : moves) {
            if (move == KillerArray[ply][0]) {
            	move.score = 20000;
            }
            else if (move == KillerArray[ply][1]) {
            	move.score = 10000;
            }
            if (move.isCapture()) {
                if (move.isEnPassant()) {  // special case
                    move.score = CAPTURE_SCORE + 100;
                } else {
                    float ratio = (float) HCE.Weights[Constants.pieceTypeOfPiece(board.board[move.target])] / HCE.Weights[Constants.pieceTypeOfPiece(board.board[move.source])];
                    move.score = CAPTURE_SCORE + (int) (ratio * 100);  // turn it into an integer
                }
            }
            //History Heuristic move scores based upon depth^2
            else {
            	move.score = HistoryArray[move.getSource()][move.getTarget()];
            }
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
                    	//Killer Move Update-implementation
                    	if(!(m.isCapture())) {
                    		KillerArray[ply][1] = KillerArray[ply][0];
                    		KillerArray[ply][0] = m;
                    	}
                    	//History Heuristic, Find isQuiet Method or create it
                    	if (m.isQuiet()) {
                    		HistoryArray[m.getSource()][m.getTarget()] += depth*depth;
                    	}
                        break;
                    }
                }
              //Store position in transpositionTable
                transpositionTable.put(board.getFEN(), score);
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
    

    //Partition
    public int partition(ArrayList<Move> b, int low, int high) {
        int pivot = b.get(high).score;
        int i = low - 1;
        for (int j = low; j <= high - 1; j++) {
            if (b.get(j).score < pivot) {
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