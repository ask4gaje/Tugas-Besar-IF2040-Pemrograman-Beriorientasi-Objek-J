package org.example.item;

public enum IngredientType {
    BUN, MEAT, CHEESE, LETTUCE, TOMATO, 
    // Anda bisa menambahkan logic untuk mapping posisi ke tipe ingredient di sini, 
    // atau Anggota 2/3 yang menentukan mapping tersebut.
    UNKNOWN; 
    
    // Method placeholder agar GameMap.java bisa menggunakan IngredientType.get(x, y)
    public static IngredientType get(int x, int y) {
        // Anggota tim harus koordinasi untuk menentukan tipe ingredient di posisi mana.
        // Untuk kompilasi, kembalikan UNKNOWN atau salah satu tipe.
        if (x == 5 && y == 1) return BUN; 
        if (x == 5 && y == 7) return MEAT;
        return UNKNOWN; 
    }
}
