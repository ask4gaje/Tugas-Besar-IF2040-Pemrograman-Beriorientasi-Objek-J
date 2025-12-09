package org.example.map;

import org.example.Position;
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
        // Tile ini tidak bisa dilalui Chef
        return false; 
    }

    @Override
    public Item getItemOnTile() {
        // Dinding tidak dapat menampung item di atasnya
        return null; 
    }

    @Override
    public void setItemOnTile(Item item) {
        // Jika Anggota 2 mencoba menaruh item di dinding,
        // kita bisa melempar Exception atau log Warning.
        // throw new UnsupportedOperationException("Tidak dapat menaruh item di dinding.");
    }
}