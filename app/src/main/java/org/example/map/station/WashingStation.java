package org.example.map.station;

import org.example.chef.Chef;
import org.example.chef.Position;
import org.example.item.Item;
import org.example.item.Plate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedList; 

public class WashingStation extends AbstractStation {
    private static final Logger LOGGER = LoggerFactory.getLogger(WashingStation.class);
    private final LinkedList<Item> dirtyPlates = new LinkedList<>(); 
    private final LinkedList<Item> cleanPlates = new LinkedList<>();
    
    public WashingStation(Position position) {
        super(position);
    }

    @Override
    public void interact(Chef chef) {
        // 1. Chef cuci piring (Busy State)
        if (chef.getInventory() == null && !dirtyPlates.isEmpty()) {
            // Start Thread Cuci
            LOGGER.info("{} started washing...", chef.getName());
            chef.performLongAction(3, () -> {
                Item washed = dirtyPlates.poll();
                if (washed instanceof Plate) {
                    ((Plate) washed).clean();
                    cleanPlates.add(washed);
                    LOGGER.info("Plate cleaned!");
                }
            });
            return;
        }
        
        // 2. Chef ambil piring bersih
        if (chef.getInventory() == null && !cleanPlates.isEmpty()) {
            chef.setInventory(cleanPlates.poll());
            LOGGER.info("{} took clean plate from sink.", chef.getName());
        }
    }

    @Override
    public void pickUp(Chef chef) {
        if (chef.getInventory() instanceof Plate) {
            Plate p = (Plate) chef.getInventory();
            if (!p.isClean()) {
                chef.dropItem();
                dirtyPlates.add(p);
                LOGGER.info("{} put dirty plate in sink.", chef.getName());
                return;
            }
        }
    }
}