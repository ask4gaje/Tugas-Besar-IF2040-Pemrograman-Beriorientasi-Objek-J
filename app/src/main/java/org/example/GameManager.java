package org.example;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.example.config.MapLayouts;
import org.example.map.GameMap;
import org.example.map.Tile;
import org.example.map.station.CuttingStation;
import org.example.chef.Position;
import org.example.chef.Chef;
import org.example.chef.Direction;
import org.example.model.Order;
import org.example.item.Plate;
import org.example.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GameManager {
    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);
    private static final Random RANDOM = new Random();

    // --- New Recipe/Order Data ---
    private static final List<String> ALL_RECIPES = Arrays.asList(
            "Classic Burger Dish",
            "Cheese Burger Dish",
            "BLT Burger Dish",
            "Deluxe Burger Dish"
    );
    private static final Map<String, Integer> RECIPE_REWARD = Map.of(
            "Classic Burger Dish", 100,
            "Cheese Burger Dish", 120,
            "BLT Burger Dish", 150,
            "Deluxe Burger Dish", 200
    );

    // FIX: Define fixed time limits for each recipe
    private static final Map<String, Integer> RECIPE_TIME_LIMIT = Map.of(
            "Classic Burger Dish", 60,
            "Cheese Burger Dish", 75,
            "BLT Burger Dish", 90,
            "Deluxe Burger Dish", 90
    );

    private static final int MAX_ACTIVE_ORDERS = 2;

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
    private ScheduledExecutorService orderScheduler;
    private IntegerProperty timeRemaining = new SimpleIntegerProperty(180);
    private ObservableList<Order> orders = FXCollections.observableArrayList();
    private final int MAX_GAME_DURATION = 300;
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

        this.currentState = GameState.PLAYING;
        this.failedOrderCount = 0;
        this.score.set(0);
        logger.info("Permainan Dimulai. Waktu: {} detik", MAX_GAME_DURATION);
        startTimer(MAX_GAME_DURATION);
        startOrderGenerator();
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

                    // Decrement time for active orders (running simultaneously)
                    orders.removeIf(order -> {
                        order.setTimeLeft(order.getTimeLeft() - 1);
                        if (order.getTimeLeft() <= 0) {
                            logger.warn("Order {} failed (Time's up)!", order.getId());
                            increaseFailedOrderCount();
                            return true; // Remove the failed order
                        }
                        return false;
                    });
                } else {
                    endStage(EndCondition.TIMES_UP);
                    timerScheduler.shutdown();
                }
            });
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void startOrderGenerator() {
        this.orderScheduler = Executors.newSingleThreadScheduledExecutor();
        // Check every 3 seconds if a new order is needed
        orderScheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(this::generateNewOrder);
        }, 5, 3, TimeUnit.SECONDS); // Delay 5s, Repeat 3s
    }

    private void generateNewOrder() {
        if (currentState != GameState.PLAYING || orders.size() >= MAX_ACTIVE_ORDERS) {
            return;
        }

        String recipe = ALL_RECIPES.get(RANDOM.nextInt(ALL_RECIPES.size()));

        int time = RECIPE_TIME_LIMIT.getOrDefault(recipe, 60);
        int reward = RECIPE_REWARD.get(recipe);

        int newId = 1;
        if (!orders.isEmpty()) {
            newId = orders.stream().mapToInt(Order::getId).max().orElse(0) + 1;
        }

        Order newOrder = new Order(newId, recipe, time, reward);
        orders.add(newOrder);
        logger.info("New Order received: {} (Reward: {}, Time: {}s)", newOrder.getRecipe(), newOrder.getReward(), newOrder.getTimeLeft());
    }

    public int validateOrder(Item servedItem) {
        if (!(servedItem instanceof Plate plate)) {
            logger.warn("Served item is not a Plate. Invalid service.");
            increaseFailedOrderCount();
            return 0;
        }

        String dishName = plate.getName();

        Order matchedOrder = null;
        for (Order order : orders) {
            if (order.getRecipe().equals(dishName)) {
                matchedOrder = order;
                break;
            }
        }

        if (matchedOrder != null) {
            orders.remove(matchedOrder);
            updateScore(matchedOrder.getReward());
            logger.info("Order {} (Recipe: {}) fulfilled successfully!", matchedOrder.getId(), matchedOrder.getRecipe());
            return matchedOrder.getReward();
        } else {
            logger.warn("Served dish {} does not match any active order.", dishName);
            increaseFailedOrderCount();
            return 0;
        }
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

        if (orderScheduler != null && !orderScheduler.isShutdown()) {
            orderScheduler.shutdownNow();
            logger.info("Order Scheduler dihentikan.");
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