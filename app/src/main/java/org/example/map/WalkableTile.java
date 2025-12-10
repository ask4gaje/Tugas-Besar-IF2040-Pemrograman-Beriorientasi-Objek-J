package org.example.map;

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
        // Tile ini adalah ruang yang bisa dilalui
        return true; 
    }
    
    @Override
    public Item getItemOnTile() { return itemOnTile; }

    @Override
    public void setItemOnTile(Item item) { this.itemOnTile = item; }
}
