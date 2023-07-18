package com.duck.chess;

import java.util.ArrayList;

import static com.duck.chess.Constants.PIECE_TO_CHAR;
import static com.duck.chess.Constants.SQUARE_TO_STRING;

public class Move {
    // Private for now because we might want to do encoding in the future.
    //Made source & target public so that they can be accessed by searcher
    public int source;
    public int target;
    public int score = 0;
    private int piece;
    private boolean isDoublePush;
    private boolean isCapture;
    private boolean isEnPassant;
    // 0 if not promotion
    private int promotionPiece;
    private boolean isCastle;
    private boolean isQuiet;


    public Move(int source, int target, int piece, boolean isDoublePush, boolean isCapture, boolean isEnPassant, int promotionPiece, boolean isCastle, boolean isQuiet) {
        this.source = source;
        this.target = target;
        this.piece = piece;
        this.isDoublePush = isDoublePush;
        this.isCapture = isCapture;
        this.isEnPassant = isEnPassant;
        this.promotionPiece = promotionPiece;
        this.isCastle = isCastle;
        this.isQuiet = isQuiet;
    }

    @Override
    public String toString() {
        return SQUARE_TO_STRING[source] + "-" + SQUARE_TO_STRING[target] + (isCapture ? " x" : "") + (isCastle ? " Castle" : "") + (isEnPassant ? " e.p." : "") + (promotionPiece != 0 ? " =" + PIECE_TO_CHAR[promotionPiece] : "");
    }

    public String toUCI() {
        return SQUARE_TO_STRING[source] + SQUARE_TO_STRING[target] + (promotionPiece != 0 ? PIECE_TO_CHAR[promotionPiece].toLowerCase() : "");
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

    public boolean isQuiet() {
        return isQuiet;
    }
}