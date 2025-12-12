package org.example.controller;

import org.example.GameManager;
import org.example.chef.Direction;
import org.example.view.GamePanel;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class GameController {

    private final GameManager manager;

    public GameController(GamePanel view) {
        this.manager = GameManager.getInstance();
    }


    public void input(Scene scene) {
        scene.setOnKeyPressed(e -> {
            KeyCode k = e.getCode();
            
            if (manager.getActiveChef() == null) return;

            switch(k) {
                case W -> manager.moveChef(Direction.UP);
                case A -> manager.moveChef(Direction.LEFT);
                case S -> manager.moveChef(Direction.DOWN);
                case D -> manager.moveChef(Direction.RIGHT);
                case V -> manager.interact();
                case C -> System.out.println("Pick/Drop (Belum implementasi)");
                case B -> manager.switchChef();
            }
        });
    }
}