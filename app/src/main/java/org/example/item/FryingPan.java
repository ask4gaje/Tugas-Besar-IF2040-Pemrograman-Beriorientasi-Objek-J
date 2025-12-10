package org.example.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FryingPan extends KitchenUtensil implements CookingDevice {
    private boolean isCooking;

    public FryingPan() {
        super("Frying Pan");
        this.isCooking = false;
    }

    @Override
    public boolean isPortable() {
        return true; 
    }

    @Override
    public int capacity() {
        return 1; 
    }

    @Override
    public boolean canAccept(Ingredient ingredient) {
        if (ingredient instanceof Ingredient) {
            Ingredient i = (Ingredient) ingredient;
            return i.getType() == IngredientType.MEAT && !isCooking && contents.isEmpty();
        }
        return false;
    }

    @Override
    public void addIngredient(Ingredient ingredient) {
        if (canAccept((Ingredient) ingredient)) {
            this.contents.add(ingredient);
        }
    }

    @Override
    public void startCooking() {
        if (!contents.isEmpty()) {
            this.isCooking = true;
        }
    }
    
    public void stopCooking() {
        this.isCooking = false;
    }

    public boolean isCooking() {
        return isCooking;
    }
}