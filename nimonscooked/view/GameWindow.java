package com.burger.nimonscooked.view;

import com.burger.nimonscooked.controller.GameController;
import com.burger.nimonscooked.model.GameModel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameWindow extends Application {

    @Override
    public void start(Stage stage) {
        GameModel model = new GameModel();
        GameView view = new GameView();
        GameController controller = new GameController(model, view);

        Scene scene = new Scene(view);
        controller.input(scene);

        stage.setScene(scene);
        stage.setTitle("NimonsCooked");
        stage.show();
    }
}
