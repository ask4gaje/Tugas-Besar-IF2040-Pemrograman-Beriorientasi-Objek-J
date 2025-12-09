package org.example.map.station;

import org.example.Position;
import org.example.item.Item;

import java.util.Stack; // Stack adalah pilihan yang baik untuk tumpukan piring

public class PlateStorage extends AbstractStation {
    // Menggunakan Collections (Stack) untuk tumpukan piring.
    // Anggota 3 akan menggunakan ini untuk tumpukan kotor/bersih
    private final Stack<Item> plateStack = new Stack<>(); 
    
    public PlateStorage(Position position) {
        super(position);
        // Pada inisialisasi, Plate Storage harus diisi dengan plate bersih awal (4 Plate)
        // plateStack.push(new CleanPlate()); // Anggota 2/3 yang membuat Plate.java
    }

    public Stack<Item> getPlateStack() {
        return plateStack;
    }
    // Anggota 3 akan mengimplementasikan logika stack piring kotor/bersih yang kompleks.
}