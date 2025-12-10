package org.example.map.station;

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
    public void interact(Chef chef) {
        // Chef menyajikan makanan (Drop item ke counter)
        if (chef.getInventory() != null) {
            Item servedItem = chef.dropItem();
            LOGGER.info("{} served {}. (Validation Pending)", chef.getName(), servedItem.getName());
            
            // Di sini nanti panggil logic GameManager untuk cek Order & Score
            // int score = GameManager.getInstance().validateOrder(servedItem);
            
            // Item dianggap "terkirim" dan hilang dari dunia game
        }
    }
}