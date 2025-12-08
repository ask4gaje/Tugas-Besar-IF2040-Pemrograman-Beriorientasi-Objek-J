package com.burger.nimonscooked.view;

import com.burger.nimonscooked.model.*;
import com.burger.nimonscooked.model.Chef;
import com.burger.nimonscooked.model.GameModel;
import com.burger.nimonscooked.model.Tile;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class GameView extends BorderPane {

    private Canvas canvas = new Canvas(800, 600);

    public GameView() {
        this.setCenter(canvas);
    }

    public void render(GameModel model) {
        GraphicsContext g = canvas.getGraphicsContext2D();

        g.setFill(Color.GRAY);
        g.fillRect(0, 0, 800, 600);

        drawGrid(g, model);
        drawChefs(g, model);
        drawProgressBars(g, model);
    }

    private void drawGrid(GraphicsContext g, GameModel model) {
        Tile[][] grid = model.getGrid();
        int s = 48;

        for (int x=0;x<model.COLS;x++){
            for(int y=0;y<model.ROWS;y++) {
                Tile t = grid[x][y];

                switch (t.type) {
                    case FLOOR -> g.setFill(Color.LIGHTGRAY);
                    case WALL -> g.setFill(Color.DARKGRAY);
                    case CUT -> g.setFill(Color.BEIGE);
                    case COOK -> g.setFill(Color.ORANGE);
                    case INGREDIENT -> g.setFill(Color.CYAN);
                    case SERVE -> g.setFill(Color.YELLOW);
                }

                g.fillRect(x*s, y*s, s, s);
                g.setStroke(Color.BLACK);
                g.strokeRect(x*s, y*s, s, s);
            }
        }
    }

    private void drawChefs(GraphicsContext g, GameModel model) {
        int s = 48;

        for (Chef c : model.getChefs()) {
            g.setFill(c == model.getActiveChef() ? Color.GREEN : Color.BLUE);
            g.fillOval(c.x*s + 8, c.y*s + 8, 32, 32);

            // Facing indicator
            g.setFill(Color.BLACK);
            switch(c.facing) {
                case UP -> g.fillOval(c.x*s+22, c.y*s+5, 6, 6);
                case DOWN -> g.fillOval(c.x*s+22, c.y*s+37, 6, 6);
                case LEFT -> g.fillOval(c.x*s+5, c.y*s+22, 6, 6);
                case RIGHT -> g.fillOval(c.x*s+37, c.y*s+22, 6, 6);
            }
        }
    }

    private void drawProgressBars(GraphicsContext g, GameModel model) {
        int s = 48;

        for (Chef c : model.getChefs()) {
            double p = model.getProgress(c.name).get();
            if (p > 0 && p < 1) {
                double x = c.x*s;
                double y = c.y*s - 8;

                g.setFill(Color.BLACK);
                g.fillRect(x, y, s, 6);
                g.setFill(Color.LIMEGREEN);
                g.fillRect(x+1, y+1, (s-2) * p, 4);
            }
        }
    }
}
