package items;

import items.ingredients.Ingredient;
import items.ingredients.IngredientType;

public class IngredientFactory {
    
    public static Ingredient createIngredient(IngredientType type) {
        return new Ingredient(type);
    }
}