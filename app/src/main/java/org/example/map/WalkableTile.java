package org.example.map;

import org.example.chef.Chef;
import org.example.chef.Position;
import org.example.item.Item;

public class WalkableTile implements Tile {
    private final Position position;
    private Item itemOnTile; // Dapat menampung Item

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

    public void interact(Chef chef) {
        if (chef.getInventory() == null && this.itemOnTile != null) {
            chef.setInventory(this.itemOnTile);
            this.itemOnTile = null;
            // Logger...
        }
        else if (chef.getInventory() != null && this.itemOnTile == null) {
            this.itemOnTile = chef.dropItem();    
            // Logger...
        }
    }
}
