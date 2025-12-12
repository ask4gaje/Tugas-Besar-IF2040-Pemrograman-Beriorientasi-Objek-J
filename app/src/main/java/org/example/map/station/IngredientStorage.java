package org.example.map.station;

import org.example.chef.Chef;
import org.example.chef.Position;
import org.example.item.IngredientFactory;
import org.example.item.IngredientType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IngredientStorage extends AbstractStation {
    private static final Logger LOGGER = LoggerFactory.getLogger(IngredientStorage.class);
    private final IngredientType type;
    
    public IngredientStorage(Position position, IngredientType type) {
        super(position);
        this.type = type;
    }
    
    public IngredientType getStorageType() {
        return type;
    }

    @Override
    public void interact(Chef chef) {
        // Chef hanya bisa mengambil jika tangan kosong
        if (chef.getInventory() == null) {
            // Spawn ingredient baru menggunakan Factory
            chef.setInventory(IngredientFactory.createIngredient(type));
            LOGGER.info("{} took {} from Storage.", chef.getName(), type.label);
        }
    }
}