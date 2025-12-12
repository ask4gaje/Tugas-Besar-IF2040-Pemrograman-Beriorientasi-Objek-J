package org.example.map;

import org.example.chef.Chef;
import org.example.chef.Position;
import org.example.item.Item;

public class WallTile implements Tile {
    private final Position position;
    
    public WallTile(Position position) {
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public boolean isWalkable() {
        return false; 
    }

    @Override
    public Item getItemOnTile() {
        return null; 
    }

    @Override
    public void setItemOnTile(Item item) {
        System.out.println("Tidak bisa menaruh item di tembok!");
    }

    @Override
    public void interact(Chef chef) {}

    @Override
    public void pickUp(Chef chef) {}
}