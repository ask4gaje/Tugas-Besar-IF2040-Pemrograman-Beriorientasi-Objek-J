package org.example.view;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.GameController; // Import Controller

public class GameWindow {

    public GameWindow(Stage stage) {
        GamePanel root = new GamePanel();
        GameController controller = new GameController(root);

        Scene scene = new Scene(root);

        controller.input(scene);

        stage.setScene(scene);
        stage.setTitle("Nimonscooked");
        stage.setResizable(false);
        stage.show();
    }
}