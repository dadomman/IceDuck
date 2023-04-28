package com.duck.chess;

import static com.duck.chess.Constants.PIECE_TO_CHAR;
import static com.duck.chess.Constants.SQUARE_TO_STRING;
import static com.duck.chess.Constants.pieceTypeOfPiece;

import java.util.ArrayList;

import com.duck.engine.HCE;
import com.duck.engine.Searcher;

public class Move {
    // Private for now because we might want to do encoding in the future.
	//Made source & target public so that they can be accessed by searcher
    public int source;
    public int target;
    private int piece;
    private boolean isDoublePush;
    private boolean isCapture;
    private boolean isEnPassant;
    // 0 if not promotion
    private int promotionPiece;
    private boolean isCastle;

    public int score = 0;

    public Move(int source, int target, int piece, boolean isDoublePush, boolean isCapture, boolean isEnPassant, int promotionPiece, boolean isCastle) {
        this.source = source;
        this.target = target;
        this.piece = piece;
        this.isDoublePush = isDoublePush;
        this.isCapture = isCapture;
        this.isEnPassant = isEnPassant;
        this.promotionPiece = promotionPiece;
        this.isCastle = isCastle;
    }

    @Override
    public String toString() {
        return SQUARE_TO_STRING[source] + "-" + SQUARE_TO_STRING[target] + (isCapture ? " x" : "") + (isCastle ? " Castle" : "") + (isEnPassant ? " e.p." : "") + (promotionPiece != 0 ? " =" + PIECE_TO_CHAR[promotionPiece] : "");
    }

    public String toUCI() {
        return SQUARE_TO_STRING[source] + SQUARE_TO_STRING[target] + (promotionPiece != 0 ? PIECE_TO_CHAR[promotionPiece] : "");
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getPiece() {
        return piece;
    }

    public void setPiece(int piece) {
        this.piece = piece;
    }

    public boolean isDoublePush() {
        return isDoublePush;
    }

    public void setDoublePush(boolean doublePush) {
        isDoublePush = doublePush;
    }

    public boolean isCapture() {
        return isCapture;
    }

    public void setCapture(boolean capture) {
        isCapture = capture;
    }

    public boolean isEnPassant() {
        return isEnPassant;
    }

    public void setEnPassant(boolean enPassant) {
        isEnPassant = enPassant;
    }

    public int getPromotionPiece() {
        return promotionPiece;
    }

    public void setPromotionPiece(int promotionPiece) {
        this.promotionPiece = promotionPiece;
    }

    public boolean isCastle() {
        return isCastle;
    }

    public void setCastle(boolean castle) {
        isCastle = castle;
    }
    //Partition
    public int partition(float [] a, ArrayList<Move> b, int low, int high ) {
    	float pivot = a[high];
    	int i = low - 1;
    	for (int j = low; j <= high-1; j++) {
    		if (a[j] < pivot) {
    			i++;
    			float t = a[i];  
    	        a[i] = a[j];  
    	        a[j] = t; 
    	        Move v = b.get(i);  
    	        b.set(i, b.get(j));  
    	        b.set(j, v);
    		}
    	}
    	 float t = a[i+1];  
    	 a[i+1] = a[high];  
    	 a[high] = t;
    	 Move v = b.get(i+1);  
    	 b.set(i+1, b.get(high));  
    	 b.set(high, v);
    	 return (i+1);
    }
}