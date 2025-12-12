package org.example.view;

import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import org.example.GameManager;
import org.example.map.GameMap;
import org.example.map.Tile;
import org.example.map.WallTile;
import org.example.map.WalkableTile;
import org.example.map.station.*;
import org.example.chef.Chef;
import org.example.chef.Direction;

public class GamePanel extends BorderPane {

    private final Canvas canvas;
    private final GameManager manager;
    private final VBox hudBox;

    public static final int TILE = 48;
    private final AnimationTimer loop;

    public GamePanel() {
        this.manager = GameManager.getInstance();

        int width = 14 * TILE;
        int height = 10 * TILE;
        canvas = new Canvas(width, height);

        hudBox = new VBox(10);
        hudBox.setPadding(new Insets(8));
        hudBox.setStyle("-fx-background-color: #ddd;");
        
        Label title = new Label("HUD");
        title.setFont(Font.font(16));

        Label timeLabel = new Label();
        timeLabel.textProperty().bind(Bindings.createStringBinding(() -> "Time: " + manager.timeProperty().get() + "s", manager.timeProperty()));
        
        Label scoreLabel = new Label();
        scoreLabel.textProperty().bind(Bindings.createStringBinding(() -> "Score: " + manager.scoreProperty().get(), manager.scoreProperty()));
        
        Label active = new Label();
        active.textProperty().bind(manager.activeChefNameProperty().concat(" (active)"));

        hudBox.getChildren().addAll(title, timeLabel, scoreLabel, active);

        this.setCenter(canvas);
        this.setRight(hudBox);

        loop = new AnimationTimer() {
            @Override
            public void handle(long now) { draw(); }
        };
        loop.start();
    }

    private void draw() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.web("#222"));
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawGrid(g);
        drawItems(g);
        drawChefs(g);
    }

    private void drawGrid(GraphicsContext g) {
        GameMap map = manager.getCurrentMap();
        if (map == null) return;

        for (int x = 0; x < 14; x++) {
            for (int y = 0; y < 10; y++) {
                Tile t = map.getTile(x, y);
                double sx = x * TILE, sy = y * TILE;

                if (t instanceof WallTile) {
                    g.setFill(Color.web("#444"));
                } else if (t instanceof WalkableTile) {
                    g.setFill(Color.web("#b8d8a7"));
                } else if (t instanceof CuttingStation) {
                    g.setFill(Color.web("#f2c57c"));
                } else if (t instanceof CookingStation) {
                    g.setFill(Color.web("#f27c7c"));
                } else if (t instanceof IngredientStorage) {
                    g.setFill(Color.web("#c2e0ff"));
                } else if (t instanceof ServingCounter) {
                    g.setFill(Color.web("#ffd88a"));
                } else if (t instanceof WashingStation) {
                    g.setFill(Color.CYAN);
                } else {
                    g.setFill(Color.SADDLEBROWN);
                }

                g.fillRect(sx, sy, TILE, TILE);
                g.setStroke(Color.color(0, 0, 0, 0.15));
                g.strokeRect(sx, sy, TILE, TILE);
            }
        }
    }

    private void drawItems(GraphicsContext g) {
        GameMap map = manager.getCurrentMap();
        if (map == null) return;

        for (int x = 0; x < 14; x++) {
            for (int y = 0; y < 10; y++) {
                Tile t = map.getTile(x, y);
                if (t.getItemOnTile() != null) {
                    double sx = x * TILE, sy = y * TILE;
                    g.setFill(Color.SADDLEBROWN);
                    g.fillOval(sx + 10, sy + 10, TILE - 20, TILE - 20);
                }
            }
        }
    }

    private void drawChefs(GraphicsContext g) {
        if (manager.getChefs() == null) return;

        for (Chef c : manager.getChefs()) {
            double sx = c.getPosition().getX() * TILE;
            double sy = c.getPosition().getY() * TILE;

            g.setFill(c == manager.getActiveChef() ? Color.LIGHTGREEN : Color.LIGHTBLUE);
            g.fillRoundRect(sx + 6, sy + 6, TILE - 12, TILE - 12, 8, 8);
            
            g.setFill(Color.BLACK);
            
            Direction facing = c.getDirection();
            if (facing != null) {
                switch (facing) {
                    case UP -> g.fillOval(sx + TILE / 2 - 4, sy + 10, 8, 8);
                    case DOWN -> g.fillOval(sx + TILE / 2 - 4, sy + TILE - 18, 8, 8);
                    case LEFT -> g.fillOval(sx + 10, sy + TILE / 2 - 4, 8, 8);
                    case RIGHT -> g.fillOval(sx + TILE - 18, sy + TILE / 2 - 4, 8, 8);
                }
            }

            if (c.getInventory() != null) {
                g.setFill(Color.WHITESMOKE);
                g.fillRect(sx + 10, sy + TILE - 16, TILE - 20, 6);
            }
        }
    }
}