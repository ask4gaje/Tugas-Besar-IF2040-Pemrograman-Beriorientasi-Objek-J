package org.example.map.station;

import org.example.chef.Chef;
import org.example.chef.Position;
import org.example.item.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrashStation extends AbstractStation {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrashStation.class);

    public TrashStation(Position position) {
        super(position);
    }

    @Override
    public void interact(Chef chef) {}

    @Override
    public void pickUp(Chef chef) {
        Item heldItem = chef.getInventory();

        if (heldItem != null) {

            // Check if the item is a Plate using pattern matching (Java 16+)
            if (heldItem instanceof Plate plate) {

                // If the plate has contents, log what is being discarded
                if (!plate.getContents().isEmpty() || plate.getDish() != null) {
                    // Note: plate.getDish().getName() may be null if the dish was just a single item, but this is a reasonable assumption based on prior code logic.
                    String dishName = plate.getDish() != null ? plate.getDish().getName() : "contents";
                    LOGGER.info("{} threw away dish {}. Plate remains in hand.", chef.getName(), dishName);
                } else {
                    LOGGER.info("{} Plate remains in hand.", chef.getName());
                }
            }

            else {
                // For all other items (Ingredient, FryingPan, etc.), discard the item as before.
                Item item = chef.dropItem();
                LOGGER.info("{} threw away {}.", chef.getName(), item.getName());
                // Item lost
            }
        }
    }
}