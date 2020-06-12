package com.codingame.game.othello;

public class Piece {
    // 1 WHITE 0 BLACK
    int owner;
    Cell cell;

    Piece(int owner, Cell cell) {
        this.owner = owner;
        this.cell = cell;
    }

    public int getOwner() {
        return owner;
    }

    int getX() {
        return cell.x;
    }

    int getY() {
        return cell.y;
    }

    void flip() {
        this.owner ^= 1;
    }
}
