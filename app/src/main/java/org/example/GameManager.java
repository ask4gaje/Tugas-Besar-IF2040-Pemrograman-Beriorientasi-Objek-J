
package org.example;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.example.config.MapLayouts;
import org.example.map.GameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GameManager {
    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);
    
    // 1. Singleton Instance
    private static GameManager instance; 

    // Atribut State
    private GameState currentState;
    private int score;
    private int failedOrderCount;
    private GameMap currentMap;
    private Position chefSpawnA;
    private Position chefSpawnB;

    // Atribut Concurrency dan Stage Over
    private ScheduledExecutorService timerScheduler;
    private int timeRemainingSeconds;
    private final int MAX_GAME_DURATION = 180; // 3 menit (Contoh)
    private final int MAX_FAILED_ORDERS = 5;

    // 2. Konstruktor Private
    private GameManager() {
        this.currentState = GameState.MAIN_MENU;
        this.score = 0;
        logger.info("GameManager (Singleton) diinisialisasi.");
    }

    // 3. Method publik statis untuk mendapatkan instance
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void initialize() {
        logger.info("Memuat Stage: Burger Map...");
        
        // Panggil method pemuatan stage di sini
        loadStage(MapLayouts.BURGER_MAP_LAYOUT); 
        
        this.currentState = GameState.STAGE_SELECT;
        logger.info("Current State diubah menjadi: {}", currentState);
    }
    
    // --- Method loadStage() ---
    public void loadStage(String[] layout) {
        // 1. Buat instance GameMap
        this.currentMap = new GameMap();
        
        // 2. Parsing layout peta
        this.currentMap.loadMap(layout);
        
        // 3. Simpan posisi spawn chef (disediakan untuk Anggota 2/3)
        this.chefSpawnA = this.currentMap.getChefSpawnA();
        this.chefSpawnB = this.currentMap.getChefSpawnB();

        // [Konsep Generics Wajib]: Anda bisa menambahkan method utilitas Generics
        // public <T extends Tile> T getStation(Class<T> stationClass) { ... }
        
        logger.info("Stage dimuat. Posisi awal Chef A: ({},{}), Chef B: ({},{})",
                    chefSpawnA.getX(), chefSpawnA.getY(), chefSpawnB.getX(), chefSpawnB.getY());
    }

    public void startGame() {
        if (currentState == GameState.PLAYING) return;

        this.currentState = GameState.PLAYING;
        this.failedOrderCount = 0; // Reset
        this.score = 0;          // Reset
        logger.info("Permainan Dimulai. Waktu: {} detik", MAX_GAME_DURATION);
        startTimer(MAX_GAME_DURATION);
    }

    private void startTimer(int duration) {
        this.timeRemainingSeconds = duration;
        // Membuat single thread scheduler dan memberi nama thread
        this.timerScheduler = Executors.newSingleThreadScheduledExecutor(
            r -> new Thread(r, "GameTimerThread")
        );

        // Menjalankan tugas setiap 1 detik
        timerScheduler.scheduleAtFixedRate(() -> {
            try {
                if (currentState != GameState.PLAYING) {
                    timerScheduler.shutdown();
                    logger.debug("Timer dimatikan.");
                    return;
                }

                timeRemainingSeconds--;
                // Logging terstruktur dengan nama thread (Wajib)
                logger.debug("Waktu tersisa: {} detik", timeRemainingSeconds); 

                if (timeRemainingSeconds <= 0) {
                    endStage(EndCondition.TIMES_UP);
                    timerScheduler.shutdown();
                }
            } catch (Exception e) {
                logger.error("Error pada GameTimerThread: {}", e.getMessage());
            }
        }, 0, 1, TimeUnit.SECONDS); 
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
        this.score += delta;
        logger.info("Skor diperbarui: {} (Delta: {})", score, delta);
    }

    public void endStage(EndCondition condition) {
        if (currentState != GameState.PLAYING) return;

        this.currentState = GameState.GAME_OVER;

        // Menghentikan Scheduler
        if (timerScheduler != null && !timerScheduler.isShutdown()) {
            timerScheduler.shutdownNow();
            logger.info("Scheduler Timer dihentikan.");
        }

        logger.info("=== STAGE OVER! ===");
        logger.info("Kondisi: {}", condition);
        logger.info("SKOR AKHIR: {}", score);

        // Logika sederhana Pass/Fail (Boleh disempurnakan nanti)
        boolean isPassed = condition == EndCondition.TIMES_UP && score >= 500; // Misal target skor 500
        logger.info("STATUS KELULUSAN: {}", isPassed ? "PASS" : "FAIL");
    }
    
    // --- Getter Baru untuk Anggota Lain ---
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
    return score;
        }

    public int getFailedOrderCount() {
        return failedOrderCount;
    }

    // ... method lain
}