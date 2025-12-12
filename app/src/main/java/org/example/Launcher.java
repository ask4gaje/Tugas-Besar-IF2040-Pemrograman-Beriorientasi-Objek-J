package org.example;

import javafx.application.Application;
import javafx.stage.Stage;

import org.example.view.GameWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher extends Application{
    public static void main(String[] args) {

        GameManager.getInstance().initialize();
        launch(args); 
    }

    @Override
    public void start(Stage primaryStage) {
        new GameWindow(primaryStage);
    }
}
