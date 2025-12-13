package org.example.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum IngredientType {
    BUN("Roti", true, "r"),
    MEAT("Daging", false, "d"),
    CHEESE("Keju", true, "k"),
    LETTUCE("Lettuce", true, "l"),
    TOMATO("Tomat", true, "t");

    public final String label;
    public final boolean edibleRawOrChopped;
    public final String abbreviation;

    IngredientType(String label, boolean edibleRawOrChopped, String abbreviation) {
        this.label = label;
        this.edibleRawOrChopped = edibleRawOrChopped;
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}