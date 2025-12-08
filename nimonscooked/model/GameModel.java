package com.burger.nimonscooked.model;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameModel {

    private final List<GameModelListener> listeners = new ArrayList<>();
    public void addListener(GameModelListener l) { listeners.add(l); }
    private void notifyListeners() { listeners.forEach(l -> Platform.runLater(l::onModelChanged)); }

    public final int COLS = 14, ROWS = 10;
    private Tile[][] grid = new Tile[COLS][ROWS];

    private List<Chef> chefs = new ArrayList<>();
    private int activeChef = 0;

    private ObservableList<Order> orders = FXCollections.observableArrayList();

    private IntegerProperty score = new SimpleIntegerProperty(0);
    private IntegerProperty timeRemaining = new SimpleIntegerProperty(180);
    private StringProperty activeChefName = new SimpleStringProperty("");

    private Map<String, DoubleProperty> progressMap = new HashMap<>();
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public GameModel() {
        setupGrid();
        setupChefs();
        setupOrders();
        startTimer();
    }

    private void setupGrid() {
        for (int x=0;x<COLS;x++){
            for (int y=0;y<ROWS;y++){
                grid[x][y] = new Tile(x,y, TileType.FLOOR);
            }
        }
        grid[2][1].type = TileType.CUT;
        grid[6][2].type = TileType.INGREDIENT;
        grid[9][2].type = TileType.COOK;
        grid[13][4].type = TileType.SERVE;
    }

    private void setupChefs() {
        Chef c1 = new Chef("Chef1", 6, 6);
        Chef c2 = new Chef("Chef2", 7, 6);
        chefs.add(c1);
        chefs.add(c2);

        progressMap.put(c1.name, new SimpleDoubleProperty(0));
        progressMap.put(c2.name, new SimpleDoubleProperty(0));
        activeChefName.set(c1.name);
    }

    private void setupOrders() {
        orders.addAll(
                new Order(1, "Burger", 60, 100),
                new Order(2, "Cheese Burger", 45, 120)
        );
    }

    private void startTimer() {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                timeRemaining.set(timeRemaining.get() - 1);
                notifyListeners();
            });
        }, 1, 1, TimeUnit.SECONDS);
    }

    public Tile[][] getGrid() { return grid; }
    public List<Chef> getChefs() { return chefs; }
    public Chef getActiveChef() { return chefs.get(activeChef); }
    public ObservableList<Order> getOrders() { return orders; }
    public IntegerProperty scoreProperty() { return score; }
    public IntegerProperty timeProperty() { return timeRemaining; }
    public StringProperty activeChefNameProperty() { return activeChefName; }
    public DoubleProperty getProgress(String chef) { return progressMap.get(chef); }

    // Movement
    public void move(Direction dir) {
        Chef c = getActiveChef();
        int nx = c.x + (dir == Direction.LEFT ? -1 : dir == Direction.RIGHT ? 1 : 0);
        int ny = c.y + (dir == Direction.UP ? -1 : dir == Direction.DOWN ? 1 : 0);

        if (nx < 0 || nx >= COLS || ny < 0 || ny >= ROWS) return;

        if (grid[nx][ny].type != TileType.WALL) {
            c.x = nx; c.y = ny;
            c.facing = dir;
        }
        notifyListeners();
    }

    // Interact (cut/cook)
    public void interact() {
        Chef c = getActiveChef();
        Tile t = getFrontTile(c);
        if (t != null && t.type == TileType.CUT) {
            startProgress(c, 3);
        }
    }

    private Tile getFrontTile(Chef c) {
        int nx = c.x + (c.facing == Direction.LEFT ? -1 : c.facing == Direction.RIGHT ? 1 : 0);
        int ny = c.y + (c.facing == Direction.UP ? -1 : c.facing == Direction.DOWN ? 1 : 0);

        if (nx < 0 || nx >= COLS || ny < 0 || ny >= ROWS) return null;
        return grid[nx][ny];
    }

    private void startProgress(Chef c, int seconds) {
        DoubleProperty p = progressMap.get(c.name);
        AtomicInteger elapsed = new AtomicInteger(0);

        int total = seconds * 1000;
        int step = 100;

        executor.scheduleAtFixedRate(() -> {
            int e = elapsed.addAndGet(step);

            Platform.runLater(() -> {
                p.set(Math.min(1.0, (double)e / total));
                notifyListeners();
            });

            if (e >= total) {
                Platform.runLater(() -> {
                    score.set(score.get() + 10);
                    p.set(0);
                    notifyListeners();
                });
            }

        }, 0, step, TimeUnit.MILLISECONDS);
    }

    public void switchChef() {
        activeChef = (activeChef + 1) % chefs.size();
        activeChefName.set(getActiveChef().name);
        notifyListeners();
    }
}
