package org.example.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IngredientFactory {
    
    public static Ingredient createIngredient(IngredientType type) {
        return new Ingredient(type);
    }
}