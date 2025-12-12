package org.example.view;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.GameController; // Import Controller

public class GameWindow {

    public GameWindow(Stage stage) {
        GameView view = new GameView();

        GameController controller = new GameController(view);

        Scene scene = new Scene(view);

        controller.input(scene);

        stage.setScene(scene);
        stage.setTitle("Nimonscooked");
        stage.setResizable(false);
        stage.show();
    }
}