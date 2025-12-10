package org.example.map.station;

import org.example.chef.Position;
import org.example.item.Item;

public class TrashStation extends AbstractStation {
    public TrashStation(Position position) {
        super(position);
    }
    
    // Logika untuk membuang item. Item yang ditaruh akan langsung hilang.
    @Override
    public void setItemOnTile(Item item) {
        if (item != null) {
            // Item dibuang
            System.out.println("Item thrown away: " + item.toString());
        }
        // itemOnTile tidak pernah diisi di TrashStation, selalu null setelah aksi
        this.itemOnTile = null; 
    }
    
    // Item tidak dapat diambil dari TrashStation
    @Override
    public Item getItemOnTile() {
        return null;
    }
    
    // Tambahan: Method eksplisit untuk membuang item yang dibawa oleh pemain/chef
    public void trashItem(Item itemToTrash) {
        if (itemToTrash != null) {
            System.out.println("Item trashed by player: " + itemToTrash.toString());
        }
    }
}