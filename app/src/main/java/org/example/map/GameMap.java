package org.example.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.chef.Position;
import org.example.config.MapLayouts;
import org.example.item.IngredientType;
import org.example.map.station.*; 


public class GameMap {
    private static final Logger logger = LoggerFactory.getLogger(GameMap.class);
    
    private final int width = MapLayouts.MAP_WIDTH;
    private final int height = MapLayouts.MAP_HEIGHT;
    
    private Tile[][] grid = new Tile[height][width]; 
    
    // Atribut untuk posisi awal Chef (Disediakan untuk Anggota 2)
    private Position chefSpawnA;
    private Position chefSpawnB;

    // --- Getter untuk Anggota 2 ---
    public Position getChefSpawnA() { return chefSpawnA; }
    public Position getChefSpawnB() { return chefSpawnB; }
    public Tile getTile(Position pos) {
        return getTile(pos.getX(), pos.getY());
    }

    public Tile getTile(int x, int y) {
        // Validasi Boundary Check (Exceptions Wajib OOP)
        if (x < 0 || x >= width || y < 0 || y >= height) {
            // Melempar Exception jika koordinat di luar batas
            throw new IllegalArgumentException(String.format("Koordinat di luar batas peta: (%d, %d)", x, y));
        }
        return grid[y][x];
    }
    
    // --- Implementasi Parsing Peta ---
    public void loadMap(String[] layout) {
        logger.info("Memulai parsing peta {}x{}", width, height);
        
        for (int y = 0; y < height; y++) {
            // Hapus spasi dari layout jika ada, untuk memastikan parsing X=14 berhasil
            String row = layout[y].replaceAll("\\s", ""); 
            
            if (row.length() != width) {
                logger.error("Baris ke-{} memiliki panjang yang salah: {} (Seharusnya {})", y + 1, row.length(), width);
                // Melempar Exception jika layout tidak sesuai dimensi
                throw new IllegalStateException("Layout peta memiliki dimensi yang tidak valid.");
            }

            for (int x = 0; x < width; x++) {
                char symbol = row.charAt(x);
                Position pos = new Position(x, y);

                // Menggunakan Polymorphism: Menetapkan instance sub-class ke reference Tile
                switch (symbol) {
                    case 'X':
                        grid[y][x] = new WallTile(pos);
                        break;
                    case '.':
                        grid[y][x] = new WalkableTile(pos);
                        break;
                    case 'V':
                        // Spawn point tetap Walkable, tapi simpan posisinya
                        grid[y][x] = new WalkableTile(pos);
                        if (chefSpawnA == null) {
                            chefSpawnA = pos;
                        } else {
                            chefSpawnB = pos;
                        }
                        break;
                    // --- STATION IMPLEMENTATIONS ---
                    case 'C':
                        grid[y][x] = new CuttingStation(pos);
                        break;
                    case 'R':
                        grid[y][x] = new CookingStation(pos);
                        break;
                    case 'A':
                        grid[y][x] = new AssemblyStation(pos);
                        break;
                    case 'S':
                        grid[y][x] = new ServingCounter(pos);
                        break;
                    case 'W':
                        grid[y][x] = new WashingStation(pos);
                        break;
                    case 'I':
                        IngredientType type = IngredientType.BUN;
                        if (x == 0 && y == 2) type = IngredientType.BUN;      
                        else if (x == 0 && y == 4) type = IngredientType.MEAT; 
                        else if (x == 0 && y == 6) type = IngredientType.CHEESE;
                        else if (x == 8 && y == 0) type = IngredientType.LETTUCE; 
                        else if (x == 5 && y == 4) type = IngredientType.TOMATO;  
                        grid[y][x] = new IngredientStorage(pos, type);
                        break;
                    case 'P':
                        grid[y][x] = new PlateStorage(pos);
                        break;
                    case 'T':
                        grid[y][x] = new TrashStation(pos);
                        break;
                    default:
                        // Menangani simbol yang tidak dikenal
                        logger.warn("Simbol map tidak dikenal: {} pada ({}, {}). Diperlakukan sebagai Wall.", symbol, x, y);
                        grid[y][x] = new WallTile(pos);
                }
            }
        }
        logger.info("Map Parsing Selesai. Spawn A: ({},{}), Spawn B: ({},{})", 
                    chefSpawnA.getX(), chefSpawnA.getY(), chefSpawnB.getX(), chefSpawnB.getY());
    }

    // Method Generics untuk mencari semua instance dari tipe Station tertentu 
    public <T extends Tile> java.util.List<T> findAllTilesOfType(Class<T> tileClass) {
        java.util.List<T> foundTiles = new java.util.ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = grid[y][x];
                // Memeriksa apakah tile adalah instance dari class yang diminta
                if (tileClass.isInstance(tile)) { 
                    foundTiles.add(tileClass.cast(tile)); // Melakukan casting yang aman
                }
            }
        }
        logger.debug("Ditemukan {} instance dari tipe {}", foundTiles.size(), tileClass.getSimpleName());
        return foundTiles;
    }
}