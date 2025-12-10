package org.example.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum IngredientType {
    BUN("Roti", true),      
    MEAT("Daging", false),  
    CHEESE("Keju", true),   
    LETTUCE("Lettuce", true),
    TOMATO("Tomat", true);

    public final String label;
    public final boolean edibleRawOrChopped; 

    IngredientType(String label, boolean edibleRawOrChopped) {
        this.label = label;
        this.edibleRawOrChopped = edibleRawOrChopped;
    }
}