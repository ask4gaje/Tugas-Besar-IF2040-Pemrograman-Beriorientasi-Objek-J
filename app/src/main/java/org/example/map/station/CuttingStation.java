package org.example.map.station;

import org.example.chef.Chef;
import org.example.chef.Position;
import org.example.item.Ingredient;
import org.example.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CuttingStation extends AbstractStation {
    private static final Logger LOGGER = LoggerFactory.getLogger(CuttingStation.class);
    private static final int CUTTING_TIME_SECONDS = 3;

    // Removed unused internal progress variables and methods,
    // relying on Chef.performLongAction and Ingredient state.

    public CuttingStation(Position position) {
        super(position);
    }

    @Override
    public void interact(Chef chef) {
        if (chef.getCurrentAction() == org.example.chef.ChefActionState.BUSY) {
            LOGGER.info("{} is busy and cannot interact.", chef.getName());
            return;
        }

        // Case 1: Chef has item and station is empty -> Drop item (only if choppable ingredient or any item if not an ingredient)
        if (itemOnTile == null && chef.getInventory() != null) {
            Item heldItem = chef.getInventory();
            if (heldItem instanceof Ingredient ingredient) {
                if (ingredient.canBeChopped()) {
                    this.itemOnTile = chef.dropItem();
                    LOGGER.info("{} placed {} on Cutting Station.", chef.getName(), itemOnTile.getName());
                } else {
                    LOGGER.warn("{} cannot be chopped or is already prepared.", heldItem.getName());
                }
            } else {
                // Allow placing non-ingredient items (e.g., a plate) on the station
                this.itemOnTile = chef.dropItem();
                LOGGER.info("{} placed {} on Cutting Station.", chef.getName(), itemOnTile.getName());
            }
        }

        // Case 2: Chef is empty and station has item -> Pick up or Start Cutting
        else if (itemOnTile != null && chef.getInventory() == null) {
            // A. Pick up the item
            if (itemOnTile instanceof Ingredient ingredient) {
                // If ingredient is already prepared (chopped) or is a BUN (not choppable), pick up.
                if (!ingredient.canBeChopped()) {
                    chef.setInventory(this.itemOnTile);
                    this.itemOnTile = null;
                    LOGGER.info("{} took {} from Cutting Station.", chef.getName(), ingredient.getName());
                    return;
                }
            }

            // B. Start Cutting the raw ingredient
            if (itemOnTile instanceof Ingredient ingredient && ingredient.canBeChopped()) {
                LOGGER.info("{} started cutting {}.", chef.getName(), ingredient.getName());
                chef.performLongAction(CUTTING_TIME_SECONDS, () -> {
                    ingredient.chop();
                    LOGGER.info("{} finished cutting. Item is now {}.", chef.getName(), ingredient.getName());
                });
            } else {
                // If it's a non-Ingredient item (e.g., Plate) or an un-choppable item, pick up immediately.
                chef.setInventory(this.itemOnTile);
                this.itemOnTile = null;
                LOGGER.info("{} took {} from Cutting Station.", chef.getName(), chef.getInventory().getName());
            }
        }
    }
}