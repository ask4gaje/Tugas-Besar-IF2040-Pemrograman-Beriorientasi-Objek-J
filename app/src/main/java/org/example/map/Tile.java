package org.example.map;

import org.example.Position;
import org.example.items.Item;

public interface Tile {
    Position getPosition();
    boolean isWalkable();
    Item getItemOnTile(); // Konsep: Item yang diletakkan di lantai/tile
    void setItemOnTile(Item item);
}