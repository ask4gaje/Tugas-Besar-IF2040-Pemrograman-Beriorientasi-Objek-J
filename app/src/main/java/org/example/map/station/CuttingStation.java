package org.example.map.station;

import org.example.chef.Chef;
import org.example.chef.Position;
import org.example.item.Item;

public class CuttingStation extends AbstractStation {
    private static final int MAX_CUTTING_PROGRESS = 3; // Contoh: Perlu 3 kali aksi untuk selesai
    private int currentCuttingProgress = 0;
    private boolean isBusy = false;

    public CuttingStation(Position position) {
        super(position);
    }

    public boolean isBusy() {
        return isBusy;
    }

    public int getCurrentCuttingProgress() {
        return currentCuttingProgress;
    }

    public void startCutting(Item item) {
        if (itemOnTile != null && !isBusy) {
            // Asumsi itemOnTile adalah item yang akan di-cut
            this.isBusy = true;
            this.currentCuttingProgress = 0;
            // Di sini, Anda mungkin ingin melakukan validasi apakah item bisa di-cut
            System.out.println("Cutting started on: " + itemOnTile.toString());
        }
    }

    public Item performCutAction() {
        if (isBusy && currentCuttingProgress < MAX_CUTTING_PROGRESS) {
            currentCuttingProgress++;
            System.out.println("Cutting progress: " + currentCuttingProgress + "/" + MAX_CUTTING_PROGRESS);
            if (currentCuttingProgress >= MAX_CUTTING_PROGRESS) {
                // Selesai di-cut.
                // Anggota 3 akan menambahkan logika untuk mengganti itemOnTile dengan versi yang sudah di-cut
                Item finishedItem = itemOnTile; // Placeholder: seharusnya ini adalah item baru (e.g., ChoppedTomato)
                this.itemOnTile = null; // Item selesai dan siap diambil
                this.isBusy = false;
                this.currentCuttingProgress = 0;
                System.out.println("Cutting finished.");
                return finishedItem;
            }
        }
        return null;
    }

    @Override
    public boolean isWalkable() {
        return false; // Tetap tidak bisa diinjak
    }

    @Override
    public void interact(Chef chef) {
        // Taruh item
        if (itemOnTile == null && chef.getInventory() != null) {
        this.itemOnTile = chef.dropItem();
        }
        // Proses item
        else if (itemOnTile != null && chef.getInventory() == null) {
        // Logic cek ingredient dan start cutting thread...
        // chef.performLongAction(...)
    }
}
}