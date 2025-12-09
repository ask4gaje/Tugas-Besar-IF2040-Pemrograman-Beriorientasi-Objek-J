package com.burger.nimonscooked.controller;

import com.burger.nimonscooked.model.*;
import com.burger.nimonscooked.model.Direction;
import com.burger.nimonscooked.model.GameModel;
import com.burger.nimonscooked.view.GameView;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class GameController {

    private GameModel model;
    private GameView view;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
        startLoop();
    }

    private void startLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                view.render(model);
            }
        }.start();
    }

    public void input(Scene scene) {
        scene.setOnKeyPressed(e -> {
            KeyCode k = e.getCode();
            switch(k) {
                case W -> model.move(Direction.UP);
                case A -> model.move(Direction.LEFT);
                case S -> model.move(Direction.DOWN);
                case D -> model.move(Direction.RIGHT);
                case V -> model.interact();
                case C -> System.out.println("Pick/Drop");
                case B -> model.switchChef();
            }
        });
    }
}
