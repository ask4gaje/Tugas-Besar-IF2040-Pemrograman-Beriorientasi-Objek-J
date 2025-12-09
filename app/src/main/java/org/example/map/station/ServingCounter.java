package org.example.map.station;

import org.example.Position;
import org.example.items.Item;

public class ServingCounter extends AbstractStation {
    // Placeholder untuk Order dan Score Manager
    // private final ScoreManager scoreManager; 

    public ServingCounter(Position position) {
        super(position);
        // this.scoreManager = scoreManager; // Dihilangkan untuk kesederhanaan
    }
    
    // Logika validasi order dan scoring
    public int serveOrder(Item servedItem) {
        if (servedItem == null) {
            return 0;
        }

        // Anggota 3: Logika untuk membandingkan servedItem dengan Order yang aktif.
        // Asumsi servedItem adalah CleanPlate yang berisi makanan yang sudah selesai.
        
        // Contoh validasi placeholder:
        // Cek apakah item adalah CleanPlate yang sudah di-plating
        if (isPlatedDish(servedItem)) { 
            // Cek ke Order List
            // Order matchingOrder = findMatchingOrder(servedItem); 

            // Contoh scoring sederhana:
            int score = 100; // Jika sesuai
            
            // if (matchingOrder != null) {
            //     score = scoreManager.calculateScore(servedItem, matchingOrder); 
            //     System.out.println("Order served successfully! Score: " + score);
            //     // scoreManager.addScore(score);
            //     // orderManager.completeOrder(matchingOrder);
            // } else {
            //     score = -10; // Penalty untuk salah order
            //     // scoreManager.addScore(score);
            //     System.out.println("Wrong order served! Penalty: " + score);
            // }

            // Setelah serve, piring kotor ditinggalkan untuk dijemput
            // servedItem harus menjadi DirtyPlate.
            // Placeholder: itemOnTile diisi dengan piring kotor
            super.setItemOnTile(servedItem); // Item di counter menjadi DirtyPlate (asumsi)
            
            return score;
        } else {
            System.out.println("Item is not a complete plated dish.");
            return 0;
        }
    }
    
    // Placeholder method untuk mengecek apakah item adalah hidangan yang sudah di-plating
    private boolean isPlatedDish(Item item) {
        // Anggota 3 akan memiliki logika validasi yang kompleks di sini.
        // Misalnya: return item instanceof CleanPlate && ((CleanPlate) item).hasFood();
        return true;
    }
}