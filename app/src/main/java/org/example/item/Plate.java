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
        this.name = "Plate with " + currentDish.getName();
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