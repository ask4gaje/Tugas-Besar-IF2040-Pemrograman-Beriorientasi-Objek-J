package org.example.map.station;

import org.example.chef.Chef;
import org.example.chef.Position;
import org.example.item.Item;
import org.example.item.Plate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Stack; 

public class PlateStorage extends AbstractStation {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlateStorage.class);
    private final Stack<Item> cleanPlateStack = new Stack<>();
    
    public PlateStorage(Position position) {
        super(position);
        // Isi awal 4 piring
        for (int i = 0; i < 4; i++) {
            cleanPlateStack.push(new Plate()); 
        }
    }

    @Override
    public void interact(Chef chef) {}

    @Override
    public void pickUp(Chef chef) {
        if (chef.getInventory() == null && !cleanPlateStack.isEmpty()) {
            chef.setInventory(cleanPlateStack.pop());
            LOGGER.info("{} took a clean plate. Remaining: {}", chef.getName(), cleanPlateStack.size());
        }
    }
}