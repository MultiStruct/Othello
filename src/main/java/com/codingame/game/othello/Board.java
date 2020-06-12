package com.codingame.game.othello;

import java.util.ArrayList;
import java.util.List;

public class Board {
    int WIDTH = 8;
    int HEIGHT = 8;

    Cell[][] cells;
    List<Piece> pieces;
    int[] piecesCounter;
    // Adding this so I don't need to compute stuff twice, this is just for viewer.
    ArrayList<ArrayList<Piece>> flips;

    public Board() {
        cells = new Cell[HEIGHT][WIDTH];
        pieces = new ArrayList<>();
        piecesCounter = new int[2];
        flips = new ArrayList<>();

        for(int y = 0; y < HEIGHT; ++y) {
            for(int x = 0; x < WIDTH; ++x) {
                cells[y][x] = new Cell(x, y);
            }
        }
        setupBoard();
    }

    public Cell[][] getCells() {
        return cells;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public int[] getPiecesCounter() {
        return piecesCounter;
    }

    void setupBoard() {
        int half = HEIGHT / 2;
        for(int y = half - 1; y <= half; ++y) {
            for(int x = half - 1; x <= half; ++x) {
                Piece piece = cells[y][x].placePiece((x == y) ? 1 : 0);
                pieces.add(piece);
                ++piecesCounter[(x == y) ? 1 : 0];
            }
        }
    }

    public void applyAction(Cell cell, int player) {
        Piece piece = cell.placePiece(player);
        pieces.add(piece);
        ++piecesCounter[player];
        List<Piece> origins = new ArrayList<>();
        flips = new ArrayList<>();
        origins.add(piece);
        flip(origins);
    }

    void flip(List<Piece> origins) {
        List<Piece> toFlip = new ArrayList<>();
        for (Piece piece : origins) {
            for (Direction dir : Direction.values()) {
                List<Piece> piecesToAdd = new ArrayList<>();
                int x = piece.getX();
                int y = piece.getY();

                while (true) {
                    x += dir.x;
                    y += dir.y;
                    if (x < 0 || x > WIDTH - 1) break;
                    if (y < 0 || y > HEIGHT - 1) break;

                    Cell cell = cells[y][x];
                    if (cell.piece == null) {
                        break;
                    } else {
                        // Can only jump over opponent pieces
                        if (cell.piece.owner != piece.owner) {
                            piecesToAdd.add(cell.piece);
                        } else {
                            for(Piece p : piecesToAdd) {
                                if(!toFlip.contains(p))
                                    toFlip.add(p);
                            }

                            if(piecesToAdd.size() > 0) {
                                flips.add(new ArrayList());
                                flips.get(flips.size() - 1).addAll(piecesToAdd);
                            }

                            break;
                        }
                    }
                }
            }
        }

        for (Piece piece : toFlip) {
            --piecesCounter[piece.owner];
            piece.flip();
            ++piecesCounter[piece.owner];
        }
/*
        if (toFlip.size() > 0) {
            flips.add(new ArrayList());
            flips.get(flips.size() - 1).addAll(toFlip);
        }
 */
    }

    public List<Cell> getActions(int player) {
        List<Cell> actions = new ArrayList<>();

        for(Piece piece : pieces) {
            if (piece.owner != player) continue;

            for (Direction dir : Direction.values()) {
                int count = 0;
                int x = piece.getX();
                int y = piece.getY();
                while (true) {
                    x += dir.x;
                    y += dir.y;

                    if(x < 0 || x > WIDTH - 1) break;
                    if(y < 0 || y > HEIGHT - 1) break;

                    Cell cell = cells[y][x];

                    if(cell.piece == null) {
                        if (count > 0) {
                            // ADD ACTION
                            if(!actions.contains(cell))
                                actions.add(cell);
                        }
                        break;
                    } else {
                         // Can only jump over opponent pieces
                        if(cell.piece.owner != player) {
                            count++;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        //System.err.println("Number of actions " + actions.size());

        return actions;
    }
}
