package org.example.map.station;

import org.example.chef.Chef;
import org.example.chef.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssemblyStation extends AbstractStation {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssemblyStation.class);

    public AssemblyStation(Position position) {
        super(position);
    }

    @Override
    public void interact(Chef chef) {
        // Kasus 1: Station Kosong, Chef Bawa Item -> Taruh
        if (itemOnTile == null && chef.getInventory() != null) {
            this.itemOnTile = chef.dropItem();
            LOGGER.info("{} placed {} on Assembly Station.", chef.getName(), itemOnTile.getName());
        }
        // Kasus 2: Station Ada Item, Chef Kosong -> Ambil
        else if (itemOnTile != null && chef.getInventory() == null) {
            chef.setInventory(this.itemOnTile);
            this.itemOnTile = null;
            LOGGER.info("{} took item from Assembly Station.", chef.getName());
        }
        // Kasus 3: Keduanya Ada Item -> Coba Gabung (Plating/Assembly)
        // Kita simulasikan dengan: Chef mengambil item di meja (akan mentrigger logic combine di inventory chef)
        else if (itemOnTile != null && chef.getInventory() != null) {
            // Simpan referensi sementara
            org.example.item.Item itemOnTable = this.itemOnTile;
            
            // Coba masukkan ke inventory chef (method setInventory chef punya logic combine)
            chef.setInventory(itemOnTable);
            
            // Jika inventory chef sekarang memegang hasil gabungan (atau masih item lama tapi sukses combine)
            // Maka item di meja hilang. 
            // Namun, karena setInventory chef logic-nya void dan agak kompleks,
            // untuk M2 kita sederhanakan: Swap tidak didukung dulu, atau anggap berhasil ambil.
            
            // Refinement: Jika chef.setInventory berhasil combine, kita harus null-kan meja.
            // Tapi karena method setInventory void, kita asumsikan Chef mengambilnya.
            this.itemOnTile = null; 
        }
    }
}