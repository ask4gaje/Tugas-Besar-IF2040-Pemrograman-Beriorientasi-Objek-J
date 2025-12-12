package org.example.map;

import org.example.chef.Chef;
import org.example.chef.Position;
import org.example.item.Item;

import org.example.map.station.WashingStation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WalkableTile implements Tile {
    private final Position position;
    private Item itemOnTile; // Dapat menampung Item

    private static final Logger LOGGER = LoggerFactory.getLogger(WashingStation.class);

    public WalkableTile(Position position) {
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public boolean isWalkable() {
        return true; 
    }
    
    @Override
    public Item getItemOnTile() { return this.itemOnTile; }

    @Override
    public void setItemOnTile(Item item) { this.itemOnTile = item; }

    public void interact(Chef chef) {}

    @Override
    public void pickUp(Chef chef) {
        if (chef.getInventory() == null && this.itemOnTile != null) {
            chef.setInventory(this.itemOnTile);
            this.itemOnTile = null;
            LOGGER.info("{} took item from floor.", chef.getName());
        }
        else if (chef.getInventory() != null && this.itemOnTile == null) {
            this.itemOnTile = chef.dropItem();
            LOGGER.info("{} dropped an item.", chef.getName());
        }
    }
}
