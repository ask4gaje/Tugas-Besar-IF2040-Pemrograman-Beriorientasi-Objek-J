package org.example.items;

public class IngredientFactory {
    
    public static Ingredient createIngredient(IngredientType type) {
        return new Ingredient(type);
    }
}