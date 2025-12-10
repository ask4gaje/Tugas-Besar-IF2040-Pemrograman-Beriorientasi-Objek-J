package org.example.map.station;

import org.example.chef.Chef;
import org.example.chef.Position;
import org.example.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrashStation extends AbstractStation {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrashStation.class);

    public TrashStation(Position position) {
        super(position);
    }

    @Override
    public void interact(Chef chef) {
        // Jika chef bawa item, buang itemnya
        if (chef.getInventory() != null) {
            Item item = chef.dropItem();
            LOGGER.info("{} threw away {}.", chef.getName(), item.getName());
            // Item hilang (tidak disimpan di station)
        }
    }
}