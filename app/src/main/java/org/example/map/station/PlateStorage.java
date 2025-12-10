package org.example.map.station;

import org.example.item.Item;
import org.example.item.Plate;
import org.example.chef.Position;

import java.util.Stack; 

public class PlateStorage extends AbstractStation {
    // Stack untuk tumpukan piring bersih
    private final Stack<Item> cleanPlateStack = new Stack<>();
    // Stack untuk tumpukan piring kotor (jika ada) - Dihilangkan, kotoran masuk ke WashingStation
    
    public PlateStorage(Position position) {
        super(position);
        // Pada inisialisasi, Plate Storage diisi dengan plate bersih awal (4 Plate)
        for (int i = 0; i < 4; i++) {
             // Asumsi CleanPlate() adalah konstruktor yang valid
            // cleanPlateStack.push(new CleanPlate()); 
        }
        System.out.println("Plate Storage initialized with 4 clean plates (placeholder).");
    }

    public Stack<Item> getCleanPlateStack() {
        return cleanPlateStack;
    }
    
    // Logika mengambil piring bersih
    public Item takeCleanPlate() {
        if (!cleanPlateStack.isEmpty()) {
            return cleanPlateStack.pop();
        }
        return null;
    }
    
    // Logika menaruh piring (asumsi, hanya menerima piring bersih untuk ditumpuk)
    public boolean putPlate(Item item) {
        // Anggota 3: Validasi bahwa item adalah piring bersih.
        if (item instanceof Plate) {
            if (((Plate) item).isClean()){
            cleanPlateStack.push(item);
            return true;
            }
        }
        System.out.println("Only clean plates can be put here.");
        return false;
    }
}