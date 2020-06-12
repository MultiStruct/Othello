package com.codingame.game.othello;

public class Cell {
    int x, y;
    Piece piece;

    Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.piece = null;
    }

    Piece placePiece(int owner) {
        this.piece = new Piece(owner, this);
        return this.piece;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Piece getPiece() {
        return piece;
    }

    @Override
    public String toString() {
        return (char)(97 + x) + Integer.toString(y + 1);
    }
}
