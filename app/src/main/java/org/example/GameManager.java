package org.example;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.example.config.MapLayouts;
import org.example.map.GameMap;
import org.example.map.Tile;
import org.example.map.station.CuttingStation;
import org.example.chef.Position;
import org.example.chef.Chef;
import org.example.chef.Direction;
import org.example.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GameManager {
    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);
    
    private static GameManager instance; 

    private GameState currentState;
    private int failedOrderCount;
    private GameMap currentMap;
    private Position chefSpawnA;
    private Position chefSpawnB;
    private List<Chef> chefs = new ArrayList<>();
    private IntegerProperty score = new SimpleIntegerProperty(0);
    private IntegerProperty activeChefIndex = new SimpleIntegerProperty(0); 
    private StringProperty activeChefName = new SimpleStringProperty("");

    private ScheduledExecutorService timerScheduler;
    private IntegerProperty timeRemaining = new SimpleIntegerProperty(180);
    private ObservableList<Order> orders = FXCollections.observableArrayList();
    private final int MAX_GAME_DURATION = 180; // 3 menit (Contoh)
    private final int MAX_FAILED_ORDERS = 5;

    private GameManager() {
        this.currentState = GameState.MAIN_MENU;
        logger.info("GameManager (Singleton) diinisialisasi.");
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void initialize() {
        logger.info("Memuat Stage: Burger Map...");
        loadStage(MapLayouts.BURGER_MAP_LAYOUT); 
        
        this.currentState = GameState.STAGE_SELECT;

        startGame();
    }
    
    public void loadStage(String[] layout) {
        this.currentMap = new GameMap();
        this.currentMap.loadMap(layout);
        this.chefSpawnA = this.currentMap.getChefSpawnA();
        this.chefSpawnB = this.currentMap.getChefSpawnB();
        logger.info("Stage dimuat. Posisi awal Chef A: ({},{}), Chef B: ({},{})",
                    chefSpawnA.getX(), chefSpawnA.getY(), chefSpawnB.getX(), chefSpawnB.getY());
    }

    public void startGame() {
        if (currentState == GameState.PLAYING) return;
        
        if (this.chefs == null) {
            this.chefs = new ArrayList<>();
        } else {
            this.chefs.clear();
        }

        Chef c1 = new Chef("C1", "Chef A", chefSpawnA != null ? chefSpawnA : new Position(1, 1));
        Chef c2 = new Chef("C2", "Chef B", chefSpawnB != null ? chefSpawnB : new Position(2, 1));
        
        chefs.add(c1);
        chefs.add(c2);

        this.activeChefIndex.set(0); 
        chefs.get(0).setActive(true);
        activeChefName.set(chefs.get(0).getName());

        orders.clear();
        orders.addAll(
            new Order(1, "Burger", 60, 100),
            new Order(2, "Cheese Burger", 45, 120)
        );

        this.currentState = GameState.PLAYING;
        this.failedOrderCount = 0; 
        this.score.set(0);
        logger.info("Permainan Dimulai. Waktu: {} detik", MAX_GAME_DURATION);
        startTimer(MAX_GAME_DURATION);
    }

    private void startTimer(int duration) {
        this.timeRemaining.set(duration);
        this.timerScheduler = Executors.newScheduledThreadPool(2);

        timerScheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                if (currentState != GameState.PLAYING) {
                    timerScheduler.shutdown();
                    return;
                }

                int current = timeRemaining.get();
                if (current > 0) {
                    timeRemaining.set(current - 1);
                } else {
                    endStage(EndCondition.TIMES_UP);
                    timerScheduler.shutdown();
                }
            });
        }, 1, 1, TimeUnit.SECONDS); 
    }

    public void moveChef(Direction dir) {
        Chef active = getActiveChef();
        if (active != null && currentState == GameState.PLAYING) {
            active.move(dir, currentMap);
        }
    }

    public void switchChef() {
        if (chefs == null || chefs.isEmpty()) return;

        getActiveChef().setActive(false);

        int currentIndex = activeChefIndex.get();
        int nextIndex = (currentIndex + 1) % chefs.size();
        activeChefIndex.set(nextIndex);

        Chef newChef = getActiveChef();
        newChef.setActive(true);
        activeChefName.set(newChef.getName());
        
        logger.info("Switch Chef ke: {}", newChef.getName());
    }

    public void interact() {
        Chef active = getActiveChef();
        
        if (active != null && currentState == GameState.PLAYING) {
            active.interact(currentMap);
        }
    }

    public void grabItem() {
        Chef active = getActiveChef();
        
        if (active != null && currentState == GameState.PLAYING) {
            active.grabItem(currentMap);
        }
    }

    public void increaseFailedOrderCount() {
        if (currentState != GameState.PLAYING) return;

        this.failedOrderCount++;
        logger.warn("Order Gagal! Total: {}/{}", failedOrderCount, MAX_FAILED_ORDERS);

        if (this.failedOrderCount >= MAX_FAILED_ORDERS) {
            endStage(EndCondition.TOO_MANY_FAILED_ORDERS);
        }
    }

    public void updateScore(int delta) {
        this.score.set(this.score.get() + delta);
        logger.info("Skor diperbarui: {} (Delta: {})", score, delta);
    }

    public void endStage(EndCondition condition) {
        if (currentState != GameState.PLAYING) return;

        this.currentState = GameState.GAME_OVER;

        if (timerScheduler != null && !timerScheduler.isShutdown()) {
            timerScheduler.shutdownNow();
            logger.info("Scheduler Timer dihentikan.");
        }

        logger.info("=== STAGE OVER! ===");
        logger.info("Kondisi: {}", condition);
        logger.info("SKOR AKHIR: {}", score);

        boolean isPassed = condition == EndCondition.TIMES_UP && score.get() >= 500; 
        logger.info("STATUS KELULUSAN: {}", isPassed ? "PASS" : "FAIL");
    }
    
    public GameMap getCurrentMap() {
        return currentMap;
    }

    public Position getChefSpawnA() {
        return chefSpawnA;
    }

    public Position getChefSpawnB() {
        return chefSpawnB;
    }

    public int getScore() {
        return score.get();
    }

    public int getFailedOrderCount() {
        return failedOrderCount;
    }

    public List<Chef> getChefs() {
        return chefs;
    }
    public int getActiveChefIndex() {
        return activeChefIndex.get();
    }

    public DoubleProperty getProgress(String chefName) {
        for (Chef c : chefs) {
            if (c.getName().equals(chefName)) {
                return c.actionProgressProperty(); 
            }
        }
        return null;
    }

    public IntegerProperty timeProperty() { return timeRemaining; }
    public IntegerProperty scoreProperty() { return score; }
    public StringProperty activeChefNameProperty() { return activeChefName; }
    public ObservableList<Order> getOrders() { return orders; }

    public Chef getActiveChef() { 
        if (chefs == null || chefs.isEmpty()) return null;
        return chefs.get(activeChefIndex.get()); 
    }

}