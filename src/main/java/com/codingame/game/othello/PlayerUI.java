package com.codingame.game.othello;

import com.codingame.game.Player;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Text;

public class PlayerUI {
    Player owner;
    Group group;
    Text last;
    Text pieceChange;
    Text pieceCount;
    Viewer viewer;
    Text message;

    PlayerUI(Player player, GraphicEntityModule graphics, Viewer viewer) {
        int startX = player.getIndex()  == 0 ? 200 : viewer.VIEWER_WIDTH - 200;
        int startY = 100;
        this.owner = player;
        this.viewer = viewer;
        group = graphics.createGroup();


        group.add(graphics.createRoundedRectangle().setY(startY - 20).setX(startX - 150).setWidth(300).setHeight(700).setLineWidth(4).setFillColor(0x696969));
        group.add(graphics.createText(player.getNicknameToken()).setX(startX).setY(startY).setFontSize(36).setFontFamily("Verdana").setFontWeight(Text.FontWeight.BOLD).setAnchorX(0.5).setFillColor(player.getColorToken()));
        group.add(graphics.createSprite().setImage(player.getAvatarToken()).setX(startX).setY(startY + 100).setAnchorX(0.5).setBaseWidth(200).setBaseHeight(200));
        group.add(last = graphics.createText(player.getLastAction()).setX(startX).setY(startY + 350).setFontSize(54).setFontFamily("Verdana").setFontWeight(Text.FontWeight.BOLD).setAnchorX(0.5));
        group.add(graphics.createCircle().setRadius(viewer.CIRCLE_RADIUS + 10).setFillColor(viewer.colors[player.getIndex()]).setX(startX - 60).setY(startY + 510).setLineWidth(4));
        group.add(pieceCount = graphics.createText(Integer.toString(viewer.board.piecesCounter[player.getIndex()])).setX(startX + 60).setY(startY + 465 - viewer.CIRCLE_RADIUS / 2).setAnchorX(0.5).setFontSize(54).setFontFamily("Verdana").setFontWeight(Text.FontWeight.BOLD).setAnchorX(0.5));
        group.add(pieceChange = graphics.createText("").setX(startX + 60).setY(startY + 520).setFontSize(44).setFontFamily("Verdana").setAnchorX(0.5));
        group.add(message = graphics.createText("").setX(startX - 5).setY(startY + 600).setFontSize(40).setFontFamily("Verdana").setAnchorX(0.5));

    }

    void update() {
        if(owner.getLastAction() != last.getText())
            last.setText(owner.getLastAction());

        pieceChange.setText((Integer.parseInt(pieceCount.getText()) > viewer.board.getPiecesCounter()[owner.getIndex()] ? "-" : "+") + Math.abs(viewer.board.getPiecesCounter()[owner.getIndex()] - (Integer.parseInt(pieceCount.getText()))));

            pieceCount.setText(Integer.toString(viewer.board.getPiecesCounter()[owner.getIndex()]));

        if(owner.getMessage() != message.getText()) {
            message.setText(owner.getMessage());
        }

        viewer.graphics.commitEntityState(0.15, last);
        viewer.graphics.commitEntityState(0.15, message);

        viewer.graphics.commitEntityState(0.45, pieceCount);
        viewer.graphics.commitEntityState(0.45, pieceChange);
    }
}
