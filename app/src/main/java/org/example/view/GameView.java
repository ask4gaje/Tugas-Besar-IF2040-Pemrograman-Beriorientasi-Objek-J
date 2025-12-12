package org.example.view;

import org.example.model.*;
import org.example.chef.Chef;
import org.example.GameManager;
import org.example.map.Tile;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.example.map.GameMap;
import org.example.map.WallTile;
import org.example.map.WalkableTile;
import org.example.map.station.*;
import org.example.chef.Direction;

public class GameView extends BorderPane {

    private Canvas canvas;
    private final int TILE_SIZE = 48; 

    public GameView() {
        canvas = new Canvas(14 * TILE_SIZE, 10 * TILE_SIZE);
        this.setCenter(canvas);
    }

    public void render() {
        GameManager manager = GameManager.getInstance(); 
        GraphicsContext g = canvas.getGraphicsContext2D();

        g.setFill(Color.GRAY);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawGrid(g, manager);
        drawChefs(g, manager);
    }

    private void drawGrid(GraphicsContext g, GameManager manager) {
        GameMap map = manager.getCurrentMap();
        if (map == null) return;

        for (int x = 0; x < 14; x++) {
            for (int y = 0; y < 10; y++) {
                Tile t = map.getTile(x, y);
                int sx = x * TILE_SIZE;
                int sy = y * TILE_SIZE;

                if (t instanceof WallTile) {
                    g.setFill(Color.DARKGRAY);
                } else if (t instanceof WalkableTile) {
                    g.setFill(Color.LIGHTGRAY);
                } else if (t instanceof CuttingStation) {
                    g.setFill(Color.BEIGE);
                } else if (t instanceof CookingStation) {
                    g.setFill(Color.ORANGE);
                } else if (t instanceof IngredientStorage) {
                    g.setFill(Color.CYAN);
                } else if (t instanceof ServingCounter) {
                    g.setFill(Color.YELLOW);
                } else if (t instanceof WashingStation) {
                    g.setFill(Color.SKYBLUE);
                } else {
                    g.setFill(Color.SADDLEBROWN); 
                }

                g.fillRect(sx, sy, TILE_SIZE, TILE_SIZE);
                g.setStroke(Color.BLACK);
                g.strokeRect(sx, sy, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void drawChefs(GraphicsContext g, GameManager manager) {
        if (manager.getChefs() == null) return;

        for (Chef c : manager.getChefs()) {
            double pixelX = c.getPosition().getX() * TILE_SIZE;
            double pixelY = c.getPosition().getY() * TILE_SIZE;

            g.setFill(c == manager.getActiveChef() ? Color.LIMEGREEN : Color.BLUE);
            g.fillOval(pixelX + 8, pixelY + 8, 32, 32);

            g.setFill(Color.BLACK);
            Direction facing = c.getDirection();
            
            if (facing != null) {
                switch(facing) {
                    case UP    -> g.fillOval(pixelX + 22, pixelY + 5, 6, 6);
                    case DOWN  -> g.fillOval(pixelX + 22, pixelY + 37, 6, 6);
                    case LEFT  -> g.fillOval(pixelX + 5, pixelY + 22, 6, 6);
                    case RIGHT -> g.fillOval(pixelX + 37, pixelY + 22, 6, 6);
                }
            }
        }
    }
}    