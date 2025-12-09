package org.example.map.station;

import org.example.Position;
import org.example.items.Item;

import java.util.LinkedList; 

// Washing Station perlu Collections (LinkedList/Queue) untuk tumpukan piring kotor.
public class WashingStation extends AbstractStation {
    // Anggota 3 akan menggunakan ini:
    private final LinkedList<Item> dirtyPlates = new LinkedList<>(); 
    
    public WashingStation(Position position) {
        super(position);
    }

    public LinkedList<Item> getDirtyPlates() {
        return dirtyPlates;
    }
    
    // Anggota 3 akan menambahkan method cuci() dan getCleanPlate().
}