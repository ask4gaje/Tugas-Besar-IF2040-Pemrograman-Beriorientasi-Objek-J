package org.example.map.station;

import org.example.chef.Chef;
import org.example.chef.ChefActionState;
import org.example.chef.Position;
import org.example.item.Ingredient;
import org.example.item.IngredientType;
import org.example.item.Item;
import org.example.item.Plate;
import org.example.map.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CuttingStation extends AbstractStation {
    private static final Logger LOGGER = LoggerFactory.getLogger(CuttingStation.class);
    private static final int CUTTING_TIME_SECONDS = 3;

    public CuttingStation(Position position) {
        super(position);
    }

    @Override
    public void interact(Chef chef) {
        if (chef.getCurrentAction() == org.example.chef.ChefActionState.BUSY) {
            LOGGER.info("{} is busy and cannot interact.", chef.getName());
            return;
        }

        if (this.itemOnTile != null) {
            if (this.itemOnTile instanceof Ingredient) {
                Ingredient ingredient = (Ingredient) this.itemOnTile;

                if (ingredient.canBeChopped()) {
                    
                    LOGGER.info("{} started cutting {}...", chef.getName(), ingredient.getName());
                    
                    chef.performLongAction(CUTTING_TIME_SECONDS, () -> {
                        ingredient.chop(); 
                        LOGGER.info("Cutting finished! Item is now {}", ingredient.getName());
                    });
                    
                } else {
                    LOGGER.warn("Item {} tidak bisa dipotong (mungkin sudah matang/chopped).", ingredient.getName());
                }
            }
        } else {
            LOGGER.info("Tidak ada bahan di meja untuk dipotong.");
        }
    }

    @Override
    public void pickUp(Chef chef) {
        Item heldItem = chef.getInventory();

        if (itemOnTile != null && heldItem == null) {
            chef.setInventory(this.itemOnTile);
            this.itemOnTile = null;
            LOGGER.info("{} took {} from Cutting Station.", chef.getName(), chef.getInventory().getName());
            return;
        }

        else if (itemOnTile == null && heldItem != null) {
            this.itemOnTile = chef.dropItem();
            LOGGER.info("{} placed {} on Cutting Station.", chef.getName(), itemOnTile.getName());
            return;
        }

        LOGGER.warn("{} tried to pick up, but nothing happened.", chef.getName());
    }
}