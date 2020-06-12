package com.codingame.game;
import java.util.List;
import java.util.Random;

import com.codingame.game.othello.Board;
import com.codingame.game.othello.Cell;
import com.codingame.game.othello.Viewer;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.endscreen.EndScreenModule;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.toggle.ToggleModule;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {
    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphics;
    @Inject private TooltipModule tooltips;
    @Inject private ToggleModule toggleModule;
    @Inject private EndScreenModule endScreenModule;

    Board board;
    Viewer viewer;
    Player currentPlayer;
    boolean wonByTimeout = false;
    List<Cell> legalActions;

    @Override
    public void init() {
        board = new Board();
        viewer = new Viewer(graphics, board, gameManager, toggleModule);
        currentPlayer = gameManager.getPlayer(0);
        gameManager.getPlayer(0).opponent = gameManager.getPlayer(1);
        gameManager.getPlayer(1).opponent = gameManager.getPlayer(0);

        legalActions = board.getActions(currentPlayer.getIndex());
        gameManager.setMaxTurns(board.getHEIGHT() * board.getWIDTH() - 4);
        // TODO CHANGE THIS
        gameManager.setFirstTurnMaxTime(2000);
        gameManager.setTurnMaxTime(150);
        gameManager.setFrameDuration(1200);
    }

    @Override
    public void gameTurn(int turn) {

        try {
            sendInputs(turn);
            currentPlayer.execute();
            String[] outputs = currentPlayer.getOutputs().get(0).split("MSG");
            outputs[0] = outputs[0].replaceAll("\\s", "").toLowerCase();

            if (outputs.length > 1) {
                outputs[1] = outputs[1].replaceFirst("MSG\\s","");
                outputs[1] = outputs[1].substring(0, Math.min(outputs[1].length(), 12));
                currentPlayer.message = outputs[1];
            }

            boolean found = false;
            for (Cell cell : legalActions) {
                if(cell.toString().equals(outputs[0])) {
                    found = true;
                    currentPlayer.lastAction = cell.toString();
                    board.applyAction(cell, currentPlayer.getIndex());
                    viewer.applyAction(cell, currentPlayer.getIndex());
                    break;
                }
            }
            if(!found) {
                throw new InvalidAction(String.format("Action was not valid!"));
            }
        } catch (TimeoutException e) {
            gameManager.addToGameSummary(gameManager.formatErrorMessage(currentPlayer.getNicknameToken() + " did not output in time!"));
            currentPlayer.deactivate(currentPlayer.getNicknameToken() + " timeout.");
            currentPlayer.setScore(-1);
            gameManager.endGame();
            wonByTimeout = true;
            return;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | InvalidAction e) {
            gameManager.addToGameSummary(gameManager.formatErrorMessage(currentPlayer.getNicknameToken() + " made an invalid action!"));
            currentPlayer.deactivate(currentPlayer.getNicknameToken() + " made an invalid action.");
            currentPlayer.setScore(-1);
            gameManager.endGame();
            wonByTimeout = true;
            return;
        }

        int[] counter = board.getPiecesCounter();
        // Check if opponent has no pieces
        if(counter[currentPlayer.opponent.getIndex()] == 0) {
            currentPlayer.setScore(1);
            gameManager.endGame();
            return;
        } else if(turn == gameManager.getMaxTurns()) {
            setWinner();
            return;
        }

        List<Cell> actions = board.getActions(currentPlayer.opponent.getIndex());
        if(actions.size() > 0) {
            currentPlayer = currentPlayer.opponent;
            legalActions = actions;
        } else {
            actions = board.getActions(currentPlayer.getIndex());
            if(actions.size() > 0) {
                legalActions = actions;
                gameManager.addTooltip(currentPlayer.opponent, currentPlayer.opponent.getNicknameToken() + " pass");
                gameManager.addToGameSummary(gameManager.formatErrorMessage(currentPlayer.opponent.getNicknameToken() + " had to pass."));
                currentPlayer.opponent.lastAction = "pass";
            } else {
                setWinner();
                gameManager.endGame();
            }
        }
    }

    public void setWinner() {
        int[] counter = board.getPiecesCounter();
        if(counter[0] > counter[1]) {
            gameManager.getPlayer(0).setScore(1);
            gameManager.endGame();
        } else if(counter[0] < counter[1]) {
            gameManager.getPlayer(1).setScore(1);
            gameManager.endGame();
        } else {
            gameManager.endGame();
            gameManager.endGame();
        }
    }

    public void sendInputs(int turn) {
        if(turn <= 2) {
            currentPlayer.sendInputLine(Integer.toString(currentPlayer.getIndex()));
        }

        //Board
        currentPlayer.sendInputLine(Integer.toString(board.getHEIGHT()));
        Cell[][] cells = board.getCells();
        for(int y = 0; y < board.getHEIGHT(); ++y) {
            String s = "";
            for (int x = 0; x < board.getWIDTH(); ++x) {
                if(cells[y][x].getPiece() != null) {
                    s += cells[y][x].getPiece().getOwner();
                } else {
                    s += ".";
                }
            }
            currentPlayer.sendInputLine(s);
        }

        // Legal actions
        currentPlayer.sendInputLine(Integer.toString(legalActions.size()));
        for (Cell legalAction : legalActions) {
            currentPlayer.sendInputLine(legalAction.toString());
        }
    }


    @Override
    public void onEnd() {
        int[] scores = { gameManager.getPlayer(0).getScore(), gameManager.getPlayer(1).getScore() };
        String[] text = new String[2];
        if(scores[0] > scores[1]) {
            gameManager.addToGameSummary(gameManager.formatErrorMessage(gameManager.getPlayer(0).getNicknameToken() + " won"));
            gameManager.addTooltip(gameManager.getPlayer(0), gameManager.getPlayer(0).getNicknameToken() + " won");
            text[0] = "Won";
            text[1] = "Lost";
        } else if(scores[1] > scores[0]) {
            gameManager.addToGameSummary(gameManager.formatErrorMessage(gameManager.getPlayer(1).getNicknameToken() + " won"));
            gameManager.addTooltip(gameManager.getPlayer(1), gameManager.getPlayer(1).getNicknameToken() + " won");
            text[0] = "Lost";
            text[1] = "Won";
        } else {
            gameManager.addToGameSummary(gameManager.formatErrorMessage("draw"));
            text[0] = "Draw";
            text[1] = "Draw";
        }
        text[0] += (wonByTimeout ? "" : " pieces " + board.getPiecesCounter()[0]);
        text[1] += (wonByTimeout ? "" : " pieces " + board.getPiecesCounter()[1]);

        endScreenModule.setScores(scores, text);
    }
}
