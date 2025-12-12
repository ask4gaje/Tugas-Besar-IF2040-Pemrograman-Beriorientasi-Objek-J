package org.example.map.station;

import org.example.chef.Chef;
import org.example.chef.Position;
import org.example.item.Ingredient;
import org.example.item.Item;
import org.example.item.Plate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssemblyStation extends AbstractStation {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssemblyStation.class);

    public AssemblyStation(Position position) {
        super(position);
    }

    @Override
    public void interact(Chef chef) {
        // --- Case A: Try to Combine (Plating: Plate in hand + Ingredient on Tile) ---
        if (chef.getInventory() instanceof Plate plate && itemOnTile instanceof Ingredient ingredient) {
            if (ingredient.canBePlacedOnPlate()) {
                // Ingredient is consumed and added to the Plate
                plate.addDishComponent(ingredient);
                this.itemOnTile = null;
                LOGGER.info("{} successfully added {} to their plate: {}", chef.getName(), ingredient.getName(), plate.getName());
                return;
            } else {
                LOGGER.warn("Cannot combine: {} is not prepared for plating.", ingredient.getName());
                return;
            }
        }

        // --- Case B: Try to pick up item (Chef is empty) ---
        if (itemOnTile != null && chef.getInventory() == null) {
            chef.setInventory(this.itemOnTile);
            this.itemOnTile = null;
            LOGGER.info("{} took {} from Assembly Station.", chef.getName(), chef.getInventory().getName());
            return;
        }

        // --- Case C: Try to drop item (Station is empty) ---
        else if (itemOnTile == null && chef.getInventory() != null) {
            this.itemOnTile = chef.dropItem();
            LOGGER.info("{} placed {} on Assembly Station.", chef.getName(), itemOnTile.getName());
            return;
        }

        LOGGER.warn("{} tried to interact, but nothing happened.", chef.getName());
    }
}