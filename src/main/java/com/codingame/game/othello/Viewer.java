package com.codingame.game.othello;

import com.codingame.game.Player;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.*;
import com.codingame.gameengine.module.toggle.ToggleModule;

import java.util.ArrayList;

public class Viewer {
    MultiplayerGameManager<Player> gameManager;
    GraphicEntityModule graphics;
    ToggleModule toggleModule;
    int HEIGHT;
    int WIDTH;
    int RECTANGLE_COLOR = 0x54966c;
    int VIEWER_WIDTH;
    int VIEWER_HEIGHT;
    int RECTANGLE_SIZE;
    int CIRCLE_RADIUS;
    int GAP;
    int FONT_SIZE = 32;
    Board board;
    Rectangle[][] rectangles;
    Circle[][] pieces;
    int[] colors = new int[]{0x000000, 0xFFFFFF};
    PlayerUI[] playerUIS = new PlayerUI[2];


    public Viewer(GraphicEntityModule graphics, Board board, MultiplayerGameManager<Player> gameManager,ToggleModule toggleModule) {
        this.graphics = graphics;
        this.board = board;
        this.gameManager = gameManager;
        this.toggleModule = toggleModule;

        VIEWER_WIDTH = this.graphics.getWorld().getWidth();
        VIEWER_HEIGHT = this.graphics.getWorld().getHeight();
        HEIGHT = board.HEIGHT;
        WIDTH = board.WIDTH;
        rectangles = new Rectangle[HEIGHT][WIDTH];
        pieces = new Circle[HEIGHT][WIDTH];

        this.graphics.createRectangle().setWidth(1920).setHeight(1080).setFillColor(0xc0c0c0);

        RECTANGLE_SIZE = VIEWER_HEIGHT / -~HEIGHT;
        FONT_SIZE = RECTANGLE_SIZE / 2;

        int START_X = VIEWER_WIDTH / 2 - RECTANGLE_SIZE * WIDTH / 2;
        for (int y = 0; y < HEIGHT; ++y) {
            for (int x = 0; x < WIDTH; ++x) {
                rectangles[y][x] = graphics.createRectangle().setFillColor(RECTANGLE_COLOR).setWidth(RECTANGLE_SIZE).setHeight(RECTANGLE_SIZE).setX(START_X + x * RECTANGLE_SIZE).setY((int)(RECTANGLE_SIZE / 2) + y * RECTANGLE_SIZE - FONT_SIZE / 2).setLineWidth(4);

                if(x == 0) {
                    int length = -~y < 10 ? 1 : (int)(Math.log10(x) + 1);
                    graphics.createText(Integer.toString(-~y)).setX(rectangles[y][x].getX() - (int)(RECTANGLE_SIZE / 1.25) + (int)(FONT_SIZE / length * .5)).setY(rectangles[y][x].getY() + (int)(FONT_SIZE / 2)).setFontFamily("Verdana").setFontSize(FONT_SIZE);
                }

                if(y == ~-HEIGHT) {
                    int length = Character.toString((char) (97+x)).length();
                    graphics.createText(Character.toString((char) (97+x))).setX(rectangles[y][x].getX() + (int)(FONT_SIZE / (length + .5))).setY(rectangles[y][x].getY() + RECTANGLE_SIZE + FONT_SIZE / 4).setFontFamily("Verdana").setFontSize(FONT_SIZE);
                }
            }

        }

        CIRCLE_RADIUS = (int)(RECTANGLE_SIZE * .40);
        GAP = (RECTANGLE_SIZE - CIRCLE_RADIUS * 2) / 2;
        // Create initial pieces ui
        for (Piece piece : board.pieces) {
            int y = piece.getY();
            int x = piece.getX();

            pieces[y][x] = graphics.createCircle().setRadius(CIRCLE_RADIUS).setX(rectangles[y][x].getX() + CIRCLE_RADIUS + GAP).setY(rectangles[y][x].getY() + CIRCLE_RADIUS + GAP).setFillColor(colors[piece.owner]).setLineWidth(4);
        }

        for (int i = 0; i < 2; ++i) {
            playerUIS[i] = new PlayerUI(gameManager.getPlayer(i), graphics, this);
        }
    }



    public void applyAction(Cell cell, int player) {
        playerUIS[player].group.setAlpha(1);
        playerUIS[player ^ 1].group.setAlpha(0.5);
        graphics.commitEntityState(0, playerUIS[player].group);
        graphics.commitEntityState(0, playerUIS[player ^ 1].group);

        for (int i = 0; i < 2; ++i)
            playerUIS[i].update();

        int y = cell.y;
        int x = cell.x;
        pieces[y][x] = graphics.createCircle().setRadius(CIRCLE_RADIUS).setX(rectangles[y][x].getX() + CIRCLE_RADIUS + GAP).setY(rectangles[y][x].getY() + CIRCLE_RADIUS + GAP).setFillColor(colors[player]).setLineWidth(4).setAlpha(0);

        graphics.commitEntityState(0, pieces[y][x]);
        pieces[y][x].setAlpha(1);
        graphics.commitEntityState(0.3, pieces[y][x]);

        // TODO MAYBE ADD A LINE ANIMATION FROM THE PLACE START TO THE FURTHEST CHIP IN A LINE
        ArrayList<ArrayList<Piece>> flips = board.flips;
        double timer = 0.3;
        double timePerChain = 0.6;
        for(ArrayList<Piece> row : flips) {
            int lastX = row.get(row.size()-1).getX();
            int lastY = row.get(row.size()-1).getY();
            int dirX = row.get(0).getX() - cell.x;
            int dirY = row.get(0).getY() - cell.y;

            Line line = graphics.createLine()
                    .setLineWidth(8)
                    .setX(pieces[cell.y][cell.x].getX())
                    .setY(pieces[cell.y][cell.x].getY())
                    .setX2(pieces[lastY+dirY][lastX+dirX].getX())
                    .setY2(pieces[lastY+dirY][lastX+dirX].getY())
                    .setLineColor(gameManager.getPlayer(player).getColorToken())
                    .setZIndex(2);

            graphics.commitEntityState(0.3, line);
            line.setVisible(false);
            graphics.commitEntityState(0.9, line);
            toggleModule.displayOnToggleState(line, "debugToggle", true);

            for(Piece piece : row) {
                y = piece.getY();
                x = piece.getX();
                Circle circle = pieces[y][x];

                if (circle.getFillColor() == colors[player]) continue;

                graphics.commitEntityState(timer, circle);

                circle.setSkewY(circle.getSkewY() + Math.toRadians(90));
                circle.setRadius((int)(CIRCLE_RADIUS / 1.5));
                graphics.commitEntityState(timer + timePerChain / 2 - 0.001, circle);
                circle.setFillColor(colors[player]);
                graphics.commitEntityState(timer + timePerChain / 2, circle);

                circle.setSkewY(circle.getSkewY() + Math.toRadians(90));
                circle.setRadius(CIRCLE_RADIUS);
                graphics.commitEntityState(timer + timePerChain, circle);
            }
        }
    }
}
