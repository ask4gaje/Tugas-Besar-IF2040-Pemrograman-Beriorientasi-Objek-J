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
            // Check if the item is a Plate using pattern matching (Java 16+ style)
            if (heldItem instanceof Plate plate) {

                // Get the name of the contents for logging before they are cleared.
                String contentsName = plate.getDish() != null ? plate.getDish().getName() : (plate.getContents().isEmpty() ? "nothing" : "partial dish contents");

                ((Plate) heldItem).clean();

                // Log the action.
                if (contentsName.equals("nothing")) {
                    LOGGER.info("late remains in hand");
                } else {
                    LOGGER.info("{} discarded dish contents ({}) in trash. Plate remains in hand.", chef.getName(), contentsName);
                }

            } else {
                // For all other items (Ingredient, FryingPan, etc.), discard the item completely.
                Item item = chef.dropItem();
                LOGGER.info("{} threw away {}.", chef.getName(), item.getName());
            }
        }
    }
}