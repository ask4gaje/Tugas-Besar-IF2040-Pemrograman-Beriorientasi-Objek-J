package com.burger.nimonscooked.view;

import com.burger.nimonscooked.model.*;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GamePanel implements GameModelListener {

    private final Canvas canvas;
    private final GameModel model;
    private final VBox hudBox;
    private final ListView<Order> orderList;

    public static final int TILE = 48;
    private final AnimationTimer loop;

    public GamePanel(GameModel model){
        this.model = model;
        this.model.addListener(this);

        int width = model.COLS * TILE;
        int height = model.ROWS * TILE;
        canvas = new Canvas(width, height);

        // HUD build
        hudBox = new VBox(10);
        hudBox.setPadding(new Insets(8));
        Label title = new Label("HUD");
        title.setFont(Font.font(16));
        Label timeLabel = new Label();
        timeLabel.textProperty().bind(Bindings.createStringBinding(() -> "Time: " + model.timeProperty().get() + "s", model.timeProperty()));
        Label scoreLabel = new Label();
        scoreLabel.textProperty().bind(Bindings.createStringBinding(() -> "Score: " + model.scoreProperty().get(), model.scoreProperty()));
        Label active = new Label();
        active.textProperty().bind(model.activeChefNameProperty().concat(" (active)"));

        orderList = new ListView<>(model.getOrders());
        orderList.setPrefWidth(260);
        orderList.setPrefHeight(300);

        hudBox.getChildren().addAll(title, timeLabel, scoreLabel, active, new Label("Orders:"), orderList);

        // animation loop
        loop = new AnimationTimer() {
            @Override
            public void handle(long now) { draw(); }
        };
        loop.start();
    }

    public Canvas getCanvas(){ return canvas; }
    public VBox getHUD(){ return hudBox; }

    @Override
    public void onModelChanged() {
        // model changes cause redraw on next frame; nothing is needed here
    }

    private void draw(){
        GraphicsContext g = canvas.getGraphicsContext2D();
        // clear
        g.setFill(Color.web("#222"));
        g.fillRect(0,0,canvas.getWidth(), canvas.getHeight());

        drawGrid(g);
        drawItems(g);
        drawChefs(g);
        drawProgressBars(g);
    }

    private void drawGrid(GraphicsContext g){
        Tile[][] grid = model.getGrid();
        for (int x=0;x<model.COLS;x++){
            for (int y=0;y<model.ROWS;y++){
                Tile t = grid[x][y];
                double sx = x * TILE, sy = y * TILE;
                switch (t.type){
                    case FLOOR -> g.setFill(Color.web("#b8d8a7"));
                    case WALL -> g.setFill(Color.web("#444"));
                    case CUT -> g.setFill(Color.web("#f2c57c"));
                    case COOK -> g.setFill(Color.web("#f27c7c"));
                    case INGREDIENT -> g.setFill(Color.web("#c2e0ff"));
                    case SERVE -> g.setFill(Color.web("#ffd88a"));
                    case ASSEMBLE -> g.setFill(Color.web("#67b87c"));
                    case WASH -> g.setFill(Color.web("#cd1846"));
                    case PLATE -> g.setFill(Color.web("#f47c36"));
                    case TRASH -> g.setFill(Color.web("#553f0e"));
                    case SPAWN -> g.setFill(Color.web("#054a4a"));
                }
                g.fillRect(sx, sy, TILE, TILE);
                g.setStroke(Color.color(0,0,0,0.15));
                g.strokeRect(sx, sy, TILE, TILE);
            }
        }
    }

    private void drawItems(GraphicsContext g){
        Tile[][] grid = model.getGrid();
        for (int x=0;x<model.COLS;x++){
            for (int y=0;y<model.ROWS;y++){
                Tile t = grid[x][y];
                if (t.item != null) {
                    double sx = x*TILE, sy = y*TILE;
                    g.setFill(Color.SADDLEBROWN);
                    g.fillOval(sx + 10, sy + 10, TILE - 20, TILE - 20);
                }
            }
        }
    }

    private void drawChefs(GraphicsContext g){
        for (Chef c : model.getChefs()){
            double sx = c.x * TILE, sy = c.y * TILE;
            g.setFill(c == model.getActiveChef() ? Color.LIGHTGREEN : Color.LIGHTBLUE);
            g.fillRoundRect(sx+6, sy+6, TILE-12, TILE-12, 8,8);
            g.setFill(Color.BLACK);
            switch (c.facing){
                case UP -> g.fillOval(sx + TILE/2 -4, sy + 10, 8,8);
                case DOWN -> g.fillOval(sx + TILE/2 -4, sy + TILE - 18, 8,8);
                case LEFT -> g.fillOval(sx + 10, sy + TILE/2 -4, 8,8);
                case RIGHT -> g.fillOval(sx + TILE -18, sy + TILE/2 -4, 8,8);
            }
            if (c.holding != null){
                g.setFill(Color.WHITESMOKE);
                g.fillRect(sx+10, sy+TILE-16, TILE-20, 6);
            }
        }
    }

    private void drawProgressBars(GraphicsContext g){
        for (Chef c : model.getChefs()){
            double p = model.getProgress(c.name).get();
            if (p > 0 && p < 1.0){
                double x = c.x * TILE;
                double y = c.y * TILE - 10;
                double bw = TILE - 8;
                g.setFill(Color.gray(0.15, 0.9));
                g.fillRect(x+4, y, bw, 6);
                g.setFill(Color.LIME);
                g.fillRect(x+5, y+1, (bw-2)*p, 4);
                g.setStroke(Color.BLACK);
                g.strokeRect(x+4, y, bw, 6);
            }
        }
    }
}

