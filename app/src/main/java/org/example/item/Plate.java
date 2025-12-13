package org.example.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Plate extends KitchenUtensil {
    private boolean isClean;
    private Dish currentDish; 

    public Plate() {
        super("Plate");
        this.isClean = true; 
        this.currentDish = null;
    }

    public boolean isClean() {
        return isClean;
    }

    public void markAsDirty() {
        this.isClean = false;
        this.name = "Dirty Plate";
        this.currentDish = null;
        this.contents.clear();
    }

    public void clean() {
        this.isClean = true;
        this.name = "Plate";
    }

    public void addDishComponent(Ingredient ingredient) {
        if (!isClean) return; 
        if (currentDish == null) {
            currentDish = new Dish();
        }
        currentDish.addIngredient(ingredient);
        this.contents.add(ingredient);
        String finalDishName = determineFinalDishName();
        // If not a final dish, show generic Plate with contents description
        this.name = finalDishName != null ? finalDishName : "Plate with " + currentDish.getName(); // Set to "Classic Burger"
    }

    private String determineFinalDishName() {
        if (currentDish == null) return null;

        // Classic Burger: Bun + Cooked Meat, and nothing else
        boolean hasBun = currentDish.contains(IngredientType.BUN);
        boolean hasCookedMeat = false;
        boolean hasChoppedCheese = false;
        boolean hasChoppedTomatoes = false;
        boolean hasChoppedLettuce = false;

        for (Ingredient i : currentDish.getComponents()) {
            if (i.getType() == IngredientType.MEAT && i.getState() == IngredientState.COOKED) {
                hasCookedMeat = true;
            }
            if (i.getType() == IngredientType.CHEESE && i.getState() == IngredientState.CHOPPED) {
                hasChoppedCheese = true;
            }
            if (i.getType() == IngredientType.TOMATO && i.getState() == IngredientState.CHOPPED) {
                hasChoppedTomatoes = true;
            }
            if (i.getType() == IngredientType.LETTUCE && i.getState() == IngredientState.CHOPPED) {
                hasChoppedLettuce = true;
            }
        }

        if (currentDish.getComponents().size() == 2 && hasBun && hasCookedMeat) {
            return "Classic Burger Dish";
        }

        if (currentDish.getComponents().size() == 3 && hasBun && hasCookedMeat && hasChoppedCheese) {
            return "Cheese Burger Dish";
        }

        if (currentDish.getComponents().size() == 4 && hasBun && hasCookedMeat && hasChoppedTomatoes && hasChoppedLettuce) {
            return "BLT Burger Dish";
        }

        if (currentDish.getComponents().size() == 4 && hasBun && hasCookedMeat && hasChoppedCheese && hasChoppedLettuce) {
            return "Deluxe Burger Dish";
        }
        return null;
    }

    public Dish getDish() {
        return currentDish;
    }

    @Override
    public boolean canAccept(Ingredient ingredient) {
        return false;
    }

    @Override
    public void addIngredient(Ingredient ingredient) {

    }
}