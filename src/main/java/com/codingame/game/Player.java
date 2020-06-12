package com.codingame.game;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;

public class Player extends AbstractMultiplayerPlayer {
    Player opponent;
    String lastAction = "";
    String message = "";
    @Override
    public int getExpectedOutputLines() {
        return 1;
    }

    public String getLastAction() {
        return lastAction;
    }

    public String getMessage() {
        return message;
    }
}
