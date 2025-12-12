package org.example.view;

import javafx.scene.layout.BorderPane;

public class GameView extends BorderPane {
    private final GamePanel panel;

    public GameView() {
        this.panel = new GamePanel();
        this.setCenter(panel.getCanvas());
    }

    public GamePanel getPanel() {
        return panel;
    }
}