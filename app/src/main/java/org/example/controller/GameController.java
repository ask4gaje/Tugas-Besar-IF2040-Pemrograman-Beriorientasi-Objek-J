package org.example.controller;

import org.example.GameManager;
import org.example.chef.Direction;
import org.example.view.GameView;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class GameController {

    private final GameManager manager;
    private final GameView view;

    public GameController(GameView view) {
        this.manager = GameManager.getInstance();
        this.view = view;
        startLoop();
    }

    private void startLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                view.render(); 
            }
        }.start();
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
                
                case C -> System.out.println("Pick/Drop (Belum implementasi)");
                
                case B -> manager.switchChef();
            }
        });
    }
}