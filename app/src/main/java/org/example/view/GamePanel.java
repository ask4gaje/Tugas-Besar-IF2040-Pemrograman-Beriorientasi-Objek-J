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
import javafx.scene.control.ListView;
import javafx.scene.image.Image;

import org.example.GameManager;
import org.example.map.GameMap;
import org.example.map.Tile;
import org.example.map.WallTile;
import org.example.map.WalkableTile;
import org.example.map.station.*;
import org.example.chef.Chef;
import org.example.chef.Direction;
import org.example.model.Order;
import org.example.item.*;

import java.util.HashMap;
import java.util.Map;

public class GamePanel extends BorderPane {

    private final Canvas canvas;
    private final GameManager manager;
    private final VBox hudBox;
    private final ListView<Order> orderList;

    public static final int TILE = 64;
    private final AnimationTimer loop;

    private final Map<String, Image> tileImages = new HashMap<>();
    private final Map<String, Image> itemImages = new HashMap<>();
    private final Map<Direction, Image> chef1Images = new HashMap<>();
    private final Map<Direction, Image> chef2Images = new HashMap<>();

    public GamePanel() {
        this.manager = GameManager.getInstance();

        int width = 14 * TILE;
        int height = 10 * TILE;
        canvas = new Canvas(width, height);
        this.setCenter(canvas);

        hudBox = new VBox(10);
        hudBox.setPadding(new Insets(10));
        hudBox.setStyle("-fx-background-color: #6dd4d2ff;");
        hudBox.setPrefWidth(300);
        
        Label title = new Label("Nimonscooked");
        title.setFont(Font.font("Arial", 20));

        Label timeLabel = new Label();
        timeLabel.textProperty().bind(Bindings.concat("Time: ", manager.timeProperty(), "s"));
        timeLabel.setFont(Font.font(16));
        
        Label scoreLabel = new Label();
        scoreLabel.textProperty().bind(Bindings.concat("Score: ", manager.scoreProperty()));
        scoreLabel.setFont(Font.font(16));
        
        Label active = new Label();
        active.textProperty().bind(manager.activeChefNameProperty().concat(" (active)"));
        active.setTextFill(Color.BLUE);

        orderList = new ListView<>(manager.getOrders());
        orderList.setPrefHeight(300);

        hudBox.getChildren().addAll(title, timeLabel, scoreLabel, active, new Label("Orders:"), orderList);

        this.setRight(hudBox);
        loadImages();

        loop = new AnimationTimer() {
            @Override
            public void handle(long now) { draw(); }
        };
        loop.start();
    }

    private void loadImages() {
        try {
            tileImages.put("FLOOR", new Image(getClass().getResourceAsStream("/asset/tile/tile_FLOOR.png")));
            tileImages.put("WALL", new Image(getClass().getResourceAsStream("/asset/tile/tile_WALL.png")));
            tileImages.put("CUT", new Image(getClass().getResourceAsStream("/asset/tile/station/tile_CUTTING.png")));
            tileImages.put("COOK", new Image(getClass().getResourceAsStream("/asset/tile/station/tile_COOKING.png")));
            tileImages.put("SERVE", new Image(getClass().getResourceAsStream("/asset/tile/station/tile_SERVING.png")));
            tileImages.put("WASH", new Image(getClass().getResourceAsStream("/asset/tile/station/tile_WASHING.png")));
            tileImages.put("ASSEMBLE", new Image(getClass().getResourceAsStream("/asset/tile/station/tile_ASSEMBLY.png")));
            tileImages.put("PLATE", new Image(getClass().getResourceAsStream("/asset/tile/station/tile_PLATE.png")));
            tileImages.put("TRASH", new Image(getClass().getResourceAsStream("/asset/tile/station/tile_TRASH.png")));
            tileImages.put("BUN STORAGE", new Image(getClass().getResourceAsStream("/asset/tile/station/tile_Roti_storage.png")));
            tileImages.put("MEAT STORAGE", new Image(getClass().getResourceAsStream("/asset/tile/station/tile_Daging_storage.png")));
            tileImages.put("CHEESE STORAGE", new Image(getClass().getResourceAsStream("/asset/tile/station/tile_Keju_storage.png")));
            tileImages.put("LETTUCE STORAGE", new Image(getClass().getResourceAsStream("/asset/tile/station/tile_Lettuce_storage.png")));
            tileImages.put("TOMATO STORAGE", new Image(getClass().getResourceAsStream("/asset/tile/station/tile_Tomat_storage.png")));

            
            chef1Images.put(Direction.UP, new Image(getClass().getResourceAsStream("/asset/chef/chef1_UP.png")));
            chef1Images.put(Direction.DOWN, new Image(getClass().getResourceAsStream("/asset/chef/chef1_DOWN.png")));
            chef1Images.put(Direction.LEFT, new Image(getClass().getResourceAsStream("/asset/chef/chef1_LEFT.png")));
            chef1Images.put(Direction.RIGHT, new Image(getClass().getResourceAsStream("/asset/chef/chef1_RIGHT.png")));

            chef2Images.put(Direction.UP, new Image(getClass().getResourceAsStream("/asset/chef/chef2_UP.png")));
            chef2Images.put(Direction.DOWN, new Image(getClass().getResourceAsStream("/asset/chef/chef2_DOWN.png")));
            chef2Images.put(Direction.LEFT, new Image(getClass().getResourceAsStream("/asset/chef/chef2_LEFT.png")));
            chef2Images.put(Direction.RIGHT, new Image(getClass().getResourceAsStream("/asset/chef/chef2_RIGHT.png")));

            itemImages.put("Roti", new Image(getClass().getResourceAsStream("/asset/item/ingredient/roti_RAW.png")));
            
            itemImages.put("Daging Raw", new Image(getClass().getResourceAsStream("/asset/item/ingredient/daging_RAW.png")));
            itemImages.put("Daging Chopped", new Image(getClass().getResourceAsStream("/asset/item/ingredient/daging_CHOPPED.png")));
            itemImages.put("Daging Cooking", new Image(getClass().getResourceAsStream("/asset/item/ingredient/daging_COOKING.png")));
            itemImages.put("Daging Cooked", new Image(getClass().getResourceAsStream("/asset/item/ingredient/daging_COOKED.png")));
            itemImages.put("Daging Burned", new Image(getClass().getResourceAsStream("/asset/item/ingredient/daging_BURNED.png")));

            itemImages.put("Keju Raw", new Image(getClass().getResourceAsStream("/asset/item/ingredient/keju_RAW.png")));
            itemImages.put("Keju Chopped", new Image(getClass().getResourceAsStream("/asset/item/ingredient/keju_CHOPPED.png")));

            itemImages.put("Lettuce Raw", new Image(getClass().getResourceAsStream("/asset/item/ingredient/lettuce_RAW.png")));
            itemImages.put("Lettuce Chopped", new Image(getClass().getResourceAsStream("/asset/item/ingredient/lettuce_CHOPPED.png")));

            itemImages.put("Tomat Raw", new Image(getClass().getResourceAsStream("/asset/item/ingredient/tomat_RAW.png")));
            itemImages.put("Tomat Chopped", new Image(getClass().getResourceAsStream("/asset/item/ingredient/tomat_CHOPPED.png")));


            itemImages.put("Classic Burger", new Image(getClass().getResourceAsStream("/asset/item/menu/classicburger.png")));
            itemImages.put("CheeseBurger", new Image(getClass().getResourceAsStream("/asset/item/menu/cheeseburger.png")));
            itemImages.put("BLT Burger", new Image(getClass().getResourceAsStream("/asset/item/menu/bltburger.png")));
            itemImages.put("Deluxe Burger", new Image(getClass().getResourceAsStream("/asset/item/menu/deluxeburger.png")));

            itemImages.put("Classic Burger Dish", new Image(getClass().getResourceAsStream("/asset/item/dish/dish_classicburger.png")));
            itemImages.put("CheeseBurger Dish", new Image(getClass().getResourceAsStream("/asset/item/dish/dish_cheeseburger.png")));
            itemImages.put("BLT Burger Dish", new Image(getClass().getResourceAsStream("/asset/item/dish/dish_bltburger.png")));
            itemImages.put("Deluxe Burger Dish", new Image(getClass().getResourceAsStream("/asset/item/dish/dish_deluxeburger.png")));

            itemImages.put("Plate", new Image(getClass().getResourceAsStream("/asset/item/utensil/plate.png")));
            itemImages.put("Frying Pan", new Image(getClass().getResourceAsStream("/asset/item/utensil/fryingpan.png")));

            
        } catch (Exception e) {
            System.err.println("Warning: Beberapa gambar gagal dimuat. Menggunakan fallback warna.");
        }
    }

    private String getImageKey(Item item) {
        if (item == null) return null;

        if (item instanceof Ingredient) {
            Ingredient ing = (Ingredient) item;
            
            String typeLabel = ing.getType().label; 
            
            if (typeLabel.equalsIgnoreCase("Roti")) {
                return "Roti";
            }

            String state = ing.getState().toString(); 
        
            String stateFormatted = state.charAt(0) + state.substring(1).toLowerCase();
            
            return typeLabel + " " + stateFormatted;
        }

        return item.getName();
    }

    public Canvas getCanvas(){ return canvas; }
    public VBox getHUD(){ return hudBox; }

    private void draw() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.web("#222"));
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawGrid(g);
        drawItems(g);
        drawChefs(g);
        drawProgressBars(g);
    }

    private void drawGrid(GraphicsContext g) {
        GameMap map = manager.getCurrentMap();
        if (map == null) return;

        for (int x = 0; x < 14; x++) {
            for (int y = 0; y < 10; y++) {
                Tile t = map.getTile(x, y);
                double sx = x * TILE;
                double sy = y * TILE;

                Image img = null;
                Color fallbackColor = Color.MAGENTA;

                if (t instanceof WallTile) {
                    img = tileImages.get("WALL"); 
                    fallbackColor = Color.DARKGRAY;
                } else if (t instanceof WalkableTile) {
                    img = tileImages.get("FLOOR"); 
                    fallbackColor = Color.LIGHTGRAY;
                } else if (t instanceof CuttingStation) {
                    img = tileImages.get("CUT"); 
                    fallbackColor = Color.ORANGE;
                } else if (t instanceof CookingStation) {
                    img = tileImages.get("COOK"); 
                    fallbackColor = Color.RED;
                } else if (t instanceof IngredientStorage ingredientStorage) {
                    switch (ingredientStorage.getStorageType()) {
                        case BUN -> img = tileImages.get("BUN STORAGE");
                        case MEAT -> img = tileImages.get("MEAT STORAGE");
                        case CHEESE -> img = tileImages.get("CHEESE STORAGE");
                        case LETTUCE -> img = tileImages.get("LETTUCE STORAGE");
                        case TOMATO -> img = tileImages.get("TOMATO STORAGE");
                        default -> img = tileImages.get("INGREDIENT"); // Fallback to generic image
                    }
                } else if (t instanceof ServingCounter) {
                    img = tileImages.get("SERVE"); 
                    fallbackColor = Color.GOLD;
                } else if (t instanceof WashingStation) {
                    img = tileImages.get("WASH"); 
                    fallbackColor = Color.SKYBLUE;
                }  else if (t instanceof PlateStorage) { 
                    img = tileImages.get("PLATE");
                    fallbackColor = Color.WHITE;
                } else if (t instanceof TrashStation) { 
                    img = tileImages.get("TRASH");
                    fallbackColor = Color.BLACK;
                } else if (t instanceof AssemblyStation) { 
                    img = tileImages.get("ASSEMBLE");
                    fallbackColor = Color.LIGHTGREEN;
                }

                if (img != null) {
                    g.drawImage(img, sx, sy, TILE, TILE);
                } else {
                    g.setFill(fallbackColor);
                    g.fillRect(sx, sy, TILE, TILE);
                }

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
                    String imageKey = getImageKey(t.getItemOnTile());
                    Image img = itemImages.get(imageKey);
                    double sx = x * TILE, sy = y * TILE;

                    if (img != null) {
                        g.drawImage(img, sx + 5, sy + 5, TILE - 10, TILE - 10);
                    } else {
                        g.setFill(Color.SADDLEBROWN);
                        g.fillOval(sx + 10, sy + 10, TILE - 20, TILE - 20);
                    }
                }
            }
        }
    }

    private void drawChefs(GraphicsContext g) {
        if (manager.getChefs() == null) return;

        for (Chef c : manager.getChefs()) {
            double sx = c.getPosition().getX() * TILE;
            double sy = c.getPosition().getY() * TILE;

            Map<Direction, Image> currentChefImages;
            if (c.getName().contains("Chef A") || c.getName().contains("C1")) {
                currentChefImages = chef1Images;
            } else {
                currentChefImages = chef2Images;
            }

            Image img = currentChefImages.get(c.getDirection());

            if (img != null) {
                g.drawImage(img, sx, sy, TILE, TILE);
            } else {
                g.setFill(c == manager.getActiveChef() ? Color.LIMEGREEN : Color.BLUE);
                g.fillRoundRect(sx + 6, sy + 6, TILE - 12, TILE - 12, 8, 8);
            }

            if (c == manager.getActiveChef()) {
                g.setStroke(Color.WHITE);
                g.setLineWidth(2);
                g.strokeOval(sx, sy, TILE, TILE);
                g.setLineWidth(1);
            }

            if (c.getInventory() != null) {
                String heldItemKey = getImageKey(c.getInventory());
                Image heldImg = itemImages.get(heldItemKey);
                
                double itemSize = TILE / 2.0;
                double itemX = sx + (TILE / 4.0); 
                double itemY = sy + (TILE / 4.0); 

                if (heldImg != null) {
                    g.drawImage(heldImg, itemX, itemY, itemSize, itemSize);
                } else {
                    g.setFill(Color.WHITE);
                    g.fillOval(itemX, itemY, itemSize, itemSize);
                    g.setStroke(Color.BLACK);
                    g.strokeOval(itemX, itemY, itemSize, itemSize);
                }
            }
        }
    }

    private void drawProgressBars(GraphicsContext g) {
        GameMap map = manager.getCurrentMap();
        if (map == null) return;

        // --- Draw Chef Action Progress Bars (Existing Logic) ---
        if (manager.getChefs() != null) {
            for (Chef c : manager.getChefs()) {
                Double prog = manager.getProgress(c.getName()).get();

                if (prog > 0 && prog < 1.0) {
                    double x = c.getPosition().getX() * TILE;
                    double y = c.getPosition().getY() * TILE - 10;
                    double width = TILE - 8;

                    g.setFill(Color.gray(0.3));
                    g.fillRect(x + 4, y, width, 6);

                    g.setFill(Color.LIME);
                    g.fillRect(x + 4, y, width * prog, 6);

                    g.setStroke(Color.BLACK);
                    g.strokeRect(x + 4, y, width, 6);
                }
            }
        }
        // --- End Chef Action Progress Bars ---

        // --- Draw Station Progress Bars (New Logic) ---
        for (int x = 0; x < 14; x++) {
            for (int y = 0; y < 10; y++) {
                Tile t = map.getTile(x, y);

                if (t instanceof CookingStation cs) {
                    double cookingProg = cs.cookingProgressProperty().get();
                    double burnProg = cs.burnProgressProperty().get();
                    double sx = x * TILE;
                    double sy = y * TILE;
                    double width = TILE - 8;

                    // Draw Cooking Progress Bar (Green)
                    if (cookingProg > 0.0 && cookingProg < 1.0) {
                        double barY = sy - 15;
                        g.setFill(Color.gray(0.3));
                        g.fillRect(sx + 4, barY, width, 6);

                        g.setFill(Color.YELLOWGREEN);
                        g.fillRect(sx + 4, barY, width * cookingProg, 6);

                        g.setStroke(Color.BLACK);
                        g.strokeRect(sx + 4, barY, width, 6);
                    }

                    // Draw Burn Timer Progress Bar (Orange/Red)
                    if (burnProg > 0.0) {
                        double barY = sy - 8;
                        Color barColor = burnProg < 1.0 ? Color.ORANGE : Color.RED;

                        g.setFill(Color.gray(0.3));
                        g.fillRect(sx + 4, barY, width, 6);

                        g.setFill(barColor);
                        g.fillRect(sx + 4, barY, width * burnProg, 6);

                        g.setStroke(Color.BLACK);
                        g.strokeRect(sx + 4, barY, width, 6);
                    }
                }
            }
        }
    }
}
