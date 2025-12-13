package org.example.map.station;

import org.example.chef.Position;
import org.example.item.Item;
import org.example.map.Tile;
import org.example.chef.Chef;

public abstract class AbstractStation implements Tile {
    protected Position position;
    protected Item itemOnTile; // Tempat menaruh item di atas station

    public AbstractStation(Position position) {
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public boolean isWalkable() {
        // Station umumnya tidak bisa diinjak
        return false;
    }

    @Override
    public Item getItemOnTile() {
        return itemOnTile;
    }

    @Override
    public void setItemOnTile(Item item) {
        this.itemOnTile = item;
    }

    @Override
    public abstract void interact(Chef chef);

    @Override
    public abstract void pickUp(Chef chef);
}