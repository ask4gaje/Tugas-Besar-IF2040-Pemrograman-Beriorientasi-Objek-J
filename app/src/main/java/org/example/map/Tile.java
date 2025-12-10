package org.example.map;

import org.example.chef.Position;
import org.example.item.Item;

public interface Tile {
    Position getPosition();
    boolean isWalkable();
    Item getItemOnTile(); // Konsep: Item yang diletakkan di lantai/tile
    void setItemOnTile(Item item);
}