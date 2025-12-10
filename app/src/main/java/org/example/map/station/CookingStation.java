package org.example.map.station;

import org.example.chef.Position;
import org.example.item.Item;

public class CookingStation extends AbstractStation {
    // Cooking Station dapat menampung Kitchen Utensils (Frying Pan/Boiling Pot)
    // Utensil yang sedang digunakan oleh station
    private Item currentUtensil; 
    
    // Logika thread memasak otomatis akan diwakilkan oleh variabel progress
    private int cookingProgress = 0;
    private boolean isCooking = false;
    private static final int MAX_COOKING_TIME = 10; // Contoh waktu masak
    
    public CookingStation(Position position) {
        super(position);
    }

    public Item getCurrentUtensil() {
        return currentUtensil;
    }
    
    // Metode untuk menaruh Utensil di station
    @Override
    public void setItemOnTile(Item item) {
        // Asumsi item yang diletakkan adalah Utensil (FryingPan/BoilingPot)
        // Jika itemOnTile adalah Utensil, maka ini menjadi currentUtensil
        if (currentUtensil == null && item != null) {
            // Anggota 3 harus memvalidasi jenis Item
            this.currentUtensil = item;
            System.out.println("Utensil placed: " + item.toString());
        } else {
            // Logika lain, misalnya menaruh item di dalam utensil
            super.setItemOnTile(item); 
        }
    }
    
    // Metode untuk mengambil Utensil
    public Item pickUpUtensil() {
        Item utensil = this.currentUtensil;
        this.currentUtensil = null;
        this.isCooking = false;
        this.cookingProgress = 0;
        return utensil;
    }

    // Logika thread memasak (disimulasikan dengan method)
    public void startCooking() {
        if (currentUtensil != null && itemOnTile != null && !isCooking) {
            // Asumsi itemOnTile adalah bahan yang akan dimasak (di dalam Utensil)
            this.isCooking = true;
            this.cookingProgress = 0;
            // Dalam implementasi nyata, ini akan menjadi thread/timer.
            System.out.println("Cooking started for item: " + itemOnTile.toString());
        }
    }
    
    public void updateCooking() {
        if (isCooking && cookingProgress < MAX_COOKING_TIME) {
            cookingProgress++;
            System.out.println("Cooking progress: " + cookingProgress + "/" + MAX_COOKING_TIME);
            if (cookingProgress >= MAX_COOKING_TIME) {
                // Item selesai dimasak. Anggota 3 akan mengganti itemOnTile.
                // Placeholder: Item selesai dimasak
                System.out.println("Cooking finished. Item ready.");
                this.isCooking = false;
            }
        }
    }

    public boolean isCooking() {
        return isCooking;
    }
    
    public int getCookingProgress() {
        return cookingProgress;
    }
}