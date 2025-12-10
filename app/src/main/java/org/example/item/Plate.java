package items.utensils;

import items.dishes.Dish;

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

    public void addDishComponent(items.ingredients.Ingredient ingredient) {
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
}