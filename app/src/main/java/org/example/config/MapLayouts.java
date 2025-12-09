package org.example.config;

// Class statis untuk menyimpan layout peta.
public class MapLayouts {
    // Dimensi: 14 kolom (x) x 10 baris (y)
    public static final int MAP_WIDTH = 14;
    public static final int MAP_HEIGHT = 10;
    
    // Layout Burger Map (MAP TYPE C)
    public static final String[] BURGER_MAP_LAYOUT = {
        //   1  2  3  4  5  6  7  8  9 10 11 12 13 14 (X-Axis)
        "XXXXXAAIAAXXXX", // 1
        "C..XXA...AX..A", // 2
        "I..XXRV..RXX.P", // 3 (V=Spawn Chef Point)
        "C..... .... ..S", // 4
        "I.......... ..S", // 5
        "C..XXR..VRXX.A", // 6 (V=Spawn Chef Point)
        "I..AXA...AX..A", // 7
        "AWW A X A...AX..A", // 8
        "XXXXXAAIAAX..T", // 9
        "XXXXXXXXXXXXXX"  // 10
    };
    
    // Simbol penting:
    // C=Cutting, R=Cooking, A=Assembly, S=Serving, W=Washing, 
    // I=Ingredient Storage, P=Plate Storage, T=Trash, X=Wall, .=Walkable, V=Spawn
}