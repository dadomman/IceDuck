package com.duck.chess;

public class History {
    public Move move;
    public int capturedPiece;
    public int color;
    public int ep;
    public int castle;
    public int fifty;

    public History(Move move, int capturedPiece, int color, int ep, int castle, int fifty) {
        this.move = move;
        this.capturedPiece = capturedPiece;
        this.color = color;
        this.ep = ep;
        this.castle = castle;
        this.fifty = fifty;
    }
}
