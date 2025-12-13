package org.example.map.station;

import org.example.GameManager;
import org.example.chef.Chef;
import org.example.chef.Position;
import org.example.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServingCounter extends AbstractStation {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServingCounter.class);

    public ServingCounter(Position position) {
        super(position);
    }

    @Override
    public void interact(Chef chef) {}

    @Override
    public void pickUp(Chef chef) {
        if (chef.getInventory() != null) {
            Item servedItem = chef.getInventory();

            // Validate order via GameManager
            int score = GameManager.getInstance().validateOrder(servedItem);

            if (score > 0) {
                // If validation passed, drop the item permanently (serve)
                chef.dropItem();
                LOGGER.info("{} served {} successfully. Score: {}", chef.getName(), servedItem.getName(), score);
            } else {
                // If invalid, the item remains in the chef's hand, and GameManager logs the failure.
                LOGGER.warn("{} failed to serve {}. Item retained.", chef.getName(), servedItem.getName());
            }
        }
    }
}