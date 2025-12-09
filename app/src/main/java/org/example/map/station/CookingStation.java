package org.example.map.station;

import org.example.Position;

public class CookingStation extends AbstractStation {
    // Cooking Station dapat menampung Kitchen Utensils (Frying Pan/Boiling Pot)
    
    public CookingStation(Position position) {
        super(position);
    }
    // Anggota 3 akan menambahkan logika thread memasak otomatis.
}
