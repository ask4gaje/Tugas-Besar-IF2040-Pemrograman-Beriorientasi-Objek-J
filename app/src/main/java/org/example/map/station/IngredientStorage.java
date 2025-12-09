package org.example.map.station;

import org.example.Position;
import org.example.items.IngredientType;

public class IngredientStorage extends AbstractStation {
    private final IngredientType type;
    
    public IngredientStorage(Position position, IngredientType type) {
        super(position);
        this.type = type;
    }
    
    public IngredientType getType() {
        return type;
    }
    // Anggota 2 akan menambahkan method getRawIngredient().
}